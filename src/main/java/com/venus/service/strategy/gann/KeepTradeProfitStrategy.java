package com.venus.service.strategy.gann;

import com.google.common.collect.Lists;
import com.venus.domain.Stock;
import com.venus.domain.Trade;
import com.venus.domain.TradeAction;
import com.venus.domain.Transaction;
import com.venus.domain.enums.TradeDirection;
import com.venus.domain.enums.TradeStrategy;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.strategy.AbstractTradeStrategy;
import com.venus.service.strategy.Strategy;
import com.venus.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 15/10/24.
 */
@Slf4j
@Component
public class KeepTradeProfitStrategy extends AbstractTradeStrategy implements Strategy{

    private final static double DELTA_100_PROFIT = 40;
    private final static double DELTA_50_PROFIT = 30;
    private final static double DELTA_30_PROFIT = 15;
    private final static double DELTA_20_PROFIT = 10;


    @Override
    public String getName() {
        return TradeStrategy.KeepTradeProfitStrategy.toString();
    }

    @Override
    public List<TradeAction> analysis(TradeAnalysisInfo info){
        List<TradeAction> result = Lists.newArrayList();

        Stock stock = info.getStock();
        Date date = info.getAnalysisDate();
        Trade trade = info.getTrade();

        if ( trade.getPositions() == 0 )
            return result;

        double currentProfit = trade.getProfit().getTotalProfitPercentage();
        double maxProfit = trade.getHighestProfit();
        if ( currentProfit > maxProfit ){
            trade.setHighestProfit(currentProfit);
        }else{
            if ( triggerKeepProfit(maxProfit,currentProfit) ){

                List<Transaction> transactions = trade.getTransactions();
                for ( Transaction trans : transactions ){
                    if ( trans.getDate().after(date) || !trans.getDirection().equals(TradeDirection.BUY) || trans.isClosed() ){
                        continue;
                    }

                    TradeAction action = new TradeAction(TradeStrategy.KeepTradeProfitStrategy,TradeDirection.SELL,stock,date,date);

                    action.setTradeDate(date);
                    action.setTradePrice(info.getMarketPrice());
                    action.setOrignalTransPrice(trans.getPrice());
                    action.setNettingHandled(true);
                    trans.setClosed(true);
                    trans.setCloseAction(action);

                    result.add(action);
                }

            }
        }

        return result;
    }

    private boolean triggerKeepProfit(double max, double current){

        if ( current <=0 )
            return false;

        boolean isTrigger = false;
        double delta = (max - current);

        if ( max >= 100 ){
            isTrigger = delta >= DELTA_100_PROFIT;
        }else if ( max >= 50 ){
            isTrigger = delta >= DELTA_50_PROFIT;
        }else if ( max <= 30){
            isTrigger = delta >= DELTA_30_PROFIT;
        }else if ( max <= 20){
            isTrigger = delta >= DELTA_20_PROFIT;
        }

        return isTrigger;
    }
}
