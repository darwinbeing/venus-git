package com.venus.service.trade;

import com.google.common.collect.Lists;
import com.venus.domain.HistoricalData;
import com.venus.domain.Trade;
import com.venus.domain.TradeProfit;
import com.venus.domain.Transaction;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.market.BeanContext;
import com.venus.utils.CSVUtils;
import com.venus.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.venus.domain.HistoricalData.toMarketDate;

/**
 * Created by erix-mac on 15/10/27.
 */

@Slf4j
@Service
public class DailyBatch {

    public final static long capital = 50000;
    private final static String FILE_TRANSACTION_LOCATOIN = Constants.TRADE_FILE_DAILY_REPORT_LOCATION;



    @Autowired
    TraderService1 traderService;

    public static void main(String[] args) {
        log.info("Daily Report Starting....");

        DailyBatch dailyBatch = BeanContext.getBean(DailyBatch.class);
        dailyBatch.runDailyTradeAnalysis();

        log.info("Daily Report End.");

    }

    public void runDailyTradeAnalysis() {

        Date today = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -20);
        // report all T-7 to T trade trans
        Date reportWindow = calendar.getTime();

        //TimeWindow lastMonth = TimeWindow.getLastMonths(TimePeriod.DAILY, -3);
        TimeWindow tradWindow = new TimeWindow(toMarketDate("2016-04-26"),today,TimePeriod.DAILY);


        List<Transaction> todayTrans = Lists.newArrayList();
        List<Trade> result = this.traderService.autoTrade(tradWindow, capital);

        System.out.println("\n\n\n TRADE REPORT  BEGIN");
        for (Trade trade : result) {
            List<Transaction> trans = trade.getTransactions();
            for (Transaction tran : trans) {
                if (tran.getDate().after(reportWindow)) {
                    //System.out.println(">>>>>>> Report: " + tran.toString());
                    todayTrans.add(tran);
                }
            }
        }
        Collections.sort(todayTrans);
        printTradingTransationReport(todayTrans);
        System.out.println("\n\n TRADE REPORT  END");

        printTradeResult(tradWindow, result);
        //String fileName = FILE_TRANSACTION_LOCATOIN + HistoricalData.toMarketDate(today) + "_daily.csv";
        //CSVUtils.writeCSV(fileName,todayTrans,false);
    }


    private static void printTradingTransationReport(List<Transaction> trans) {
        for (Transaction tran : trans) {
            System.out.println(">>>>>>> Report: " + tran.toString());
        }

    }

    public static void printTradeResult(TimeWindow window, List<Trade> trades) {

        System.out.println("\n >>>>>>>>>>>>>> >>>>>>>>>>>>>>   Trade " + window.toString() + ">>>>>>>>>>>>>> >>>>>>>>>>>>>>: ");

        if (trades == null || trades.size() == 0) {
            System.out.println(" >>>>>>>>>>>>>> NO Trade Action ! >>>>>>>>>>>>>>: ");

            return;
        }


        DecimalFormat format = new DecimalFormat("0.##");

        for (Trade t : trades) {
            TradeProfit p = t.getProfit();
            System.out.println("Stock: " + t.getStock().getCode() + " Total Capital Profit: " + format.format(p.getTotalProfitPercentage()) + "% Profit: " + format.format(p.getProfit()) + " Market Price:" + t.getMarketPrice() + " Net Profit:" + format.format(p.getNetProfit()) + " Total Buy: " + format.format(p.getTotalBuy()) + " Total Sell: " + format.format(p.getTotalSell()) + " Total Holding Positions: " + t.getPositions() + " Transactions: " + t.getTransactions().size());
        }

        String fileName = "Trade";
        //Trade.toCSVFile(fileName,window,trades,false);
        System.out.println(" >>>>>>>>>>>>>> >>>>>>>>>>>>>>   Trade >>>>>>>>>>>>>> >>>>>>>>>>>>>>");

    }
}
