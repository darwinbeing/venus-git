package com.venus.service.trade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.venus.base.AbstractTestCase;
import com.venus.domain.Stock;
import com.venus.domain.Trade;
import com.venus.domain.TradeProfit;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.market.BeanContext;
import com.venus.utils.DateUtils;
import com.venus.utils.PerformanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.venus.domain.HistoricalData.toMarketDate;

@Slf4j
public class TraderServiceTest extends AbstractTestCase {
    @Autowired
    TraderService traderService;

    public final static long capital  = 100000;

    public final static TimeWindow trade_simulation_window = new TimeWindow(toMarketDate("2015-09-01"),toMarketDate("2015-10-09"),TimePeriod.DAILY);

    public final static TimeWindow test_window = new TimeWindow(toMarketDate("2015-05-01"),toMarketDate("2015-06-07"),TimePeriod.DAILY);
    public final static TimeWindow full_2015 = new TimeWindow(toMarketDate("2014-01-01"),toMarketDate("2015-09-01"),TimePeriod.DAILY);
    public final static TimeWindow full_2016 = new TimeWindow(toMarketDate("2015-01-01"),toMarketDate("2016-04-28"),TimePeriod.DAILY);
    public final static TimeWindow full_2015_year = new TimeWindow(toMarketDate("2015-01-01"),toMarketDate("2015-12-31"),TimePeriod.DAILY);
    public final static TimeWindow bull_2014 = new TimeWindow(toMarketDate("2014-01-01"),toMarketDate("2015-05-01"),TimePeriod.DAILY);
    public final static TimeWindow full_benchmark = new TimeWindow(toMarketDate("2015-01-01"),toMarketDate("2016-01-26"),TimePeriod.DAILY);


    public final static TimeWindow full_2015_callapse = new TimeWindow(toMarketDate("2015-05-01"),toMarketDate("2015-09-01"),TimePeriod.DAILY);
    public final static TimeWindow bear_2015_callapse = new TimeWindow(toMarketDate("2015-06-01"),toMarketDate("2015-09-01"),TimePeriod.DAILY);

    public final static TimeWindow full_2007 = new TimeWindow(toMarketDate("2005-02-15"),toMarketDate("2008-12-20"),TimePeriod.DAILY);
    public final static TimeWindow bear_2008 = new TimeWindow(toMarketDate("2007-10-15"),toMarketDate("2008-12-20"),TimePeriod.DAILY);
    public final static TimeWindow bull_2007 = new TimeWindow(toMarketDate("2005-02-15"),toMarketDate("2007-10-20"),TimePeriod.DAILY);
    public final static TimeWindow bull_2009 = new TimeWindow(toMarketDate("2009-01-15"),toMarketDate("2010-01-20"),TimePeriod.DAILY);

    public final static TimeWindow split = new TimeWindow(toMarketDate("2015-06-02"),toMarketDate("2015-06-15"),TimePeriod.DAILY);


    private TraderService1 getTraderService(){
        return BeanContext.getBean(TraderService1.class);
    }

    @Test
    public void testSplitTradeResult() throws Exception {

        List<Trade> splitResult = this.traderService.autoTrade(split,capital);
        printTradeResult(split,splitResult);
    }

    @Test
    public void testForWeChatResult() throws Exception{
        TimeWindow m1  = TimeWindow.getLastMonths(TimePeriod.DAILY,-1);
        TimeWindow m2  = TimeWindow.getLastMonths(TimePeriod.DAILY, - 2);
        TimeWindow m3  = TimeWindow.getLastMonths(TimePeriod.DAILY, - 3);
        TimeWindow m6  = TimeWindow.getLastMonths(TimePeriod.DAILY, - 6);
        TimeWindow m12 = TimeWindow.getLastMonths(TimePeriod.DAILY, - 12);
        TimeWindow m24 = TimeWindow.getLastMonths(TimePeriod.DAILY, - 24);


        List<Trade> r1 = this.traderService.autoTrade(m1, capital);
        List<Trade> r2 = this.traderService.autoTrade(m2, capital);
        List<Trade> r3 = this.traderService.autoTrade(m3, capital);
        List<Trade> r6 = this.traderService.autoTrade(m6, capital);
        List<Trade> r12 = this.traderService.autoTrade(m12, capital);
        List<Trade> r24 = this.traderService.autoTrade(m24, capital);


        printTradeResult(m1, r1);
        printTradeResult(m2, r2);
        printTradeResult(m3, r3);
        printTradeResult(m6, r6);
        printTradeResult(m12, r12);
        printTradeResult(m24, r24);
    }


    @Test
    public void testFull2007_2008TradeProfit() throws Exception {
        //printTradeResult(full_2007,this.traderService.autoTrade(full_2007,capital));
        printTradeResult(full_benchmark,this.traderService.autoTrade(full_benchmark,capital));
    }


    @Test
    public void testDailyBatch() throws  Exception{
        Date today = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        TimeWindow lastMonth = TimeWindow.getTimeWindow(TimeWindow.getLastMonth(TimePeriod.DAILY).getBegin(), calendar.getTime());
        List<Trade> result = this.traderService.autoTrade(lastMonth,capital);
        printTradeResult(lastMonth,result);


    }

    @Test
    public void testAnaysisFull2015TradeResult1() throws Exception {
        List<Trade> result = this.traderService.autoTrade(full_2015,capital);
        printTradeResult(full_2015,result);
    }


    @Test
    public void testAnaysisFullTradeResult() throws Exception {
        Date beginDate = new Date();
        long begin = PerformanceUtils.beginTime("testAnaysisFullTradeResult");

        //List<Trade> result1 = this.traderService.autoTrade(full_2016, capital);
        //List<Trade> result7 = this.traderService.autoTrade(full_2015,capital);

        //List<Trade> result2 = this.traderService.autoTrade(full_2015_callapse,capital);
        //List<Trade> result3 = this.traderService.autoTrade(full_2007, capital);
        //List<Trade> result4 = this.traderService.autoTrade(bear_2008, capital);


        //List<Trade> result5 = this.traderService.autoTrade(bull_2014,capital);
        List<Trade> result6 = this.traderService.autoTrade(bull_2007,capital);
        //List<Trade> result6 = this.traderService.autoTrade(bull_2009,capital);


       // printTradeResult(full_2016,result1);
        //printTradeResult(full_2015,result7);

        //printTradeResult(full_2015_callapse,result2);
        //printTradeResult(full_2007,result3);
        //printTradeResult(bear_2008,result4);

        //printTradeResult(bull_2014,result5);
        printTradeResult(bull_2007,result6);

        //printTradeResult(bull_2009,result6);

        long cost = PerformanceUtils.endTime("testAnaysisFullTradeResult", begin);
        System.out.println(">>>>>>>>>>>>>>>>  Time Begin: " + beginDate);
        System.out.println(">>>>>>>>>>>>>>>>  Time End: " + new Date());
    }



    @Test
    public void testStressRandomYearsTrade() throws  Exception{
        Date begin = DateUtils.toMarketDate("2006-01-01");
        Date end   = DateUtils.toMarketDate("2016-01-01");
        Map<TimeWindow,List<Trade>> tradeResult = Maps.newHashMap();
        Map<String, List<Trade>> tradeStockResult = Maps.newConcurrentMap();

        for ( int i = 0; i<10; i++ ){
            Date randomDate = DateUtils.randomDate(begin,end);

            TimeWindow window = TimeWindow.getTimeWindow(TimePeriod.DAILY,randomDate,0,-12,0);
            List<Trade> result = this.traderService.autoTrade(window, capital);
            tradeResult.put(window,result);

            for (Trade t : result){
                List<Trade> trades = tradeStockResult.get(t.getStock().getCode());

                if ( trades == null ){
                    trades = Lists.newArrayList();
                    trades.add(t);
                    tradeStockResult.put(t.getStock().getCode(),trades);
                }else{
                    trades.add(t);
                }
            }
        }

        printTradeResult(tradeResult);
        printStockTradeResult(tradeStockResult);

        calcPrintAverageStressProfit(tradeStockResult);
    }


    private void calcPrintAverageStressProfit( Map<String, List<Trade>> stockTrades){

        System.out.println("\n ======================  Stock Average Stress Profit ==========================: ");

        DecimalFormat format = new DecimalFormat("0.##");

        System.out.println("Stock           Average Profit%  ");


        for ( Map.Entry<String,List<Trade>> entry : stockTrades.entrySet() ) {
            List<Trade> trades = entry.getValue();

            if ( trades == null || trades.size() == 0 )
                continue;

            double averageProfit = 0;
            double sumProfit = 0;

            for ( Trade t : trades ){
                TradeProfit p = t.getProfit();
                sumProfit += p.getTotalProfitPercentage();
            }

            averageProfit = sumProfit / trades.size();
            Trade trade = trades.get(0);
            System.out.println(trade.getStock().getCode() + "           " + format.format(averageProfit) + "% ");
        }
    }

    @Test
    public void testStockFor600577() throws Exception{
        TimeWindow past1month = new TimeWindow(toMarketDate("2015-11-20"),toMarketDate("2015-12-21"),TimePeriod.DAILY);
        TimeWindow past2month = new TimeWindow(toMarketDate("2015-10-20"),toMarketDate("2015-12-07"),TimePeriod.DAILY);
        TimeWindow past3month = new TimeWindow(toMarketDate("2015-10-20"),toMarketDate("2015-12-07"),TimePeriod.DAILY);

        TimeWindow full_2015 = new TimeWindow(toMarketDate("2014-01-01"),toMarketDate("2015-12-20"),TimePeriod.DAILY);
        TimeWindow bear_2015_callapse = new TimeWindow(toMarketDate("2015-06-01"),toMarketDate("2015-09-01"),TimePeriod.DAILY);
        TimeWindow bull_2014 = new TimeWindow(toMarketDate("2014-06-01"),toMarketDate("2015-06-01"),TimePeriod.DAILY);


        List<Trade> result1 = this.traderService.autoTrade(past1month, capital);
        List<Trade> result2 = this.traderService.autoTrade(past2month,capital);
        List<Trade> result3 = this.traderService.autoTrade(past3month, capital);

        List<Trade> result4 = this.traderService.autoTrade(full_2015, capital);
        List<Trade> result5 = this.traderService.autoTrade(bear_2015_callapse, capital);
        List<Trade> result6 = this.traderService.autoTrade(bull_2014, capital);


        printTradeResult(past1month,result1);
        printTradeResult(past2month,result2);
        printTradeResult(past3month,result3);

        printTradeResult(full_2015,result4);
        printTradeResult(bear_2015_callapse,result5);
        printTradeResult(bull_2014,result6);



    }

    public static void printTradeResult(Map<TimeWindow,List<Trade>> tradeResult){

        for ( Map.Entry<TimeWindow,List<Trade>> entry : tradeResult.entrySet() ){
            printTradeResult(entry.getKey(),entry.getValue());
        }
    }

    public static void printStockTradeResult(Map<String, List<Trade>> stockTrades){
        System.out.println("\n ======================  Stock Stress Trade ==========================: ");

        if ( stockTrades == null || stockTrades.size() == 0 ) {
            System.out.println("         >>>>>>>>>>>>>>  NO Trade Trigger !>>>>>> >>>>>>>>>>>>>>: ");

            return;
        }

        DecimalFormat format = new DecimalFormat("0.##");
        for ( Map.Entry<String,List<Trade>> entry : stockTrades.entrySet() ){

            List<Trade> trades = entry.getValue();
            for ( Trade t : trades ){
                TradeProfit p = t.getProfit();
                System.out.println("Stock: " + t.getStock().getCode() + " " + t.getStock().getCode() + " Total Capital Profit: " + format.format(p.getTotalProfitPercentage()) + "% Profit: " + format.format(p.getProfit()) + " Net Profit:" + format.format(p.getNetProfit()) + " Total Buy: " + format.format(p.getTotalBuy()) + " Total Sell: " + format.format(p.getTotalSell()) + " Total Holding Positions: " + t.getPositions() + " Transactions: " + t.getTransactions().size());
            }
        }

        System.out.println("\n ======================  Stock Stress Trade END==========================: ");

    }

    public static void printTradeResult(TimeWindow window, List<Trade> trades) {

        System.out.println("\n >>>>>>>>>>>>>> >>>>>>>>>>>>>>   Trade " + window.toString() + ">>>>>>>>>>>>>> >>>>>>>>>>>>>>: ");

        if ( trades == null || trades.size() == 0 ) {
            System.out.println("         >>>>>>>>>>>>>>  NO Trade Trigger !>>>>>> >>>>>>>>>>>>>>: ");

            return;
        }


        DecimalFormat format = new DecimalFormat("0.##");

        for ( Trade t : trades ){
            TradeProfit p = t.getProfit();
            System.out.println("Stock: " + t.getStock().getCode() + " " + t.getStock().getName()+ " Total Capital Profit: " + format.format(p.getTotalProfitPercentage()) + "% Profit: " + format.format(p.getProfit()) + " Net Profit:" + format.format(p.getNetProfit()) + " Total Buy: " + format.format(p.getTotalBuy()) + " Total Sell: " + format.format(p.getTotalSell()) + " Total Holding Positions: " + t.getPositions() + " Transactions: " + t.getTransactions().size());
        }

        String fileName = "Trade";
        Trade.toCSVFile(fileName,window,trades,false);
        System.out.println(" >>>>>>>>>>>>>> >>>>>>>>>>>>>>   Trade >>>>>>>>>>>>>> >>>>>>>>>>>>>>");

    }
}