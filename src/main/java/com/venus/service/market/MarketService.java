package com.venus.service.market;

import com.google.common.collect.Lists;
import com.venus.dao.MarketDataDAO;
import com.venus.domain.HistoricalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 16/1/14.
 */
@Service
@Slf4j
public class MarketService {

    @Autowired
    private MarketDataDAO marketDataDAO;
    @Autowired
    private SinaMarketDataService sinaMarketDataService;

    @Autowired
    private YahooMarketDataService yahooMarketDataService;


    private boolean hasMarketData(String code){
        return this.marketDataDAO.getMarketCount(code) > 0;
    }

    public void updateMarketData(String code){
        boolean hasMarket = this.hasMarketData(code);

        if ( hasMarket ){
            System.out.println(">>>>>>>> updateMarketDataIncremental :  " + code);
            this.updateMarketDataIncremental(code);
        }else{
            System.out.println(">>>>>>>> insertAllMarketData :  " + code);
            this.insertAllMarketData(code);
        }

    }




    private void insertAllMarketData(String code){
        this.marketDataDAO.insert(this.sinaMarketDataService.getMarketData(code));
        //this.marketDataDAO.insert(this.yahooMarketDataService.getAllMarketData(code));

    }

    private void updateMarketDataIncremental(String code){
        Date currMarketDate = this.marketDataDAO.getLatestMarketData(code).getDate();
        List<HistoricalData> mktData = this.sinaMarketDataService.getMarketDataIncremental(code,currMarketDate);
        List<HistoricalData> incremental = Lists.newArrayList();

        for ( HistoricalData d : mktData ){
            if ( d.getDate().after(currMarketDate) ){
                incremental.add(d);
            }
        }

        this.marketDataDAO.insert(incremental);
    }
}
