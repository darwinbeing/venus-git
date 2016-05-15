package com.venus.service.strategy.common;

import com.google.common.collect.Lists;
import com.venus.domain.*;
import com.venus.domain.enums.TradeDirection;
import com.venus.domain.enums.TradeStrategy;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.strategy.AbstractTradeStrategy;
import com.venus.service.strategy.Strategy;
import com.venus.utils.PropertieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 15/8/22.
 */
@Slf4j
@Component
public class StopLossStrategy extends AbstractTradeStrategy implements Strategy {

    final static String STOP_LOSS_PERCETAGE_KEY = "trade.transaction.stoploss";

    @Override
    public String getName() {
        return TradeStrategy.StopLossStrategy.toString();
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

        for ( Transaction tran : transactions ){

            if ( tran.getDate().after(date) || !tran.getDirection().equals(TradeDirection.BUY) || tran.isClosed() ){
                continue;
            }

            double stopLossPercentage = Double.parseDouble(PropertieUtils.getMarketProperty(STOP_LOSS_PERCETAGE_KEY));
            double stopLossPrice = tran.getPrice() * (100 + stopLossPercentage)/100;
            double currentProfit = tran.getProfitPercentage(info.getMarketPrice());

            if ( currentProfit <= stopLossPercentage ) {
                TradeAction action = new TradeAction(TradeStrategy.StopLossStrategy,TradeDirection.SELL,stock,date,date);

                action.setTradeDate(date);
                action.setTradePrice(stopLossPrice);
                action.setOrignalTransPrice(tran.getPrice());
                action.setNettingHandled(true);

                tran.setClosed(true);
                tran.setCloseAction(action);

                result.add(action);
            }
        }

        return result;
    }
}
