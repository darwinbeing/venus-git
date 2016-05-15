package com.venus.service.trade;

import com.google.common.collect.Lists;
import com.venus.domain.*;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.market.BeanContext;
import com.venus.service.market.MarketDataService;
import com.venus.service.strategy.StrategeService;
import com.venus.service.strategy.Strategy;
import com.venus.service.strategy.gann.PyramidStrategy;
import com.venus.utils.MarketDataUtils;
import com.venus.utils.PropertieUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 15/9/5.
 */
@Service
@Slf4j
public class TraderService1 {
    private static final List<Stock> stocks = PropertieUtils.getStockList();
    private static final List<Stock> stocks333 = Lists.newArrayList(new Stock("600577"), new Stock("600036"),new Stock("600030"));
    private static final List<Stock> stocks3343 = Lists.newArrayList(new Stock("600577"));
    private static final List<Stock> stocks5 = Lists.newArrayList(new Stock("000001,399001"));
    private static final List<Stock> stocks343 = Lists.newArrayList(new Stock("600577"));
    private static final List<Stock> stocks34 = Lists.newArrayList(new Stock("600036"));

    private static final List<Stock> stocks99 = Lists.newArrayList(new Stock("600577"), new Stock("600030"));


    private static final List<Stock> stocks3 = Lists.newArrayList(new Stock("000002"));


    @Autowired
    private StrategeService strategeService;
    @Autowired
    private MarketDataService marketService;
    @Autowired
    @Getter
    private PyramidStrategy pyramidStrategy;
    @Autowired
    private Dividends dividends;

    public List<Trade> autoTrade(TimeWindow time, double totalCapital) {
        return this.autoTrade(time,time.getPeriod(), totalCapital);
    }


    public List<Trade> autoTrade(TimeWindow timeWindow, TimePeriod period, double totalCapital) {
        List<Trade> trades = Lists.newArrayList();

        TradeManager manager = new TradeManager(null, timeWindow, period, totalCapital);
        manager.setMarketService(marketService);
        manager.setStrategeService(strategeService);
        manager.setPyramidStrategy(pyramidStrategy);
        manager.setDividends(dividends);

        for ( Stock stock : stocks ) {
            manager.setStock(stock);
            Trade trade = manager.trade();
            if ( trade.hasTransaction() ) {
                trades.add(trade);
            }
        }

        return trades;
    }

}
