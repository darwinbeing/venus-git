package com.venus.utils;

import com.venus.base.AbstractTestCase;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.market.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.venus.domain.HistoricalData.toMarketDate;

public class MarketDataUtilsTest extends AbstractTestCase {

    @Autowired
    private MarketDataService marketDataService;

    private final static TimeWindow full_2015 = new TimeWindow(toMarketDate("2014-06-15"), toMarketDate("2015-09-01"), TimePeriod.DAILY);





}