package com.venus.service.analysis;

import com.google.common.collect.Lists;
import com.venus.base.AbstractTestCase;
import com.venus.domain.HistoricalData;
import com.venus.domain.MarketPeak;
import com.venus.domain.Stock;
import com.venus.domain.enums.Peak;
import com.venus.domain.enums.StockIndex;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.market.MarketDataService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ExtremeAnalyzeTest extends AbstractTestCase{

    @Autowired
    ExtremeAnalyze analyze;

    @Autowired
    MarketDataService marketDataService;


    @Test
    public void testDeterminIndexMarketPeaksMonthly() throws Exception {

        Stock stock = new Stock(StockIndex.ShanghaiCompositeIndex.getCode());
        List<HistoricalData> all = this.marketDataService.getMarketHistoricalData(stock.getCode(),TimePeriod.MONTHLY);

        Map<Peak,List<MarketPeak>> peaks = this.analyze.determinMarketPeaks(stock,all, this.toHistoricalDate("2005-02-28"), this.toHistoricalDate("2015-09-01"), 5);
        ExtremeAnalyze.print(peaks);

    }

    @Test
    public void testDeterminIndexMarketPeaksWeekly() throws Exception {

        Stock stock = new Stock(StockIndex.ShanghaiCompositeIndex.getCode());
        List<HistoricalData> all = this.marketDataService.getMarketHistoricalData(stock.getCode(),TimePeriod.WEEKLY);

        Map<Peak,List<MarketPeak>> peaks = this.analyze.determinMarketPeaks(stock,all,this.toHistoricalDate("2005-01-01"),this.toHistoricalDate("2015-09-01"),5);
        ExtremeAnalyze.print(peaks);

    }


    @Test
    public void testDeterminStockMarketPeaksDaily() throws Exception {

        Stock stock = new Stock("600030");
        Map<Peak,List<MarketPeak>> peaks = this.analyze.determinMarketPeaks(stock,this.toHistoricalDate("2013-01-01"),this.toHistoricalDate("2013-12-30"),TimePeriod.DAILY, 5);
        ExtremeAnalyze.print(peaks);

    }

    @Test
    public void testDeterminStockPeaksDailyStrategy() throws Exception {

        Stock stock = new Stock("600030");
        Map<Peak, List<MarketPeak>> peaks = this.analyze.determinMarketPeaks(stock, TimeWindow.getTimeWindow(TimePeriod.DAILY, this.toHistoricalDate("2015-04-07"), -1, 0, 0), 3);

        ExtremeAnalyze.print(peaks);

    }
}