package com.gigaspaces.mq.common;


//**PTK import com.gigaspaces.annotation.pojo.SpaceId;
//**PTK import com.gigaspaces.annotation.pojo.SpaceRouting;

import com.gigaspaces.annotation.pojo.SpaceId;

import java.sql.Timestamp;

/**
 * @author Alexander_Koltsov
 */
public abstract class Trade {

    /**
     * ISIN of the security
     */
    String isin;

    /**
     * Currency in which the product is traded
     */
    String currency;

    /**
     * Type of instrument
     */
    String securityType;

    /**
     * Unique identifier for each contract
     */
    Integer securityID;

    /**
     * Date and time of trading period
     */
    Timestamp dateTimeTrade;

    /**
     * Trading price at the start of period
     */
    Double startPrice;

    /**
     * Maximum price over the period
     */
    Double maxPrice;

    /**
     * Minimum price over the period
     */
    Double minPrice;

    /**
     * Trading price at the end of the period
     */
    Double endPrice;

    /**
     * Number of distinct trades during the period
     */
    Integer numberOfTrades;

    Trade() {
    }

    Trade(String isin, String currency, String securityType, Integer securityID,
          Timestamp dateTimeTrade, Double startPrice, Double maxPrice, Double minPrice,
          Double endPrice, Integer numberOfTrades) {
        this.isin = isin;
        this.currency = currency;
        this.securityType = securityType;
        this.securityID = securityID;
        this.dateTimeTrade = dateTimeTrade;
        this.startPrice = startPrice;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.endPrice = endPrice;
        this.numberOfTrades = numberOfTrades;
    }

    //*** PTK @SpaceRouting
    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    //*** PTK @SpaceId()
    @SpaceId()
    public Integer getSecurityID() {
        return securityID;
    }

    public void setSecurityID(Integer securityID) {
        this.securityID = securityID;
    }

    public Timestamp getDateTimeTrade() {
        return dateTimeTrade;
    }

    public void setDateTimeTrade(Timestamp dateTimeTrade) {
        this.dateTimeTrade = dateTimeTrade;
    }

    public Double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Double startPrice) {
        this.startPrice = startPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(Double endPrice) {
        this.endPrice = endPrice;
    }

    public Integer getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(Integer numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }
}
