package com.venus.service.strategy.gann;

import com.google.common.collect.Lists;
import com.venus.domain.*;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.enums.TradeDirection;
import com.venus.domain.enums.TradeStrategy;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.strategy.AbstractTradeStrategy;
import com.venus.service.strategy.Strategy;
import com.venus.utils.MarketDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 15/9/23.
 */
@Slf4j
@Component
public class KeepTransactionProfitStrategy extends AbstractTradeStrategy implements Strategy {

    final static double MAX_PROFIT_RAIO = 8;
    final static double MAX_KEEP_PROFIT_RAIO = 4;

    final static double PROFIT_RAIO = 5;
    final static double KEEP_PROFIT_RAIO = 1.5;

    @Override
    public String getName() {
        return TradeStrategy.KeepTransactionProfitStrategy.toString();
    }

    @Override
    public List<TradeAction> analysis(TradeAnalysisInfo info){
        List<TradeAction> result = Lists.newArrayList();

        Stock stock = info.getStock();
        Date date = info.getAnalysisDate();
        Trade trade = info.getTrade();

        if ( trade.getPositions() == 0 )
            return result;


        List<Transaction> transactions = trade.getTransactions();

        for ( Transaction trans : transactions ){

            if ( trans.getDate().after(date) || !trans.getDirection().equals(TradeDirection.BUY) || trans.isClosed() ){
                continue;
            }


            if ( triggerKeepProfit(stock, date, trans) ) {
                //log.info("++++++++  triggerKeepProfit : " + trans.toString());
                TradeAction action = new TradeAction(TradeStrategy.KeepTransactionProfitStrategy,TradeDirection.SELL,stock,date,date);

                action.setTradeDate(date);
                action.setTradePrice(info.getMarketPrice());
                action.setOrignalTransPrice(trans.getPrice());
                action.setNettingHandled(true);
                trans.setClosed(true);
                trans.setCloseAction(action);

                result.add(action);
            }
        }

        return result;
    }

    private boolean triggerKeepProfit(Stock stock, Date date, Transaction trans){

        if ( trans.isClosed() || trans.getDate().equals(date) || trans.getPositions() <= 0)
            return false;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trans.getDate());
        calendar.add(Calendar.DATE, 1); // T + 1

        Date end = date;
        boolean maxProfitFlag = false;
        boolean minProfitFlag = false;

        while ( calendar.getTime().before(end) || calendar.getTime().equals(end) ) {
            Date marketDate = calendar.getTime();
            HistoricalData market = this.marketService.getMarketHistoricalData(stock.getCode(), TimePeriod.DAILY, marketDate);

            if (!MarketDataUtils.isTradingDate(market)) {
                calendar.add(Calendar.DATE, 1);
                continue;
            }

            double maxProfit = trans.getProfitPercentage(market.getHigh());

            if ( maxProfit >= MAX_PROFIT_RAIO && !maxProfitFlag ){
                maxProfitFlag = true;
            }else if ( maxProfit >= PROFIT_RAIO && !minProfitFlag ){
                minProfitFlag = true;
            }


            if ( maxProfitFlag || minProfitFlag ){
                market = this.marketService.getMarketHistoricalData(stock.getCode(), TimePeriod.DAILY, date);
                double minProfit = trans.getProfitPercentage(market.getLow());

                if ( maxProfitFlag && ( minProfit > 0 && minProfit <= MAX_KEEP_PROFIT_RAIO) ){
                    return true;
                }else if ( minProfitFlag && (minProfit > 0 && minProfit <= KEEP_PROFIT_RAIO) ){
                    return true;
                }else{
                    return false;
                }
            }

            calendar.add(Calendar.DATE, 1);
        }

        return false;
    }
}
