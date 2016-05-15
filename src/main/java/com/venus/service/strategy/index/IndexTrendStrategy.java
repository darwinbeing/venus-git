package com.venus.service.strategy.index;

import com.venus.domain.HistoricalData;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.enums.Trend;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.market.MarketDataService;
import com.venus.utils.DateUtils;
import com.venus.utils.MarketDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 16/2/29.
 */
@Component
public class IndexTrendStrategy  {

    private static final double BULL_MARKET_TREND =  20;
    private static final double BEAR_MARKET_TREND = -20;

    private static final int TIME_WINDOW = 1;

    @Autowired
    private MarketDataService marketDataService;


    public Trend anaysisIndexTrend(final Date date){
        List<HistoricalData> indexs = marketDataService.getShanghaiIndexHistoricalData();

        final HistoricalData today = MarketDataUtils.getMarketCurrent(indexs,date);
        return anaysisIndexTrend(date, today);
    }

    public Trend anaysisIndexTrend(final Date date, final HistoricalData today){
        List<HistoricalData> markets = marketDataService.getShanghaiIndexHistoricalData();
        Collections.sort(markets);

        int index = MarketDataUtils.indexOf(markets,date);
        Date beginDate = TimeWindow.getTimeWindow(TimePeriod.DAILY,date,-1,0,0).getBegin();
        System.out.println(">>>>>>> Date: " + DateUtils.toMarketDate(date) + " Today's Close: " + today.getClose());

        HistoricalData market = markets.get(--index);
        while ( market.getDate().after(beginDate) ){
            market = markets.get(index);

            double delta = (today.getClose() - market.getClose()) / market.getClose() * 100;
            System.out.println("--------- Date: " + DateUtils.toMarketDate(date) + " His: "  + DateUtils.toMarketDate(market.getDate()) + " Today's Close: " + today.getClose() + " His Close: " + market.getClose() + " Delta : " + delta + "%");

            if ( delta >= BULL_MARKET_TREND ){
                System.out.println(">>>>>>>Today:  " + DateUtils.toMarketDate(date) + " Bull. " + " Today Close: " + today.getClose() + " His: " + DateUtils.toMarketDate(market.getDate()) + " Close: " + market.getClose()  + " Delta: " + delta + "%");

                return Trend.UP;
            }else if ( delta <= BEAR_MARKET_TREND ){
                System.out.println(">>>>>>>Today:  " + DateUtils.toMarketDate(date) + " Bear. " + " Today Close: " + today.getClose() + " His: " + DateUtils.toMarketDate(market.getDate()) + " Close: " + market.getClose()  + " Delta: " + delta + "%");

                return Trend.DOWN;
            }

            index --;
        }

        return Trend.NONE;
    }


}
