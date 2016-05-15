package com.venus.domain;

import com.venus.domain.enums.TimePeriod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erix-mac on 15/8/21.
 */
@Data
@Slf4j
public class HistoricalData extends AbstractMarketData {
    public static final String MARKET_DATE_FORMAT = "yyyy-MM-dd";
    public static final String LIVE_DATE_FORMAT = "M/dd/yyyy";


    protected TimePeriod period;

    public HistoricalData(){}

    public HistoricalData(Date date, double high){
        this.setDate(date);
        this.setHigh(high);
    }

    public HistoricalData(Date date,TimePeriod period, double open, double yesterdayClose, Stock stock){
        this.setDate(date);
        this.setPeriod(period);
        this.setOpen(open);
        this.setYesterdayClose(yesterdayClose);
        this.setCode(stock.getCode());
        this.setName(stock.getName());
    }

    @Override
    public String toString(){
        return super.toString() + (period == null ? "" : " TimePeriod: " + period.toString());
    }

    public static Date toMarketDate(String date){
        SimpleDateFormat format = new SimpleDateFormat(HistoricalData.MARKET_DATE_FORMAT);
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
           log.error("toMarket Date Error: " + e.toString());
        }

        return d;
    }

    public static String toMarketDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat(HistoricalData.MARKET_DATE_FORMAT);
        return format.format(date);
    }
}
