package com.venus.utils;

import com.google.common.collect.Lists;
import com.venus.base.AbstractTestCase;
import com.venus.domain.HistoricalData;
import com.venus.domain.Stock;
import com.venus.domain.Trade;
import com.venus.domain.Transaction;
import com.venus.domain.enums.TradeDirection;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CSVUtilsTest extends AbstractTestCase{

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
    public void testWriteCSV() throws Exception {

    }

    @Test
    public void testWriteTradeBackTest() throws Exception {
        Trade trade = newTrade();
        trade.setCapital(10000);
        trade.setTransactions(Lists.newArrayList(tran1,tran2, tran3, tran4));

        CSVUtils.writeCSV(Constants.MARKET_FILE_BACK_TEST_CASE_LOCATION + "test_trade.csv",Lists.newArrayList(trade),true);
        CSVUtils.writeCSV(Constants.MARKET_FILE_BACK_TEST_CASE_LOCATION + "test_transaction.csv",trade.getTransactions(),true);

    }
}