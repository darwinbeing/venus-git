package com.venus.service.strategy.gann;

import com.google.common.collect.Lists;
import com.venus.domain.HistoricalData;
import com.venus.domain.Stock;
import com.venus.domain.TradeAction;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.enums.TradeDirection;
import com.venus.domain.enums.TradeStrategy;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.strategy.AbstractTradeStrategy;
import com.venus.service.strategy.Strategy;
import com.venus.utils.MarketDataUtils;
import java.util.Date;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * Created by erix-mac on 15/9/5.
 */
@Component
public class HighLowMoveStrategy extends AbstractTradeStrategy implements Strategy{

    private final static double DELTA_PERCENTAGE = 5;
    private final static double DELTA_INDEX_PERCENTAGE = 3;


    @Override
    public String getName() {
        return TradeStrategy.HighLowMoveStrategy.toString();
    }

    @Override
    public List<TradeAction> analysis(TradeAnalysisInfo info){
        List<TradeAction> result = Lists.newArrayList();

        Stock stock = info.getStock();
        Date date = info.getAnalysisDate();

        List<HistoricalData> datas = this.marketService.getMarketHistoricalData(stock.getCode(), TimePeriod.DAILY);

        HistoricalData d0 = MarketDataUtils.getMarketCurrent(datas,date);
        HistoricalData p1 = MarketDataUtils.getMarketT(datas, date, -1);
        HistoricalData p2 = MarketDataUtils.getMarketT(datas,date,-2);
        HistoricalData p3 = MarketDataUtils.getMarketT(datas,date,-3);
        HistoricalData next  = this.getNextHistoricalDate(stock, datas, date);

        if ( d0 == null )
            return result;

        TradeAction action = new TradeAction(TradeStrategy.HighLowMoveStrategy,TradeDirection.NONE,stock,date, date);

        double delta = ((d0.getClose() - p3.getOpen())/p3.getOpen()) * 100;
        boolean isIndex = stock.getCode().indexOf("s") > -1;
       /* if ( isIndex && Math.abs(delta) > 3 )
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++: code " + stock.getCode() + " date: " + date.toString() + " delta: " + delta);*/
        boolean isDelta = isIndex ? (Math.abs(delta) >= DELTA_INDEX_PERCENTAGE) : (Math.abs(delta) >= DELTA_PERCENTAGE);


        if ( moveToHigh(p3, p2) && moveToHigh(p2, p1) && moveToHigh(p1, d0) && isDelta  ){
            action.setTradeDirection(TradeDirection.BUY);

        }else if (moveToLow(p3, p2) && moveToLow(p2, p1) && moveToLow(p1, d0) && isDelta ){
            action.setTradeDirection(TradeDirection.SELL);
        }

        if ( action.getTradeDirection().equals(TradeDirection.SELL) || action.getTradeDirection().equals(TradeDirection.BUY) ){
            Date nextTradeDate = next.getDate() == null ? MarketDataUtils.getNextTradeDate(date) : next.getDate();

            action.setTradeDate(nextTradeDate);
            action.setTradePrice(next.getOpen());
            action.setOrignalTransPrice(next.getOpen());

            result.add(action);
        }

        return result;
    }


    private boolean moveToHigh(HistoricalData d1, HistoricalData d2){

        if ( d1 == null || d2 == null )
            return false;

        return (d2.getOpen() > d1.getOpen()) && (d2.getClose() > d1.getClose());
    }

    private boolean moveToLow(HistoricalData d1, HistoricalData d2){
        if ( d1 == null || d2 == null )
            return false;

        return (d2.getOpen() < d1.getOpen()) && (d2.getClose() < d1.getClose());
    }

}
