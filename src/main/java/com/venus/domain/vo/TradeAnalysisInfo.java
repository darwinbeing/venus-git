package com.venus.domain.vo;

import com.venus.domain.Stock;
import com.venus.domain.Trade;
import com.venus.domain.enums.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Created by erix-mac on 16/2/17.
 */
@AllArgsConstructor
@Data
public class TradeAnalysisInfo {

    private Trade trade;
    private Date analysisDate;
    private TimeWindow timeWindow;
    private TimePeriod period = TimePeriod.NONE;
    private double marketPrice = 0;

    public Stock getStock(){
        return this.getTrade().getStock();
    }
}
