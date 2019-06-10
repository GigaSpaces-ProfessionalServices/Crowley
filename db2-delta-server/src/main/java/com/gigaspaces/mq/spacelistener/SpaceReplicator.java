package com.gigaspaces.mq.spacelistener;

import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.mq.common.Employee;
import com.j_spaces.core.IJSpace;
import net.jini.core.lease.Lease;
import org.apache.log4j.Logger;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.core.space.mode.PostPrimary;
import org.openspaces.events.SpaceDataEventListener;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.SimpleNotifyContainerConfigurer;
import org.openspaces.events.notify.SimpleNotifyEventListenerContainer;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SpaceReplicator implements SpaceDataEventListener<Employee> {

    private String rowid;
    private String textToParse;
    private String action;
    private String tableName;
    static Logger logger;
    private String deleteId;

    static IJSpace space = new UrlSpaceConfigurer("jini://*/*/insightedge-space").lookupGroups("xap-14.2.0").lookupTimeout(20000).space();
    static GigaSpace gigaSpace = new GigaSpaceConfigurer(space).gigaSpace();

    private static Long TIMOUT_FOR_SPACE_WRITE = 60000L;

    private static final Map<String, ReplicationHandler> HANDLER_MAP = new HashMap<>();

    public SpaceReplicator(String text) {

        this.textToParse = text;
        String[] receivedText = text.split("\",\"");
        this.tableName = receivedText[4];
        this.action = receivedText[5];


        HANDLER_MAP.put("EMPLOYEE", new EmployeeHandler());
        HANDLER_MAP.put("XETRA_STOCK_TRADE_DATA", new XetraStockMarketTradeHandler());

    }


    private ArrayList spliter(String toSplit) {

        ArrayList al = new ArrayList();

        String[] splitedStr = toSplit.split(",");


        int countstrt = (splitedStr.length - 12) / 2;
        System.out.println("No. of columns:" + countstrt);
        int i = 1;
        for (int countup = splitedStr.length - countstrt; countup < splitedStr.length; countup++) {
            System.out.println("["+i+"]"+splitedStr[countup]);
            al.add(splitedStr[countup]);
            i++;

        }

        return al;
    }

    public void spaceConnection() {


    }

    public void processMessage() {

        if (gigaSpace == null) {
            throw new IllegalStateException("gigaSpace cannot be null");
        }

        try {

            ReplicationHandler handler = HANDLER_MAP.get(tableName);
            ArrayList receivedList = handler.spliter(this.textToParse);

            if (this.action.equals("DLET")) {

                System.out.println("Delete Operation Detected");
                ArrayList delId = new ArrayList();

                String[] splitedStr = this.textToParse.split(",");
                for (int i = 0; i < splitedStr.length; i++) {

                    delId.add(splitedStr[i]);

                }

                gigaSpace.clear(handler.createDeleteQuery(delId));

            } else if (this.action.equals("ISRT")) {

                System.out.println("Insert Operation Detected");
                gigaSpace.write(handler.createNewInstance(receivedList), Lease.FOREVER, TIMOUT_FOR_SPACE_WRITE, WriteModifiers.WRITE_ONLY);

            } else if (this.action.equals("REPL")) {

                System.out.println("Update Operation Detected");
                UpdatePair pair = handler.getUpdate(receivedList);
                gigaSpace.change(pair.getKey(), pair.getValue());

            }

        } catch (Exception e) {

            e.printStackTrace();
            logger.info("Error in Updating Cache" + e.toString());

        }


    }


    @PostPrimary
    public static void listen() {

        SimpleNotifyEventListenerContainer
                notifyEventListenerContainer = new SimpleNotifyContainerConfigurer(gigaSpace)
                .template(new Employee())
                .eventListenerAnnotation(new Object() {
                    @SpaceDataEvent
                    public void eventHappened(Employee event) {


                        logger.debug("Detected SPACE EVENT : " + event);
                    }
                }).notifyContainer();

        notifyEventListenerContainer.notify();
        notifyEventListenerContainer.start();


        logger.debug("notifyEventListenerContainer " + notifyEventListenerContainer.getTransactionManagerName());
    }

    public void setRowId(String rowId) {
        this.rowid = rowId;
    }

    public String getRowId() {
        return rowid;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    @Override
    public void onEvent(Employee arg0, GigaSpace arg1, TransactionStatus arg2,
                        Object arg3) {
        // TODO Auto-generated method stub
        //System.out.println("onEvent=======");
    }

}
