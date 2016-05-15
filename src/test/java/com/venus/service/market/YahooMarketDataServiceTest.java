package com.venus.service.market;

import com.venus.base.AbstractTestCase;
import com.venus.domain.HistoricalData;
import com.venus.domain.AbstractMarketData;
import com.venus.domain.enums.TimePeriod;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class YahooMarketDataServiceTest extends AbstractTestCase{

    @Autowired
    YahooMarketDataService marketDataService;

    @Test
    public void testGetMarketLiveData() throws Exception {
        AbstractMarketData data = marketDataService.getTodayMarketData("000002.SZ");
        System.out.println(data.toString());
        Assert.assertNotNull(data);
    }


    @Test
    public void testGetMarketHistoryData() throws Exception {
        // 沪市后缀名.ss 例子： 沪深300 000300.ss ,深市后缀名 .sz 例子： 399106.sz
        List<HistoricalData> market = marketDataService.getMarketData("600577.ss", TimePeriod.DAILY, 10);


        for ( AbstractMarketData m : market ){
            System.out.println(m.toString());
        }

        Assert.assertNotNull(market);
        Assert.assertTrue(market.size() > 0);
    }

    @Test
    public void testGetMarketIndexData() throws Exception {
        // 沪市后缀名.ss 例子： 沪深300 000300.ss ,深市后缀名 .sz 例子： 399106.sz
        List<HistoricalData> market = marketDataService.getMarketData("000001.ss", TimePeriod.DAILY, 10);


        for ( HistoricalData m : market ){
            System.out.println(m.toString());
        }

        Assert.assertNotNull(market);
        Assert.assertTrue(market.size() > 0);
    }


}