package com.venus.service.market;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.venus.dao.MarketDataDAO;
import com.venus.domain.*;
import com.venus.domain.enums.StockIndex;
import com.venus.domain.enums.TimePeriod;
import com.venus.utils.FileUtils;
import com.venus.utils.MarketDataUtils;
import com.venus.utils.PerformanceUtils;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;


import static com.venus.utils.Constants.MARKET_FILE_LOCATION;


/**
 * Created by erix-mac on 15/8/6.
 */
@Service
@Slf4j
public class MarketDataService {

    private final static MarketDataDAO marketDataDAO;

    private final static String SHANGHAI_INDEX_CODE = "000001s";

    private final static Map<String, List<HistoricalData>> dailyMarketDatas;
    private final static Map<String, List<HistoricalData>> weeklyMarketDatas = Maps.newConcurrentMap();
    private final static Map<String, List<HistoricalData>> monthlyMarketDatas = Maps.newConcurrentMap();
    private final static Map<String, List<Dividends>> dividends = Maps.newConcurrentMap();

    @Autowired
    private SinaMarketDataService liveDataService;

    public MarketDataService() {
    }

    static {
        marketDataDAO = BeanContext.getBean(MarketDataDAO.class);
        long begin = PerformanceUtils.beginTime("MarketDataService.getAllMarketDataMapFromDB");
        dailyMarketDatas = getAllMarketDataMapFromDB();
        System.out.println("Map Size: " + dailyMarketDatas.size());
        PerformanceUtils.endTime("MarketDataService", begin);

        //dailyMarketDatas = getMarketHistoricalData(TimePeriod.DAILY);
        //weeklyMarketDatas = this.getMarketHistoricalData(TimePeriod.WEEKLY);
        //monthlyMarketDatas = this.getMarketHistoricalData(TimePeriod.MONTHLY);
        //dividends = this.getMarketDividends();
    }


    static Map<String, List<HistoricalData>> getAllMarketDataMapFromDB() {
        Map<String, List<HistoricalData>> maps = Maps.newConcurrentMap();

        List<HistoricalData> datas = marketDataDAO.getAllMarketData();
        for (HistoricalData d : datas) {
            List<HistoricalData> stockData = maps.get(d.getCode());

            if (stockData == null) {
                maps.put(d.getCode(), new ArrayList<HistoricalData>());
            } else {
                stockData.add(d);
            }
        }

        return maps;
    }


    @SneakyThrows(IOException.class)
    static Map<String, List<HistoricalData>> getMarketHistoricalData(TimePeriod timePeriod) {
        Map<String, List<HistoricalData>> map = Maps.newHashMap();

        File marketRoot = new File(MARKET_FILE_LOCATION);
        File[] marketData = FileUtils.getMarketDataCSVFilePath(marketRoot, timePeriod, false);

        for (File f : marketData) {
            List<HistoricalData> list = Lists.newArrayList();

            String stockCode = FileUtils.getStockCode(f.getName());
            //log.info("Get History Data for " + stockCode);

            @Cleanup
            Reader in = new FileReader(f);

            @Cleanup
            final CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader());
            for (final CSVRecord record : parser) {
                list.add(extractMarketData(stockCode, record, timePeriod));
            }

            map.put(stockCode, list);
        }

        return map;
    }

    @SneakyThrows(IOException.class)
    Map<String, List<Dividends>> getMarketDividends() {
        Map<String, List<Dividends>> map = Maps.newHashMap();

        File marketRoot = new File(MARKET_FILE_LOCATION);
        File[] marketData = FileUtils.getMarketDataCSVFilePath(marketRoot, null, true);

        for (File f : marketData) {
            List<Dividends> list = Lists.newArrayList();

            String stockCode = FileUtils.getStockCode(f.getName());
            @Cleanup
            Reader in = new FileReader(f);

            @Cleanup
            final CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader());
            for (final CSVRecord record : parser) {
                list.add(this.extractDividendsData(stockCode, record));
            }

            map.put(stockCode, list);
        }

        return map;
    }

    public List<HistoricalData> getShanghaiIndexHistoricalData() {
        return dailyMarketDatas.get(SHANGHAI_INDEX_CODE);
    }


    public List<HistoricalData> getMarketHistoricalData(String stockCode, TimePeriod timePeriod) {

        switch (timePeriod) {
            case DAILY:
                return dailyMarketDatas.get(stockCode);
            case WEEKLY:
                return weeklyMarketDatas.get(stockCode);
            case MONTHLY:
                return monthlyMarketDatas.get(stockCode);
        }

        return null;
    }

    public List<Dividends> getMarketDividends(String stockCode) {
        return dividends.get(stockCode);
    }


    public LiveData getMarketLiveData(StockIndex index) {
        return this.liveDataService.getMarketData(index);
    }


    public Map<String, LiveData> getMarketLiveData(StockIndex[] indexs) {
        return this.liveDataService.getMarketLiveData(indexs);
    }

    public LiveData getMarketLiveData(String stockName) {
        return this.liveDataService.getMarketLiveData(stockName).get(stockName);
    }

    public Map<String, LiveData> getMarketLiveData(String[] stockNames) {
        return this.liveDataService.getMarketLiveData(stockNames);
    }

    public List<HistoricalData> getMarketHistoricalData(String stockCode, TimePeriod timePeriod, Date begin, Date end) {
        List<HistoricalData> datas = this.getMarketHistoricalData(stockCode, timePeriod);
        return MarketDataUtils.extractByDate(datas, begin, end);
    }

    public HistoricalData getMarketHistoricalData(String stockCode, TimePeriod timePeriod, Date date) {
        List<HistoricalData> datas = this.getMarketHistoricalData(stockCode, timePeriod);

        return MarketDataUtils.getMarketCurrent(datas, date);
    }

    @SneakyThrows(ParseException.class)
    private static HistoricalData extractMarketData(String code, CSVRecord record, TimePeriod timePeriod) {

        if (code == null || record == null)
            return null;

        //System.out.println(">>>>>>>>>> record: " + record.toString());

        HistoricalData m = new HistoricalData();

        m.setCode(code);
        m.setName(code);
        m.setPeriod(timePeriod);

        SimpleDateFormat format = new SimpleDateFormat(HistoricalData.MARKET_DATE_FORMAT);
        m.setDate(format.parse(record.get(0)));
        m.setOpen(Double.parseDouble(record.get(1)));
        m.setHigh(Double.parseDouble(record.get(2)));
        m.setLow(Double.parseDouble(record.get(3)));
        m.setClose(Double.parseDouble(record.get(4)));
        m.setVolume(Long.parseLong(record.get(5)));
        m.setAdjClose(Double.parseDouble(record.get(6)));

        return m;
    }

    private Dividends extractDividendsData(String code, CSVRecord record) {

        if (code == null || record == null)
            return null;

        Dividends m = new Dividends();
        SimpleDateFormat format = new SimpleDateFormat(HistoricalData.MARKET_DATE_FORMAT);
        return m;
    }

    public static void main(String args[]) throws Exception {
        System.out.println(">>>>> HistoricalDataService:");
        MarketDataService historicalDataService = BeanContext.getBean(MarketDataService.class);
        System.out.println(">>>>> HistoricalDataService:");
        List<HistoricalData> datas = historicalDataService.getMarketHistoricalData(StockIndex.ShanghaiCompositeIndex.getCode(), TimePeriod.DAILY);
        System.out.println("Daily " + StockIndex.ShanghaiCompositeIndex.getCode() + ": " + datas.size());
    }

}
