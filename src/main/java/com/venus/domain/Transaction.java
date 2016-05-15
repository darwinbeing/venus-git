package com.venus.domain;

import com.venus.domain.enums.TradeDirection;
import com.venus.domain.enums.TradeStrategy;
import com.venus.domain.vo.CSVSupport;
import com.venus.utils.Constants;
import com.venus.utils.MarketDataUtils;
import lombok.*;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by erix-mac on 15/8/14.
 */

public class Transaction implements Comparable<Transaction>, CSVSupport {

    @Getter @Setter
    private Date date;
    @Getter @Setter
    private TradeDirection direction;
    @Getter
    private Stock stock;
    @Getter @Setter
    private double price;
    @Getter @Setter
    private long positions;
    @Getter @Setter
    private long netPositions;
    @Setter
    private boolean closed = false;
    @Getter @Setter
    private TradeStrategy strategy;
    @Getter @Setter
    private boolean isDividentSplit;
    @Getter @Setter
    private TradeAction openAction;
    @Getter @Setter
    private TradeAction closeAction;

    public Transaction() {
    }

    public Transaction(Date date, TradeDirection direction, Stock stock, double price, long positions) {
        this(date,direction,stock,TradeStrategy.NONE,price,positions);
    }

    public Transaction(Date date, TradeDirection direction, Stock stock, TradeStrategy strategy, double price, long positions) {
        this.date = date;
        this.direction = direction;
        this.stock = stock;
        this.price = price;
        this.positions = positions;
        this.netPositions = positions;
        this.strategy = strategy;
    }


    public boolean isClosed(){
        if ( this.getDirection().equals(TradeDirection.SELL) )
            return true;

        return this.closed;
    }

    public double getAmount() {
        return price * positions;
    }

    public double getCommission() {
        return this.getAmount() * Constants.getTradeCommission();
    }

    public double getStampTax() {

        if (this.direction == TradeDirection.BUY) {
            return 0;
        } else if (this.direction == TradeDirection.SELL) {
            return this.getAmount() * Constants.getTradeStampTax();
        }
        return 0;
    }

    public double getProfit(double marketPrice) {

        if ( direction.equals(TradeDirection.SELL) )
            return 0;

        return getDelta(marketPrice) * this.positions;
    }

    public double getProfitPercentage(double marketPrice) {
        if (price == 0 || positions <= 0)
            return 0;

        return getDelta(marketPrice) * 100 / price;
    }

    private double getDelta(double marketPrice) {
        return marketPrice == 0 ? 0 : (marketPrice - this.price) * direction.getValue();
    }

    @Override
    public int compareTo(Transaction o) {

        if ( this.getDate().compareTo(o.getDate()) == 0 )
            return 0;

        return this.getDate().before(o.getDate()) ? 1 : -1;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + HistoricalData.toMarketDate(date) +
                ", direction=" + direction +
                ", stock=" + stock +
                ", price=" + price +
                ", positions=" + positions +
                ", netPositions=" + netPositions +
                ", profit=" + this.getProfit(this.getPrice()) +
                ", closed=" + closed +
                ", isDividentSplit=" + isDividentSplit +
                ", TradeStrategy=" + strategy +
                ", CloseAction = " + (closeAction == null ? "NULL" : closeAction.toString()) +
                '}';
    }

    @Override
    public String[] getCSVHeader() {
        return new String[]{"Date", "Stock", "Direction", "Strategy", "Price", "Positions",
                "Profit",
                "Tax", "Commission"};
    }

    @Override
    public String[] getCSVRecord() {
        DecimalFormat format = new DecimalFormat("0.##");

        return new String[]{
                HistoricalData.toMarketDate(this.getDate()),
                this.getStock().getCode(),
                this.direction.toString(),
                this.strategy.toString(),
                String.valueOf(format.format(this.price)),
                String.valueOf(format.format(this.positions)),
                String.valueOf(format.format(this.getProfit(this.getPrice()))),
                String.valueOf(format.format(this.getStampTax())),
                String.valueOf(format.format(this.getCommission()))
        };
    }
}
