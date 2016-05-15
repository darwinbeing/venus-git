package com.venus.service.trade;

import com.google.common.collect.Lists;
import com.venus.domain.Dividends;
import com.venus.domain.Stock;
import com.venus.domain.Trade;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.market.BeanContext;
import com.venus.service.market.MarketDataService;
import com.venus.service.strategy.StrategeService;
import com.venus.service.strategy.gann.PyramidStrategy;
import com.venus.utils.PropertieUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by erix-mac on 15/9/5.
 */
@Service
@Slf4j
public class TraderService {
    private static final List<Stock> stocks = PropertieUtils.getStockList();
    private static final List<Stock> stocks34 = Lists.newArrayList(new Stock("000001"));
    //private static final List<Stock> stocks = Lists.newArrayList(new Stock("600577"), new Stock("600036"),new Stock("600030"));

    private static final List<Stock> stocks3 = Lists.newArrayList(new Stock("600577"), new Stock("600030"));

    @Autowired
    private StrategeService strategeService;
    @Autowired
    private MarketDataService marketService;
    @Autowired
    private PyramidStrategy pyramidStrategy;
    @Autowired
    private Dividends dividends;


    public List<Trade> autoTrade(TimeWindow time, double totalCapital) {
        return this.autoTrade(time, time.getPeriod(), totalCapital);
    }


    public List<Trade> autoTrade(TimeWindow timeWindow, TimePeriod period, double totalCapital) {
        List<Trade> trades = Lists.newArrayList();

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Trade>> futureList = Lists.newArrayList();
        final CountDownLatch countDownLatch = new CountDownLatch(stocks.size());

        try {
            for (Stock stock : stocks) {
                TradeManager manager = new TradeManager(stock, timeWindow, period, totalCapital);
                manager.setMarketService(marketService);
                manager.setStrategeService(strategeService);
                manager.setPyramidStrategy(pyramidStrategy);
                manager.setDividends(dividends);

                TradeTaskThread task = new TradeTaskThread(manager);
                task.setCountDownLatch(countDownLatch);

                Future<Trade> trade = executorService.submit(task);
                futureList.add(trade);
            }

            countDownLatch.await();

            for (Future<Trade> t : futureList) {
                Trade trade = t.get();
                if (trade != null && trade.hasTransaction()) {
                    trades.add(trade);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        return trades;
    }


}
