package com.venus.service.strategy;

import com.venus.domain.PyramidPosition;
import com.venus.domain.Trade;
import com.venus.domain.TradeAction;

/**
 * Created by erix-mac on 15/10/20.
 */
public interface PositionStrategy {

    public static final long FIXED_POSITIONS = 800;

    PyramidPosition analysis(Trade trade, TradeAction action);
}
