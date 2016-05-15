package com.venus.domain;

import junit.framework.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class HistoricalDataTest {

    @Test
    public void testSort() throws Exception {

        HistoricalData h1 = new HistoricalData();
        SimpleDateFormat format = new SimpleDateFormat(HistoricalData.MARKET_DATE_FORMAT);
        h1.setDate(format.parse("2015-08-17"));

        HistoricalData h2 = new HistoricalData();
        h2.setDate(format.parse("2015-08-19"));

        int order = h1.compareTo(h2);

        System.out.println(order);
        Assert.assertEquals(-1,order);

    }
}