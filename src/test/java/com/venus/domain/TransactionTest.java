package com.venus.domain;

import com.venus.domain.enums.TradeDirection;
import junit.framework.Assert;
import org.junit.Test;

import static com.venus.domain.TradeTest.*;

public class TransactionTest {

    @Test
    public void testGetAmount() throws Exception {

        System.out.println(tran1.getAmount());
        Assert.assertEquals(10000,tran1.getAmount(),10);
    }

    @Test
    public void testGetCommission() throws Exception {

        System.out.println(tran1.getCommission());
        System.out.println(tran2.getCommission());

        Assert.assertEquals(5, tran1.getCommission(), 1);
        Assert.assertEquals(5.5,tran2.getCommission(),1);


    }

    @Test
    public void testGetStampTax() throws Exception {

        System.out.println(tran1.getStampTax());
        Assert.assertEquals(0, tran1.getStampTax(), 1);

        System.out.println(tran4.getStampTax());
        Assert.assertEquals(23, tran4.getStampTax(), 1);

    }

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

    @Test
    public void testGetProfilt() throws Exception {

        System.out.println(tran1.getProfit(10));
        System.out.println(tran4.getProfit(11));
        System.out.println(tran5.getProfit(12.3));

        Assert.assertEquals(0, tran1.getProfit(10), 1);
        Assert.assertEquals(1000, tran4.getProfit(11), 1);
        Assert.assertEquals(-1000, tran5.getProfit(12.3), 1);


    }



    @Test
    public void testProfiltPercentage() throws Exception {
        System.out.println(tran1.getProfitPercentage(11));
        System.out.println(tran4.getProfitPercentage(11));
        System.out.println(tran5.getProfitPercentage(12.3));
    }

    @Test
    public void testNegativeProfiltPercentage() throws Exception {
        final Transaction negativeTrans = new Transaction(HistoricalData.toMarketDate("2015-08-23"), TradeDirection.BUY,stock,10,1000);

        double profit = negativeTrans.getProfitPercentage(9);
        System.out.println(profit);
    }
}