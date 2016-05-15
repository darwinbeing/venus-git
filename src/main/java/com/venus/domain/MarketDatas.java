package com.venus.domain;

import com.venus.domain.enums.TimePeriod;
import com.venus.service.market.MarketDataService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import java.util.Date;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by erix-mac on 15/8/20.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class MarketDatas {
    private Stock stock;

    private LiveData liveData;
    private List<HistoricalData> dailyMarketData;
    private List<HistoricalData> weeklyMarketData;
    private List<HistoricalData> monthlyMarketData;

    @Autowired
    private MarketDataService marketDataService;

    public MarketDatas(Stock stock){
        this.stock = stock;
    }



    public MarketDatas init(Stock s){

        if ( this.stock != null && s.getCode().equals(this.stock.getCode()) && this.dailyMarketData != null && this.dailyMarketData.size() > 0)
            return this;

        this.stock = s;
        String code = s.getCode();
        this.setDailyMarketData(marketDataService.getMarketHistoricalData(code, TimePeriod.DAILY));

        //this.setWeeklyMarketData(marketDataService.getMarketHistoricalData(code, TimePeriod.WEEKLY));
        //this.setMonthlyMarketData(marketDataService.getMarketHistoricalData(code, TimePeriod.MONTHLY));

        return this;
    }

    public List<HistoricalData> getHistoricalData(TimePeriod timePeriod) {

        switch (timePeriod) {
            case DAILY:
                return dailyMarketData;
            case WEEKLY:
                return weeklyMarketData;
            case MONTHLY:
                return monthlyMarketData;
            default:
                return null;
        }
    }
}
