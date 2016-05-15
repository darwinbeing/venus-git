package com.venus.service.trade;

import com.venus.domain.*;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.market.MarketDataService;
import com.venus.service.strategy.StrategeService;
import com.venus.service.strategy.Strategy;
import com.venus.service.strategy.gann.PyramidStrategy;
import com.venus.utils.MarketDataUtils;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 16/2/20.
 */
@Slf4j
@Data
public class TradeManager {

    private Stock stock;
    private TimeWindow timeWindow;
    private TimePeriod period;
    private double totalCapital;

    @Setter
    private StrategeService strategeService;
    @Setter
    private MarketDataService marketService;
    @Setter
    private PyramidStrategy pyramidStrategy;
    @Setter
    private Dividends dividends;


    public TradeManager(Stock stock, TimeWindow timeWindow, TimePeriod period, double totalCapital){
        this.stock = stock;
        this.timeWindow = timeWindow;
        this.period = period;
        this.totalCapital = totalCapital;
    }

    public Trade trade(){
        Trade trade = new Trade(stock, -1, totalCapital);

        Date begin = timeWindow.getBegin();
        Date end = timeWindow.getEnd();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);

        while ( calendar.getTime().before(this.timeWindow.getEnd()) ) {
            Date marketDate = calendar.getTime();
            if (!MarketDataUtils.isTradingDate(marketDate)) {
                calendar.add(Calendar.DATE, 1);
                continue;
            }

            HistoricalData market = this.marketService.getMarketHistoricalData(stock.getCode(), period, marketDate);
            if ( dividends.isSplitDate(stock, marketDate) ){
                log.info("++++++++++++ Before Split Date, Adjust Dividends Split : " + stock.getCode() + " : " + HistoricalData.toMarketDate(marketDate) + " Positions: " + trade.getPositions());
                dividends.adjustTradeDividendsSplit(trade, marketDate,market);
                log.info("++++++++++++ After Split Date, Adjust Dividends Split : " + stock.getCode() + " : " + HistoricalData.toMarketDate(marketDate) + " Positions: " + trade.getPositions());
            }

            this.applyStrategy(marketDate, trade, timeWindow, market);

            calendar.add(Calendar.DATE, 1);
        }

        HistoricalData endMarket = this.marketService.getMarketHistoricalData(stock.getCode(), period, end);
        if ( endMarket != null ){
            trade.setMarketPrice(endMarket.getClose());
            trade.getStock().setName(endMarket.getName());
        }

        return trade;
    }



    private void applyStrategy(Date date, Trade trade, TimeWindow timeWindow, HistoricalData market) {
        if ( market == null )
            return;

        List<? extends Strategy> strategies = this.strategeService.getTradingStrategys();
        trade.setMarketPrice(market.getClose());
        for (Strategy s : strategies) {
            List<TradeAction> actions = s.analysis(new TradeAnalysisInfo(trade,date, timeWindow, market.getPeriod(), market.getClose()));
            trade.trade(actions,this.pyramidStrategy);
        }
    }

}
