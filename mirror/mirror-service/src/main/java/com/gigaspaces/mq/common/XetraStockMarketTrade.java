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

}
