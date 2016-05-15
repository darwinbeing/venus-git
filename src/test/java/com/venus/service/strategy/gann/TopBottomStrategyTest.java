package com.venus.service.strategy.gann;

import com.venus.base.AbstractTestCase;
import com.venus.domain.Trade;
import com.venus.domain.TradeProfit;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.trade.TraderService1;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.util.List;

import static com.venus.domain.HistoricalData.toMarketDate;
import static org.junit.Assert.*;

@Slf4j
public class TopBottomStrategyTest extends AbstractTestCase {

    @Autowired
    TraderService1 traderService;
    public final static long capital  = 50000;



    @Test
    public void testAnaysisFull2015TopTradeResult() throws Exception {
        TimeWindow top_window = new TimeWindow(toMarketDate("2015-04-08"),toMarketDate("2015-05-29"), TimePeriod.DAILY);

        List<Trade> result = this.traderService.autoTrade(top_window,capital);
        printTradeResult(top_window,result);
    }

    @Test
    public void testAnaysisFull2015BottomTradeResult() throws Exception {
        TimeWindow top_window = new TimeWindow(toMarketDate("2014-03-20"),toMarketDate("2014-04-11"), TimePeriod.DAILY);

        List<Trade> result = this.traderService.autoTrade(top_window,capital);
        printTradeResult(top_window,result);
    }

    @Test
    public void testAnaysisFull2015Bottom2TradeResult() throws Exception {
        TimeWindow top_window = new TimeWindow(toMarketDate("2014-01-01"),toMarketDate("2014-02-18"), TimePeriod.DAILY);

        List<Trade> result = this.traderService.autoTrade(top_window,capital);
        printTradeResult(top_window,result);
    }


    public static void printTradeResult(TimeWindow window, List<Trade> trades) {

        if ( trades == null || trades.size() == 0 )
            return;

        System.out.println("\n >>>>>>>>>>>>>> >>>>>>>>>>>>>>   Trade " + window.toString() + ">>>>>>>>>>>>>> >>>>>>>>>>>>>>: ");

        DecimalFormat format = new DecimalFormat("0.##");

        for ( Trade t : trades ){
            TradeProfit p = t.getProfit();
            System.out.println("Stock: " + t.getStock().getCode() + " Total Capital Profit: " + format.format(p.getTotalProfitPercentage()) + "% Profit: " + format.format(p.getProfit()) + " Net Profit:" + format.format(p.getNetProfit()) + " Total Buy: " + format.format(p.getTotalBuy()) + " Total Sell: " + format.format(p.getTotalSell()) + " Total Holding Positions: " + t.getPositions() + " Transactions: " + t.getTransactions().size());
        }

        String fileName = "Trade";
        Trade.toCSVFile(fileName,window,trades,false);
        System.out.println(" >>>>>>>>>>>>>> >>>>>>>>>>>>>>   Trade >>>>>>>>>>>>>> >>>>>>>>>>>>>>");

    }
}