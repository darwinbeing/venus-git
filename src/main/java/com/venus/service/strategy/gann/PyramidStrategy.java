package com.venus.service.strategy.gann;

import com.venus.domain.PyramidPosition;
import com.venus.domain.Trade;
import com.venus.domain.TradeAction;
import com.venus.domain.enums.TradeDirection;
import com.venus.service.strategy.PositionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by erix-mac on 15/10/18.
 */
@Slf4j
@Component
public class PyramidStrategy implements PositionStrategy{

    public static double[] BUY_PYRAMID = {0.5, 0.3, 0.2};
    public static double[] SELL_PYRAMMID = {1};


    public static void updatePyramidArray(double[] buy, double[] sell){
        BUY_PYRAMID = buy;
        SELL_PYRAMMID = sell;
    }

    @Override
    public PyramidPosition analysis(Trade trade, TradeAction action) {
        PyramidPosition pyramid = trade.getPyramidPosistion();

        int nextIndex = this.nextIndex(action.getTradeDirection(), pyramid);
        double ratio = this.nextPyramidValue(action.getTradeDirection(), nextIndex);

        long position;

        if ( action.getTradeDirection().equals(TradeDirection.BUY) ){
            position = (long)((trade.getCapital() * ratio) / action.getTradePrice());
        } else {
            position = (long)(trade.getPositions() * ratio);
        }


        pyramid.setIndex(nextIndex);
        pyramid.setDirection(action.getTradeDirection());
        pyramid.setPositions(position);

        //log.info(">>>>>>> Pyramid : " + action.getTradeDirection().toString() + " positions: " + position + " capital: " + trade.getCapital());
        return pyramid;
    }


    private double nextPyramidValue(TradeDirection tradeDirection, int index) {
        if ( index < 0 )
            return 0;

        return tradeDirection.equals(TradeDirection.BUY) ? BUY_PYRAMID[index] : SELL_PYRAMMID[index];
    }

    private int nextIndex(TradeDirection tradeDirection, PyramidPosition position) {

        if ( !position.isInitial() ){
            position.setInitial(true);
            return 0;
        }

        if (tradeDirection.equals(position.getDirection())) {
            if ( position.isFullPosition() )
                return -1;

            int lastIndex = (tradeDirection.equals(TradeDirection.BUY) ? BUY_PYRAMID.length : SELL_PYRAMMID.length) - 1;
            position.setFullPosition((position.getIndex() + 1) >= lastIndex);

            if ( position.getIndex() + 1 < lastIndex) {
                return position.getIndex() + 1;
            } else if ( position.getIndex() + 1 == lastIndex ){
                return position.getIndex() + 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

}
