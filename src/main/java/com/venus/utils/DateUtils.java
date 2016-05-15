package com.venus.utils;

import com.venus.domain.enums.TimePeriod;
import com.venus.domain.vo.TimeWindow;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by erix-mac on 15/12/6.
 */
@Slf4j
public class DateUtils {
    public static final String MARKET_DATE_FORMAT = "yyyy-MM-dd";


    public static Date nextDate(Date curr, int next){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curr);
        calendar.add(Calendar.DATE, next);

        return calendar.getTime();
    }

    public static int daysBetween(Date begin, Date end) {

        if ( begin == null || end == null )
            return -1;

        long beginTime = begin.getTime();
        long endTime  = end.getTime();

        return (int)((endTime - beginTime) / (1000 * 60 * 60 *24) + 0.5);
    }


    public static Date toMarketDate(String date){
        SimpleDateFormat format = new SimpleDateFormat(MARKET_DATE_FORMAT);
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            log.error("toMarket Date Error: " + e.toString());
        }

        return d;
    }

    public static String toMarketDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat(MARKET_DATE_FORMAT);
        return format.format(date);
    }

    public static Date randomDate(Date begin, Date end){
        long date = begin.getTime() + (long)(Math.random() * (end.getTime() - begin.getTime()));
        return new Date(date);
    }

    public static int extractYear(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.YEAR);
    }

    public static int extractMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.MONTH) + 1;
    }

    public static int extractQuarter(Date date){
        return toQuarter(extractMonth(date));
    }

    public static int getCurrentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getCurrentMonth(){
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentQuarter(){
        return toQuarter(getCurrentMonth());
    }

    public static TimeWindow getTimeWindow(int month){
        Date today = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, month);

        System.out.println(">>>>>>  Date : " + calendar.toString());

        return TimeWindow.getTimeWindow(calendar.getTime(), today);
    }


    private static int toQuarter(int month){
        if ( month < 1 || month > 12 )
            return -1;

        int q = -1;
        if ( month == 1 || month == 2 || month == 3 ){
            q = 1;
        } else if ( month == 4 || month == 5 || month == 6 ){
            q = 2;
        } else if ( month == 7 || month == 8 || month == 9 ){
            q = 3;
        } else if ( month == 10 || month == 11 || month == 12 ){
            q = 4;
        }

        return q;

    }

}
