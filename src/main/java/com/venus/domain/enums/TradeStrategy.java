package com.venus.domain.enums;

/**
 * Created by erix-mac on 15/9/13.
 */
public enum TradeStrategy {

    NONE(-1),StopLossStrategy(0), HighLowMoveStrategy(1), KeepTransactionProfitStrategy(2),KeepTradeProfitStrategy(3), PercentageStrategy(4), TopBottomStrategy(5),
    LivermoreKeepProfit(100);

    private int value;

    private TradeStrategy(int value){
        this.value = value;
    }
}
