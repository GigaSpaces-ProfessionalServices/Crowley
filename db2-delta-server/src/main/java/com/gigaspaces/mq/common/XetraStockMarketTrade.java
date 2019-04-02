package com.gigaspaces.mq.common;

//import com.gigaspaces.annotation.pojo.SpaceClass;

import com.gigaspaces.annotation.pojo.SpaceClass;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


/**
 * Trade data relating to stock market trades.
 * Represents an aggregate of one-minute of trade activity for each security,
 * following the Open/High/Low/Close (OHLC) format,
 * with the number of trades and traded contracts.
 *
 * @author Alexander_Koltsov
 */
@SpaceClass
public class XetraStockMarketTrade extends Trade {

    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Stock exchange ticker symbol
     */
    private String mnemonic;

    /**
     * Description of the security
     */
    private String securityDesc;

    /**
     * Total value traded
     */
    private Integer tradedVolume;

    public XetraStockMarketTrade() {
    }

    private XetraStockMarketTrade(String isin, String currency, String securityType,
                                  Integer securityID, Timestamp dateTimeTrade, Double startPrice, Double maxPrice,
                                  Double minPrice, Double endPrice, Integer numberOfTrades, String mnemonic,
                                  String securityDesc, Integer tradedVolume) {
        super(isin, currency, securityType, securityID, dateTimeTrade, startPrice, maxPrice, minPrice, endPrice, numberOfTrades);
        this.mnemonic = mnemonic;
        this.securityDesc = securityDesc;
        this.tradedVolume = tradedVolume;
    }

//    public static XetraStockMarketTrade parseFromCsv(CSVRecord csvRecord) {
//        Timestamp timestamp= null;
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date parsedDate = dateFormat.parse(String.format("%s %s:00", csvRecord.get("Date"), csvRecord.get("Time")));
//            timestamp = new java.sql.Timestamp(parsedDate.getTime());
//        } catch(Exception e) { //this generic but you can control another types of exception
//            e.printStackTrace();
//        }
//        return new XetraStockMarketTradeBuilder()
//                .setIsin(csvRecord.get("ISIN"))
//                .setMnemonic(csvRecord.get("Mnemonic"))
//                .setSecurityDesc(csvRecord.get("SecurityDesc"))
//                .setSecurityType(csvRecord.get("SecurityType"))
//                .setCurrency(csvRecord.get("Currency"))
//                .setSecurityID(getOrNull(Integer::parseInt, csvRecord.get("SecurityID")))
//                .setDateTimeTrade(timestamp)
//                .setStartPrice(getOrNull(Double::parseDouble, csvRecord.get("StartPrice")))
//                .setMaxPrice(getOrNull(Double::parseDouble, csvRecord.get("MaxPrice")))
//                .setMinPrice(getOrNull(Double::parseDouble, csvRecord.get("MinPrice")))
//                .setEndPrice(getOrNull(Double::parseDouble, csvRecord.get("EndPrice")))
//                .setTradedVolume(getOrNull(Integer::parseInt, csvRecord.get("TradedVolume")))
//                .setNumberOfTrades(getOrNull(Integer::parseInt, csvRecord.get("NumberOfTrades")))
//                .build();
//    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getSecurityDesc() {
        return securityDesc;
    }

    public void setSecurityDesc(String securityDesc) {
        this.securityDesc = securityDesc;
    }

    public Integer getTradedVolume() {
        return tradedVolume;
    }

    public void setTradedVolume(Integer tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XetraStockMarketTrade)) return false;
        XetraStockMarketTrade that = (XetraStockMarketTrade) o;
        return Objects.equals(mnemonic, that.mnemonic) &&
                Objects.equals(securityDesc, that.securityDesc) &&
                Objects.equals(tradedVolume, that.tradedVolume) &&
                Objects.equals(isin, that.isin) &&
                currency.equals(that.currency) &&
                Objects.equals(securityType, that.securityType) &&
                Objects.equals(securityID, that.securityID) &&
                Objects.equals(dateTimeTrade, that.dateTimeTrade) &&
                Objects.equals(startPrice, that.startPrice) &&
                Objects.equals(maxPrice, that.maxPrice) &&
                Objects.equals(minPrice, that.minPrice) &&
                Objects.equals(endPrice, that.endPrice) &&
                Objects.equals(numberOfTrades, that.numberOfTrades);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mnemonic, securityDesc, tradedVolume, isin, currency, securityType, securityID,
                dateTimeTrade, startPrice, maxPrice, minPrice, endPrice, numberOfTrades);
    }

    @Override
    public String toString() {
        return "XetraStockMarketTrade{" + "mnemonic='" + mnemonic + '\'' +
                ", securityDesc='" + securityDesc + '\'' +
                ", tradedVolume=" + tradedVolume +
                ", isin='" + isin + '\'' +
                ", currency=" + currency +
                ", securityType=" + securityType +
                ", securityID=" + securityID +
                ", dateTimeTrade=" + dateTimeTrade +
                ", startPrice=" + startPrice +
                ", maxPrice=" + maxPrice +
                ", minPrice=" + minPrice +
                ", endPrice=" + endPrice +
                ", numberOfTrades=" + numberOfTrades +
                '}';
    }


    public String toSqlInsertString() {
        // (CURRENCY,TRADE_DATE_TIME,END_PRICE,ISIN,MAX_PRICE,MIN_PRICE,NUMB,ER_OF_TRADES,SECURITY_ID,,SECURITY_TYPE,START_PRICE,MNEMONIC,SECURITY_DESC,TRADED_VOLUME)
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append('\'').append(currency).append('\'').append(",");
        sb.append('\'').append(dateTimeTrade).append('\'').append(",");
        sb.append(endPrice).append(",");
        sb.append('\'').append(isin).append('\'').append(",");
        sb.append(maxPrice).append(",");
        sb.append(minPrice).append(",");
        sb.append(numberOfTrades).append(",");
        sb.append(securityID).append(",");
        sb.append('\'').append(securityType).append('\'').append(",");
        sb.append(startPrice).append(",");
        sb.append('\'').append(mnemonic).append('\'').append(",");
        sb.append('\'').append(securityDesc).append('\'').append(",");
        sb.append(tradedVolume);
        sb.append(")");
        return sb.toString();
    }

    public static class XetraStockMarketTradeBuilder {
        private String isin;
        private String mnemonic;
        private String securityDesc;
        private String securityType;
        private String currency;
        private Integer securityID;
        private Timestamp dateTimeTrade;
        private Double startPrice;
        private Double maxPrice;
        private Double minPrice;
        private Double endPrice;
        private Integer tradedVolume;
        private Integer numberOfTrades;

        public XetraStockMarketTradeBuilder setIsin(String isin) {
            this.isin = isin;
            return this;
        }

        public XetraStockMarketTradeBuilder setMnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
            return this;
        }

        public XetraStockMarketTradeBuilder setSecurityDesc(String securityDesc) {
            this.securityDesc = securityDesc;
            return this;
        }

        public XetraStockMarketTradeBuilder setSecurityType(String securityType) {
            this.securityType = securityType;
            return this;
        }

        public XetraStockMarketTradeBuilder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public XetraStockMarketTradeBuilder setSecurityID(Integer securityID) {
            this.securityID = securityID;
            return this;
        }

        public XetraStockMarketTradeBuilder setDateTimeTrade(Timestamp dateTimeTrade) {
            this.dateTimeTrade = dateTimeTrade;
            return this;
        }

        public XetraStockMarketTradeBuilder setStartPrice(Double startPrice) {
            this.startPrice = startPrice;
            return this;
        }

        public XetraStockMarketTradeBuilder setMaxPrice(Double maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public XetraStockMarketTradeBuilder setMinPrice(Double minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public XetraStockMarketTradeBuilder setEndPrice(Double endPrice) {
            this.endPrice = endPrice;
            return this;
        }

        public XetraStockMarketTradeBuilder setTradedVolume(Integer tradedVolume) {
            this.tradedVolume = tradedVolume;
            return this;
        }

        public XetraStockMarketTradeBuilder setNumberOfTrades(Integer numberOfTrades) {
            this.numberOfTrades = numberOfTrades;
            return this;
        }

        public XetraStockMarketTrade build() {
            return new XetraStockMarketTrade(isin, currency, securityType, securityID, dateTimeTrade, startPrice, maxPrice,
                    minPrice, endPrice, numberOfTrades, mnemonic, securityDesc, tradedVolume);
        }
    }
}
