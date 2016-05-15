package com.venus.base;

import com.venus.domain.HistoricalData;
import lombok.SneakyThrows;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erix-mac on 15/8/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-app.xml" })
public abstract class AbstractTestCase {



    @SneakyThrows(ParseException.class)
    protected Date toHistoricalDate(String date){
        SimpleDateFormat format = new SimpleDateFormat(HistoricalData.MARKET_DATE_FORMAT);

        return format.parse(date);
    }
}
