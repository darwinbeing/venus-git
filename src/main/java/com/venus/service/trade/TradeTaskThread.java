package com.venus.service.trade;

import com.venus.domain.*;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.market.MarketDataService;
import com.venus.service.strategy.StrategeService;
import com.venus.service.strategy.Strategy;
import com.venus.service.strategy.gann.PyramidStrategy;
import com.venus.utils.MarketDataUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by erix-mac on 15/10/1.
 */
@Slf4j
public class TradeTaskThread implements Callable<Trade> {

    @Setter
    private TradeManager tradeManager;
    @Setter
    private CountDownLatch countDownLatch;

    public TradeTaskThread(TradeManager manager){
        this.tradeManager = manager;
    }


    @Override
    public Trade call() throws Exception {
        Trade trade = new Trade(tradeManager.getStock(), -1, tradeManager.getTotalCapital());
        log.info(">>>>>>>>>>>>>>Thread TradeTask Begin : Thread ID: " + Thread.currentThread().getId() + " stock: " + tradeManager.getStock().getCode());
        try{
            trade = this.tradeManager.trade();
            log.info(">>>>>>>>>>>>>>Thread Trade End : " + trade.getTransactions().size());
        }catch (Exception e){
            log.error(">>>>>>>> ERROR: " + tradeManager.getStock().getCode() + ": " + e.getMessage());
        }
        finally {
            log.info(">>>>>>>>>>>>>>Thread countDownLatch : ThreadID: " + Thread.currentThread().getId() + " Count: " + this.countDownLatch.getCount());
            this.countDownLatch.countDown();
        }

        return trade;
    }
}
