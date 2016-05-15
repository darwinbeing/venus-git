package com.venus.service.strategy;

import com.venus.domain.*;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import com.venus.domain.vo.TradeAnalysisInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 15/8/20.
 */
public interface Strategy {
    String getName();
    List<TradeAction> analysis(TradeAnalysisInfo info);
}
