package com.venus.service.strategy;

import com.venus.domain.*;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.market.MarketDataService;
import com.venus.utils.MarketDataUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 15/8/22.
 */
public abstract class AbstractTradeStrategy implements Strategy {
    @Autowired
    protected MarketDataService marketService;


    @Override
    public abstract List<TradeAction> analysis(TradeAnalysisInfo info);

    protected HistoricalData getCurrentMarket(String code, Date date){
        return this.marketService.getMarketHistoricalData(code,TimePeriod.DAILY,date);
    }

    protected HistoricalData getNextHistoricalDate(Stock stock, List<HistoricalData> datas, Date date){
        HistoricalData d0 = MarketDataUtils.getMarketCurrent(datas,date);
        HistoricalData d1 = MarketDataUtils.getMarketT(datas, date, 1);

        if ( d1 != null ){
            return d1;
        }else {

            if ( d0 == null ){
                System.out.println(">>>> Exception, Please sync-up with Market Data: " + date.toString());
                return null;
            }

            Date next = (d1 == null ? MarketDataUtils.getNextTradeDate(datas,d0.getDate()) : d1.getDate());
            double price = (d1 == null ? d0.getClose() : d1.getOpen());

            return new HistoricalData(next,TimePeriod.DAILY,price,d0.getClose(),stock);
        }
    }

}
