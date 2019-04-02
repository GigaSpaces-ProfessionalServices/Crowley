package com.gigaspaces.mq.spacelistener;

import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.mq.common.XetraStockMarketTrade;
import com.j_spaces.core.client.SQLQuery;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denys_Novikov
 * Date: 3/19/19
 */
public class XetraStockMarketTradeHandler implements ReplicationHandler<XetraStockMarketTrade> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS");

    @Override
    public XetraStockMarketTrade createNewInstance(List parameters) throws ParseException {

        return new XetraStockMarketTrade.XetraStockMarketTradeBuilder()
                .setIsin(parameters.get(0).toString())
                .setMnemonic(parameters.get(1).toString())
                .setSecurityDesc(parameters.get(2).toString())
                .setSecurityType(parameters.get(3).toString())
                .setCurrency(parameters.get(4).toString())
                .setSecurityID(Integer.parseInt(parameters.get(5).toString()))
                .setDateTimeTrade(new Timestamp(DATE_FORMAT.parse(parameters.get(6).toString()).getTime()))
                .setStartPrice(Double.parseDouble(parameters.get(7).toString()))
                .setMaxPrice(Double.parseDouble(parameters.get(8).toString()))
                .setMinPrice(Double.parseDouble(parameters.get(9).toString()))
                .setEndPrice(Double.parseDouble(parameters.get(10).toString()))
                .setTradedVolume((int)Double.parseDouble(parameters.get(11).toString()))
                .setNumberOfTrades((int)Double.parseDouble(parameters.get(12).toString()))
                .build();
    }

    @Override
    public SQLQuery<XetraStockMarketTrade> createDeleteQuery(List parameters) {
        String querystr = "securityID = '" + parameters.get(18).toString() + "'";
        return new SQLQuery<>(XetraStockMarketTrade.class, querystr);

    }

    @Override
    public UpdatePair getUpdate(List receivedList) throws ParseException {
        String querystr = "securityID = '" + String.valueOf(receivedList.get(5).toString()) + "'";
        return new UpdatePair(new SQLQuery(XetraStockMarketTrade.class, querystr), createChangeSetForTrade(receivedList));
    }

    @Override
    public ArrayList spliter(String toSplit) {

        String[] splitedStr = toSplit.split("");

        List<String> resultData = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        boolean containsOpenQuote = false;
        for (String symbol : splitedStr) {

            if (symbol.matches("\"") && !containsOpenQuote) {
                containsOpenQuote = true;
            } else if (symbol.matches("\"") && containsOpenQuote) {
                containsOpenQuote = false;
            } else if (",".equals(symbol) && !containsOpenQuote) {
                resultData.add(currentPart.toString());
                currentPart = new StringBuilder();
            } else {
                currentPart.append(symbol);
            }
        }
        resultData.add(currentPart.toString());


        splitedStr = resultData.toArray(new String[0]);

        int countstrt = (splitedStr.length - 12) / 2;
//        System.out.println("No. of columns:" + countstrt);
        int i = 1;
        ArrayList al = new ArrayList();
        for (int countup = splitedStr.length - countstrt; countup < splitedStr.length; countup++) {
//            System.out.println("["+i+"]"+splitedStr[countup]);
            al.add(splitedStr[countup]);
            i++;

        }

        return al;
    }

    private ChangeSet createChangeSetForTrade(List receivedList) throws ParseException {

        return new ChangeSet()
                .set("isin", receivedList.get(0).toString())
                .set("mnemonic", receivedList.get(1).toString())
                .set("securityDesc", receivedList.get(2).toString())
                .set("securityType", receivedList.get(3).toString())
                .set("currency", receivedList.get(4).toString())
                .set("dateTimeTrade", new Timestamp(DATE_FORMAT.parse(receivedList.get(6).toString()).getTime()))
                .set("startPrice", Double.parseDouble(receivedList.get(7).toString()))
                .set("maxPrice", Double.parseDouble(receivedList.get(8).toString()))
                .set("minPrice", Double.parseDouble(receivedList.get(9).toString()))
                .set("endPrice", Double.parseDouble(receivedList.get(10).toString()))
                .set("tradedVolume", (int)Double.parseDouble(receivedList.get(11).toString()))
                .set("numberOfTrades", (int)Double.parseDouble(receivedList.get(12).toString()));
    }
}
