package com.venus.service.market;

import com.venus.base.AbstractTestCase;
import com.venus.domain.HistoricalData;
import com.venus.domain.enums.StockIndex;
import com.venus.domain.enums.TimePeriod;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class HistoricalDataServiceTest extends AbstractTestCase {

    @Autowired
    MarketDataService historicalDataService;

    @Test
    public void testGetAllMarketHistoricalData() throws Exception {

        Map<String, List<HistoricalData>> marketData = historicalDataService.getMarketHistoricalData(TimePeriod.MONTHLY);

        Assert.assertNotNull(marketData);
        Assert.assertTrue(marketData.values().size() > 0);
    }

    @Test
    public void testGetMarketHistoricalData() throws Exception {
        List<HistoricalData> datas = historicalDataService.getMarketHistoricalData(StockIndex.ShanghaiCompositeIndex.getCode(), TimePeriod.MONTHLY);

        System.out.println("Daily " + StockIndex.ShanghaiCompositeIndex.getCode() + ": " + datas.size());
        Assert.assertNotNull(datas);
        Assert.assertTrue(datas.size() > 0);
    }
}