package com.venus.service.strategy.livermore;

import com.google.common.collect.Lists;
import com.venus.domain.Stock;
import com.venus.domain.Trade;
import com.venus.domain.TradeAction;
import com.venus.domain.enums.TradeDirection;
import com.venus.domain.enums.TradeStrategy;
import com.venus.domain.vo.TradeAnalysisInfo;
import com.venus.service.strategy.AbstractTradeStrategy;
import com.venus.service.strategy.Strategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 16/5/7.
 */
@Slf4j
@Component
public class LivermoreKeepProfit extends AbstractTradeStrategy implements Strategy {

    private final static double PROFIT_100_DOUBLE = 100;


    @Override
    public String getName() {
        return TradeStrategy.LivermoreKeepProfit.toString();
    }

    @Override
    public List<TradeAction> analysis(TradeAnalysisInfo info) {
        List<TradeAction> result = Lists.newArrayList();

        Stock stock = info.getStock();
        Date date = info.getAnalysisDate();
        Trade trade = info.getTrade();

        if (trade.getPositions() == 0)
            return result;

        double currentProfit = trade.getProfit().getTotalProfitPercentage();
        boolean isDoubled = currentProfit >= PROFIT_100_DOUBLE;

        if ( isDoubled ) {

            TradeAction action = new TradeAction(TradeStrategy.KeepTradeProfitStrategy, TradeDirection.SELL, stock, date, date);

            action.setTradeDate(date);
            action.setTradePrice(info.getMarketPrice());
            action.setNettingHandled(false);

            long positions = (long)((trade.getProfit().getProfit() / 2.0) / info.getMarketPrice());
            action.setPositions(positions);

            System.out.println("#####$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  Livermore Keep Profit : " + positions);
        }

        return result;
    }
}
