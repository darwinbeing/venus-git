package com.venus.service.strategy.index;

import com.venus.base.AbstractTestCase;
import com.venus.domain.HistoricalData;
import com.venus.domain.enums.TimePeriod;
import com.venus.domain.enums.Trend;
import com.venus.domain.vo.TimeWindow;
import com.venus.utils.DateUtils;
import com.venus.utils.MarketDataUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.venus.domain.HistoricalData.toMarketDate;

public class IndexTrendStrategyTest extends AbstractTestCase {

    @Autowired
    private IndexTrendStrategy indexTrendStrategy;


    @Test
    public void testBullIndexTrend() {

        String mktDate = "2015-07-02";
        Date date = DateUtils.toMarketDate(mktDate);

        Trend trend = this.indexTrendStrategy.anaysisIndexTrend(date);

        System.out.println(">>>>>>>>  Index Trend: " + mktDate + " Trend: " + trend.toString());
    }


    @Test
    public void testFull2015IndexTrend() {
        final TimeWindow timeWindow = new TimeWindow(toMarketDate("2014-01-01"), toMarketDate("2014-02-01"), TimePeriod.DAILY);

        Date begin = timeWindow.getBegin();
        Date end = timeWindow.getEnd();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(begin);

        while (calendar.getTime().before(end)) {
            Trend trend = this.indexTrendStrategy.anaysisIndexTrend(calendar.getTime());

            calendar.add(Calendar.DATE, 1);
        }
    }
}