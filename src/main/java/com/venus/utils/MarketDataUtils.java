package com.venus.utils;

import com.google.common.collect.Lists;
import com.venus.domain.HistoricalData;
import com.venus.domain.enums.StockIndex;
import org.springframework.stereotype.Component;
import sun.jvm.hotspot.oops.Mark;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by erix-mac on 15/8/29.
 */
@Component
public final class MarketDataUtils {
    public static final String HOLIDAY_DATE_FORMAT = "MM-dd";
    private final static List<String> HOLIDAYS = Lists.newArrayList("01-01","01-02","01-03","04-04","04-05","04-06","05-01","05-02","05-03","10-01","10-02","10-03","10-04","10-05","10-06","10-07");


    public static List<HistoricalData> extractByDate(List<HistoricalData> datas, Date begin) {
        return extractByDate(datas, begin, null);
    }

    public static List<HistoricalData> extractByDate(List<HistoricalData> datas, Date begin, Date end) {

        List<HistoricalData> periodDatas = Lists.newArrayList();

        for (HistoricalData data : datas) {
            Date d = data.getDate();
            if (d.compareTo(begin) >= 0) {
                if (end == null || d.compareTo(end) <= 0) {
                    periodDatas.add(data);
                }
            }
        }

        Collections.sort(periodDatas);

        return periodDatas;
    }

    public static int indexOf(List<HistoricalData> datas, Date current) {

        if (datas == null)
            return -1;

        Collections.sort(datas);

        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getDate().equals(current)) {
                return i;
            } else {
                Date previous = (i == 0 ? datas.get(i).getDate() : datas.get(i - 1).getDate());
                Date after = (i == datas.size() - 1 ? datas.get(i).getDate() : datas.get(i + 1).getDate());

                if (current.after(previous) && current.before(after)) {
                    return i;
                }

            }
        }

        return -1;
    }


    public static HistoricalData getMarketCurrent(List<HistoricalData> datas, Date current) {
        return getMarketT(datas, current, 0);
    }

    public static Date getNextTradeDate(Date current){
        Date next = nextCalendarDate(current);

        while ( !isTradingDate(next) ){
            next = nextCalendarDate(current);
        }

        return next;
    }

    private static Date nextCalendarDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    public static Date getNextTradeDate(List<HistoricalData> datas, Date current) {
        int currIndex = indexOf(datas, current);
        int index = currIndex + 1;

        if (index > datas.size()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);

            calendar.add(Calendar.DATE, 1);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                calendar.add(Calendar.DATE, 2);
            } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, 1);
            }

            return calendar.getTime();
        }

        HistoricalData next = getMarketT(datas, current, 1);

        return  next == null ? null : next.getDate();
    }

    public static boolean isTradingDate(HistoricalData d){

        if ( d == null )
            return false;

        if ( !StockIndex.isIndex(d.getCode()) && (d.getVolume() == 0 && d.getOpen() == d.getClose() && d.getOpen() == d.getHigh() ) )
            return false;

        return isTradingDate(d.getDate());
    }

    public static boolean isTradingDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat(HOLIDAY_DATE_FORMAT);

        if ( HOLIDAYS.contains(format.format(date))){
            //System.out.println("HOLIDAY : " + HistoricalData.toMarketDate(date));
            return false;
        }

        return true;
    }

    public static HistoricalData getMarketT(List<HistoricalData> datas, Date current, int t) {

        int currIndex = indexOf(datas, current);

        if ( currIndex == -1 )
            return null;

        if (t == 0) {
            return datas.get(currIndex);
        }

        int index = currIndex + t;
        if (index < 0) {
            return datas.get(0);
        } else if (index >= datas.size()) {
            return null;
        } else {
            return datas.get(index);
        }
    }


}
