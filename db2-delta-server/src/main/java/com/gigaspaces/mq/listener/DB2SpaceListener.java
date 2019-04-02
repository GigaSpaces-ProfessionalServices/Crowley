package com.gigaspaces.mq.listener;


import com.gigaspaces.mq.common.Utils;

import javax.jms.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class DB2SpaceListener {

    public static final Pattern DATE_PATTERN =
            Pattern.compile("^(\\d){4}(\\D)(\\d){2}(\\D)(\\d){2}(\\D)(\\d)" +
                    "{2}(\\D)(\\d){2}(\\D)(\\d){2}(\\D(\\d*))?$");

    public static int tableNameIndex = 5;
    public static int operationIndex = 6;
    public static int segmentIndex = 11;

    public static String queueManager = null;
    public static String queue = null;
    public static long runTime;
    public static String outPath = null;
    public static File outDir = null;
    public static File stageDir = null;
    public static File batchDir = null;

    public static String outColDelimHexString = "1E";
    public static String outRecordDelimHexString = "0A";
    public static char outColDelim;
    public static char outRecordDelim;

    public static String inColDelimHexString = "2C";
    public static String inStringDelimHexString = "22";
    public static String inRecordDelimHexString = "0A";
    public static char inColDelim;
    public static char inStringDelim;

    public static boolean stage;


    public DB2SpaceListener(String qManName, String qName,
                            long listenFor, String outPath, String outCol,
                            String outRecord, String inCol, String inString,
                            String inRecord, int tableIdx, int opIdx, int segIdx) {
        this.queueManager = qManName;
        this.queue = qName;
        this.runTime = listenFor;
        this.outPath = outPath;
        this.outColDelimHexString = outCol;
        this.outRecordDelimHexString = outRecord;
        this.inColDelimHexString = inCol;
        this.inStringDelimHexString = inString;
        this.inRecordDelimHexString = inRecord;
        this.tableNameIndex = tableIdx;
        this.operationIndex = opIdx;
        this.segmentIndex = segIdx;
    }

    public static void main(String[] args) throws JMSException, FileNotFoundException {

        Utils ut = new Utils();
        //Read .properties file's specified parameter
        //Required Value
        String QmanagerName = ut.getProps().getProperty("QmanagerName");
        String Qname = ut.getProps().getProperty("Qname");
        String dirPath = ut.getProps().getProperty("dirPath");

        //Optional Values
        long listenTime = Long.parseLong(ut.getProps().getProperty("listenTime"));
        String outputColDel = ut.getProps().getProperty("outputColDel");
        String outputRecordSeprator = ut.getProps().getProperty("outputRecordSeprator");
        String inputColDel = ut.getProps().getProperty("inputColDel");
        String inpurStrDel = ut.getProps().getProperty("inpurStrDel");
        String inputRecordSeprator = ut.getProps().getProperty("inputRecordSeprator");

        int tableIdx = 5;
        int opIdx = 6;
        int segIdx = 11;

        //If optional values are not set in .properties file then default values will set from here
        if (listenTime == 0)
            listenTime = 300000;
        if (outputColDel == null)
            outputColDel = "1E";
        if (outputRecordSeprator == null)
            outputRecordSeprator = "0A";
        if (inputColDel == null)
            inputColDel = "2C";
        if (inpurStrDel == null)
            inpurStrDel = "22";
        if (inputRecordSeprator == null)
            inputRecordSeprator = "0A";

        if ((QmanagerName == null) || (Qname == null) || (dirPath == null)) { //If Required parameter is null
            printInstructions();    //Show some basic Instructions
            System.exit(0);
        } else {
            System.out.println(" ");
            System.out.println("The program is starting:");
            System.out.println(" WebSphere MQ queue manager: " + QmanagerName);
            System.out.println(" Local queue               : " + Qname);
            System.out.println(" Output directory          : " + dirPath);
            System.out.println(" Time (ms)                 : " + listenTime);
            System.out.println(" Output record separator   : " + outputRecordSeprator);
            System.out.println(" Output column delimiter   : " + outputColDel);
            System.out.println(" Input  column delimiter   : " + inputColDel);
            System.out.println(" Input  string delimiter   : " + inpurStrDel);
            System.out.println(" Input  record separator   : " + inputRecordSeprator);
        }

        DB2SpaceListener SL = new DB2SpaceListener(QmanagerName, Qname, listenTime,
                dirPath, outputColDel, outputRecordSeprator, inputColDel, inpurStrDel, inputRecordSeprator,
                tableIdx, opIdx, segIdx);

        SL.localSetting();
        SL.connectToQueueforListen();
    }

    /*
     * If parameter sets Wrong then printInstructions() method will call.
     *
     */

    private static void printInstructions() {
        System.out.println("Check your .properties file");
        System.out.println("Also QmanagerName and Local Qname  and dirName values");

        System.out.println(" These are the optional parameters: ");
        System.out.println("  -listenTime      <Duration in ms. Default is 28500> ");
        System.out.println("  -outputRecordSeprator <Output record separator. Default is 0A - ASCII new line> ");
        System.out.println("  -outputColDel    <Output column delimiter. Default is 1E - ASCII record separator> ");
        System.out.println("  -inputColDel     <Input column delimiter.  Default is 2C - ASCII comma> ");
        System.out.println("  -inpurStrDel  <Input string delimiter.  Default is 22 - ASCII double quotation mark> ");
        System.out.println("  -inputRecordSeprator  <Input record separator.  Default is OA - ASCII new line character>. ");
    }


    /**
     * Set/Create directory path
     */
    public void localSetting() {

        inColDelim = (char) Integer.valueOf(inColDelimHexString, 16).intValue();
        inStringDelim = (char) Integer.valueOf(inStringDelimHexString, 16).intValue();
        outColDelim = (char) Integer.valueOf(outColDelimHexString, 16).intValue();
        outRecordDelim = (char) Integer.valueOf(outRecordDelimHexString, 16).intValue();
        outDir = new File(outPath);
        stageDir = new File(outDir + File.separator + "stage");
        batchDir = new File(outDir + File.separator + Long.toString(System.currentTimeMillis()));

        if (!outDir.exists()) {
            if (!outDir.mkdir()) {
                System.out.println(" ");
                System.err.println(" Output directory does not exist and cannot be created.");
                System.out.println("The program is ending.");
                System.exit(1);
            }
        }
        if (!outDir.canWrite()) {
            if (!outDir.setWritable(true)) {
                System.out.println(" ");
                System.err.println(" Output directory is not writeable.");
                System.out.println("The program is ending.");
                System.exit(1);
            }
        }
        if (!stageDir.exists()) {
            stageDir.mkdir();
        }
        if (!stageDir.canWrite()) {
            if (!stageDir.setWritable(true)) {
                System.out.println(" ");
                System.err.println(" Staging directory is not writeable.");
                System.out.println("The program is ending.");
                System.exit(1);
            }
        }
        if (stageDir.listFiles().length > 0) {
            stage = true;
        }

    }

    /*
     * Connect to Queue Manager and start listening Specified Local Queue
     * It will Listen for Specified time/if not Specify then as Default
     *
     */
    public void connectToQueueforListen() throws JMSException {
        try {
            com.ibm.mq.jms.MQQueueConnectionFactory factory = new com.ibm.mq.jms.MQQueueConnectionFactory();
            factory.setQueueManager(queueManager);

            QueueConnection connection;
            connection = factory.createQueueConnection();
            QueueSession session;
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue ioQueue;
            ioQueue = session.createQueue("queue://" + queueManager + "/" + queue);

            MessageConsumer messageConsumer = session.createConsumer(ioQueue);
            //Listener Set
            MQlistener listener = new MQlistener();
            messageConsumer.setMessageListener(listener);

            StopListen stopListen = new StopListen(connection);
            Timer timer = new Timer();
            long delay = runTime;
            timer.schedule(stopListen, delay);
            connection.start();



        } catch (JMSException je) {
            System.err.println(" ");
            System.err.println("Error:");
            System.err.println(je);
            Exception e = je.getLinkedException();
            if (e != null) {
                System.err.println("  ");
                System.err.println(e);
            } else {
                System.err.println("startListen: Caught exception but no linked exception found.");
            }
            System.out.println(" ");
            System.out.println("The program is ending.");
            System.exit(2);
        }
    }


    static class StopListen extends TimerTask {
        public QueueConnection connection;

        public StopListen(QueueConnection QConnection) {
            connection = QConnection;
        }

        /*
         * close MQ connection and cleanup: if files existed in the
         */
        public void run() {
            try {
                connection.close();
                if (stageDir.exists()) {
                    if (stageDir.listFiles().length == 0) {
                        boolean success = stageDir.delete();
                        if (!success) {
                            System.out.println(" StopListen.run: " +
                                    "Could not delete the (empty) stage directory");
                        }
                    }
                }
                System.out.println(" ");
                System.out.println("The program ended successfully.");
                System.exit(0);
            } catch (JMSException je) {
                System.err.println(" ");
                System.err.println("Error:");
                System.err.println(je);
                Exception e = je.getLinkedException();
                if (e != null) {
                    System.err.println(e);
                } else {
                    System.err.println("StopListen.run: Caught an exception but no linked exception found.");
                }
                System.out.println(" ");
                System.out.println("The program is ending.");
                System.exit(2);
            }
        }
    }
}
