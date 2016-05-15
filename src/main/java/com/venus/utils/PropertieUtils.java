package com.venus.utils;

import com.google.common.collect.Lists;
import com.venus.domain.Stock;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by erix-mac on 15/8/3.
 */
@Slf4j
public final class PropertieUtils {

    private final static String MARKET_PROP = "marketdata.properties";


    public static String getMarketProperty(String key){
        return getProperty(MARKET_PROP, key);
    }

    public static String getProperty(String propName, String key) {
        Properties prop = new Properties();

        InputStream in = PropertieUtils.class.getClassLoader().getResourceAsStream(propName);
        try {
            prop.load(in);

            return prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if ( in != null )
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.toString());
                }
        }

        return null;
    }

    public static List<Stock> getStockList(){
        String stockList = getMarketProperty(Constants.MARKET_STOCK_LIST);
        String[] stocks = stockList.split(Constants.MARKET_STOCK_SPLIT);

        List<Stock> list = Lists.newArrayList();

        for ( String s : stocks ){
            list.add(new Stock(s,s));
        }

        return list;
    }
}
