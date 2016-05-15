package com.venus.domain;

import com.google.common.collect.Lists;
import com.venus.domain.enums.TradeDirection;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class TradeTest {

    // T1: + 10   1000 = 10000
    // T2: + 11   1000 = 11000
    // T3: + 12   1000 = 12000
    // T4: - 11.5 2000 = 23000
    // C : * 11   1000 = 11000
    //
    // Profit: (23000 + 11000) - (10000 + 11000 + 12000) = 1000
    // Percentage: (23000 + 11000) - (10000 + 11000 + 12000)/ (10000 + 11000 + 12000) = 3%
    //
    // T5: - 11.3 1000 = 11300
    // Profit1: (23000 + 11300) - (10000 + 11000 + 12000) = 1300


    public final static Stock stock = new Stock("600000","SPDB");

    public final static Transaction tran1 = new Transaction(HistoricalData.toMarketDate("2015-08-01"), TradeDirection.BUY,stock,10,1000);
    public final static Transaction tran2 = new Transaction(HistoricalData.toMarketDate("2015-08-02"), TradeDirection.BUY,stock,11,1000);
    public final static Transaction tran3 = new Transaction(HistoricalData.toMarketDate("2015-08-03"), TradeDirection.BUY,stock,12,1000);
    public final static Transaction tran4 = new Transaction(HistoricalData.toMarketDate("2015-08-04"), TradeDirection.SELL,stock,11.5,2000);
    public final static Transaction tran5 = new Transaction(HistoricalData.toMarketDate("2015-08-04"), TradeDirection.SELL,stock,11.3,1000);


    private final static double MARKET_PRICE = 11;

    public static Trade newTrade(){
        return new Trade("600000",MARKET_PRICE);
    }



    @Test
    public void testGetTotalPositisons(){

        Trade trade = newTrade();
        trade.setTransactions(Lists.newArrayList(tran1,tran2));
        log.info("testGetTotalPositisons" + trade.toString());

        long positions = trade.getPositions();
        Assert.assertEquals(2000,positions);

        trade.setTransactions(Lists.newArrayList(tran1,tran2, tran3));

        positions = trade.getPositions();
        Assert.assertEquals(3000,positions);

        trade.setTransactions(Lists.newArrayList(tran1,tran2, tran3, tran4));

        positions = trade.getPositions();
        Assert.assertEquals(1000,positions);

    }


    @Test
    public void testGetProfit() throws Exception {

        Trade trade = newTrade();
        trade.setTransactions(Lists.newArrayList(tran1, tran2, tran3, tran4));

        TradeProfit p1 = trade.getProfit();
        double profits = p1.getProfit();

        Assert.assertEquals("Trans 4 Profits:",1000,profits,1);

        trade.setTransactions(Lists.newArrayList(tran1, tran2, tran3, tran5));
        TradeProfit p2 = trade.getProfit();

        profits = p2.getProfit();
        Assert.assertEquals("Trans 5 Profits:", 300, profits, 1);


    }

    @Test
    public void testGetProfitPercentage() throws Exception {

        Trade trade = newTrade();
        trade.setTransactions(Lists.newArrayList(tran1, tran2, tran3, tran4));
        TradeProfit p1 = trade.getProfit();
    }

    @Test
    public void testGetTradeProfit(){
        Trade trade = newTrade();
        trade.setTransactions(Lists.newArrayList(tran1, tran2, tran3, tran4));

        System.out.println(trade.getProfit().toString());
    }
}