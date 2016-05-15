package com.venus.service.market;

import static com.venus.utils.PropertieUtils.*;
import static com.venus.utils.Constants.MARKET_FILE_LOCATION;

import com.venus.domain.enums.MarketDataProvider;
import com.venus.domain.enums.StockIndex;
import com.venus.domain.enums.TimePeriod;
import com.venus.utils.Constants;
import com.venus.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by erix-mac on 15/8/3.
 */
@Service
public class MarketDataDownloader {
    @Autowired
    private MarketService marketService;

    public void downloadAllDividends(){
        this.downloadMarketData(null, -1, true);
    }

    public void downloadAllMarketData(){
        this.downloadMarketData(TimePeriod.DAILY, -1, false);
        //this.downloadMarketData(TimePeriod.WEEKLY, -1, false);
        //this.downloadMarketData(TimePeriod.MONTHLY, -1,false );
    }

    public void downloadMarketData(TimePeriod timePeriod, int historyDays, boolean isDividends){
        String stockList = getMarketProperty(Constants.MARKET_STOCK_LIST);
        String[] stocks = stockList.split(Constants.MARKET_STOCK_SPLIT);

        for ( String stock : stocks ){
            MarketDataDownloadTask task = MarketDataDownloadTask.newTask(stock, stock, timePeriod, historyDays, FileUtils.getCSVFileName(MARKET_FILE_LOCATION, stock, timePeriod, isDividends), isDividends);
            task.setMarketService(this.marketService);
            new Thread(task).start();
        }
    }

    public static void main(String args[]) {
        MarketDataDownloader marketDataService = BeanContext.getBean(MarketDataDownloader.class);


        //marketDataService.downloadAllMarketData();
        marketDataService.downloadMarketData(TimePeriod.DAILY, -1, false);
        //marketDataService.downloadMarketIndexData(TimePeriod.DAILY, -1);
        //marketDataService.downloadMarketIndexData(TimePeriod.WEEKLY, -1);
        //marketDataService.downloadMarketIndexData(TimePeriod.MONTHLY, -1);


        //marketDataService.downloadAllDividends();


    }

}
