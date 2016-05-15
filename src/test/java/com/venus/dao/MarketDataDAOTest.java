package com.venus.dao;

import com.venus.base.AbstractTestCase;
import com.venus.domain.HistoricalData;
import com.venus.service.market.SinaMarketDataService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MarketDataDAOTest extends AbstractTestCase {

    @Autowired
    MarketDataDAO marketDAO;
    @Autowired
    SinaMarketDataService marketDataService;

    @Test
    public void testGetAllMarketData() throws Exception {

        List<HistoricalData> data = this.marketDAO.getAllMarketData("600577");
        System.out.println("All Data : " + data.size());
    }

    @Test
    public void testGetMarketCount() throws Exception {

        int count = marketDAO.getMarketCount("600577");

        System.out.println("Count: " + count);
    }


    @Test
    public void testInsert() throws Exception {

        List<HistoricalData> markets = this.marketDataService.getMarketData("600577");
        System.out.println("All Data : " + markets.size());

        this.marketDAO.insert(markets);
    }


    @Test
    public void testGetLatestMarketData() throws Exception{
        HistoricalData d =  this.marketDAO.getLatestMarketData("600577");

        System.out.println(d.getDate());
    }
}