package com.gigaspaces;

import com.gigaspaces.mq.common.XetraStockMarketTrade;
import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.OperationsBatchData;
import com.gigaspaces.sync.SpaceSynchronizationEndpoint;
import com.j_spaces.core.IJSpace;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.sql.*;

/**
 * @author Denys_Novikov
 * Date: 3/22/19
 */
public class MirrorSpaceSynchronizationEndpoint extends SpaceSynchronizationEndpoint {

    private static final long WEEK_IN_MILLIS = 1000 * 60 * 60 * 24 * 7;
    private static GigaSpace gigaspace;

    static {
        IJSpace space = new UrlSpaceConfigurer("jini://*/*/insightedge-space").lookupGroups("xap-14.2.0").lookupTimeout(20000).space();
        gigaspace = new GigaSpaceConfigurer(space).gigaSpace();
        System.out.println("----->> Space found");
    }

    @Override
    public void onOperationsBatchSynchronization(OperationsBatchData batchData) {

        Connection conn = null;

        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            conn = DriverManager.getConnection("jdbc:hive2://127.0.0.1:10000/hivedb", "hduser", "hduser");
            PreparedStatement preparedStatement = conn.prepareStatement("insert into XetraStockMarketTrade(isin,mnemonic,securityDesc," +
                    "securityType,currency,securityId,dateTimeTrade,startPrice,maxPrice,minPrice,endPrice,tradedVolume,numberOfTrades) " +
                    "values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

            System.out.println("----->> Driver found");


            for (DataSyncOperation operation : batchData.getBatchDataItems()) {
                switch (operation.getDataSyncOperationType()) {
                    case WRITE:
                        System.out.println("----->> Inside write");
                        XetraStockMarketTrade newTrade = (XetraStockMarketTrade)operation.getDataAsObject();


                        if (newTrade.getDateTimeTrade().before(new Timestamp(System.currentTimeMillis() - WEEK_IN_MILLIS))) {

                            System.out.println("----->> Preparing db object");
                            // add to db
                            preparedStatement.setString(1, newTrade.getIsin());
                            preparedStatement.setString(2, newTrade.getMnemonic());
                            preparedStatement.setString(3, newTrade.getSecurityDesc());
                            preparedStatement.setString(4, newTrade.getSecurityType());
                            preparedStatement.setString(5, newTrade.getCurrency());
                            preparedStatement.setInt(6, newTrade.getSecurityID());
                            preparedStatement.setDate(7, new Date(newTrade.getDateTimeTrade().getTime()));
                            preparedStatement.setDouble(8, newTrade.getStartPrice());
                            preparedStatement.setDouble(9, newTrade.getMaxPrice());
                            preparedStatement.setDouble(10, newTrade.getMinPrice());
                            preparedStatement.setDouble(11, newTrade.getEndPrice());
                            preparedStatement.setInt(12, newTrade.getTradedVolume());
                            preparedStatement.setInt(13, newTrade.getNumberOfTrades());
                            preparedStatement.execute();

                            System.out.println("----->> remove from space");
                            gigaspace.clear(newTrade);
                            System.out.println("----->> Done!!!");

                        }
                        break;
                    case UPDATE:
                        // entry was updated
                        break;
                    case REMOVE:
                        // entry was removed
                        break;
                    case CHANGE:
                        // entry was modified using change API
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
