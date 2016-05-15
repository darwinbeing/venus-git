package com.venus.domain;

import com.venus.domain.enums.TradeDirection;
import com.venus.domain.enums.TradeStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by erix-mac on 15/9/8.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeAction {

    private TradeStrategy strategy;
    private TradeDirection tradeDirection;
    private Stock stock;
    private Date analysisDate;
    private Date tradeDate;
    private double tradePrice;
    private double orignalTransPrice;
    private boolean nettingHandled = false;
    private long positions;

    public TradeAction(TradeStrategy strategy, TradeDirection direction, Stock stock, Date analysisDate, Date tradeDate) {
        this(strategy,direction,stock,analysisDate,tradeDate,-1,-1);
    }


    public TradeAction(TradeStrategy strategy, TradeDirection direction, Stock stock, Date analysisDate, Date tradeDate, double tradePrice, double orignalTransPrice) {
        this.strategy = strategy;
        this.tradeDirection = direction;
        this.stock = stock;
        this.analysisDate = analysisDate;
        this.tradeDate = tradeDate;
        this.tradePrice = tradePrice;
        this.orignalTransPrice = orignalTransPrice;
        this.nettingHandled = false;
    }

    public double getProfit() {
        return (tradePrice - orignalTransPrice) * 100 / orignalTransPrice;
    }

    public boolean isSettingTradePositions(){
        return this.positions > 0;
    }
}
