/*
package com.venus.service.strategy.common;

import com.venus.base.AbstractTestCase;
import com.venus.domain.HistoricalData;
import com.venus.domain.Trade;
import com.venus.domain.TradeAction;
import com.venus.domain.enums.TradeDirection;
import com.venus.domain.Transaction;
import com.venus.domain.vo.TradeAnalysisInfo;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.venus.domain.TradeTest.stock;

public class StopLossStrategyTest extends AbstractTestCase {

    @Autowired
    private StopLossStrategy stopLossStrategy;

    @Test
    public void testNegativeProfitPercentageStopLoss() throws Exception {
        Trade trade = new Trade();
        final Transaction trans = new Transaction(HistoricalData.toMarketDate("2015-08-23"), TradeDirection.BUY,stock,10,1000);
        trade.trade(trans);

        double price = 9;
        double profit = trans.getProfitPercentage(price);
        System.out.println(profit);

        TradeAction result = stopLossStrategy.analysis().get(0);
        System.out.println("Strategy Result: " + result.getTradeDirection().getValue());

        Assert.assertEquals(-1, result.getTradeDirection().getValue());
    }

    @Test
    public void testPosstiveProfitPercentageStopLoss() throws Exception {
        Trade trade = new Trade();
        final Transaction trans = new Transaction(HistoricalData.toMarketDate("2015-08-23"), TradeDirection.BUY,stock,10,1000);

        trade.trade(trans);

        double price = 11;
        double profit = trans.getProfitPercentage(price);
        System.out.println(profit);

        stopLossStrategy.setTrade(trade);
        stopLossStrategy.setMarketPrice(price);
        List<TradeAction> result = stopLossStrategy.analysis();

        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testTriggerProfitPercentageStopLoss() throws Exception {
        Trade trade = new Trade();
        final Transaction trans = new Transaction(HistoricalData.toMarketDate("2015-08-23"), TradeDirection.BUY,stock,10,1000);

        trade.trade(trans);
        //double stopLossPercentage = Double.parseDouble(PropertieUtils.getMarketProperty(StopLossStrategy.STOP_LOSS_PERCETAGE_KEY));

        double price = 9.2;
        double profit = trans.getProfitPercentage(price);
        System.out.println(profit);

        stopLossStrategy.setTrade(trade);
        stopLossStrategy.setMarketPrice(price);
        TradeAction result = stopLossStrategy.analysis().get(0);
        System.out.println("Strategy Result: " + result.getTradeDirection().getValue());

        Assert.assertEquals(-1, result.getTradeDirection().getValue());

        price = 9.21;
        profit = trans.getProfitPercentage(price);

        stopLossStrategy.setMarketPrice(price);
        List<TradeAction> actions = stopLossStrategy.analysis();

        Assert.assertEquals(0, actions.size());
    }

}*/
