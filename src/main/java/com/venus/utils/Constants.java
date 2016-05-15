package com.venus.utils;

import static com.venus.utils.PropertieUtils.getMarketProperty;

/**
 * Created by erix-mac on 15/8/6.
 */
public final class Constants {
    public final static String MARKET_STOCK_SPLIT = ",";

    public final static String MARKET_STOCK_LIST = "market.stock";
    private final static String MARKET_STOCK_LOCATION = "market.location";
    private final static String MARKET_BACK_TEST_LOCATION = "trade.backtest.location";
    private final static String MARKET_BACK_TEST_CASE_LOCATION = "trade.backtest.test.location";
    private final static String TRADE_DAILY_REPORT_LOCATION = "trade.daily.report.location";


    public static final String MARKET_FILE_LOCATION = getMarketProperty(MARKET_STOCK_LOCATION);
    public static final String MARKET_FILE_BACK_TEST_LOCATION = getMarketProperty(MARKET_BACK_TEST_LOCATION);
    public static final String MARKET_FILE_BACK_TEST_CASE_LOCATION = getMarketProperty(MARKET_BACK_TEST_CASE_LOCATION);

    public static final String TRADE_FILE_DAILY_REPORT_LOCATION = getMarketProperty(TRADE_DAILY_REPORT_LOCATION);


    public static final double getTradeCommission(){
        return Double.parseDouble(getMarketProperty("trade.commission"));
    }

    public static final double getTradeStampTax(){
        return Double.parseDouble(getMarketProperty("trade.stamp.tax"));
    }

}
