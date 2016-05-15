package com.venus.service.strategy;

import com.google.common.collect.Lists;
import com.venus.service.strategy.common.StopLossStrategy;
import com.venus.service.strategy.gann.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by erix-mac on 15/9/5.
 *
 */
@Service
@Scope("prototype")
public class StrategeService {

    @Autowired
    private HighLowMoveStrategy highLowMoveStrategy;
    @Autowired
    private StopLossStrategy stopLossStrategy;
    @Autowired
    private KeepTradeProfitStrategy keepTradeProfitStrategy;
    @Autowired
    private KeepTransactionProfitStrategy keepTransactionProfitStrategy;
    @Autowired
    private PercentageStrategy percentageStrategy;
    @Autowired
    private TopBottomStrategy topBottomStrategy;

    //@Autowired
    //private LivermoreKeepProfit livermoreKeepProfit;


    public List<? extends Strategy> getTradingStrategys(){
        //return Lists.newArrayList(highLowMoveStrategy,keepTradeProfitStrategy,stopLossStrategy,percentageStrategy,topBottomStrategy);
        return Lists.newArrayList(highLowMoveStrategy,keepTradeProfitStrategy,stopLossStrategy,percentageStrategy);
    }

}
