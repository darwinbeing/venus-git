package com.venus.domain.enums;

/**
 * Created by erix-mac on 15/8/3.
 */
public enum Market {
    Shanghai, Shenzhen;

    private final static String SHANGHAI_PREFIX = "600";
    private final static String SHANGHAI_INDEX = "000001";


    public static Market getMarket( String stockCode ){
        if ( stockCode.startsWith(SHANGHAI_PREFIX) || SHANGHAI_INDEX.equals(stockCode) ) {
            return Market.Shanghai;
        }else{
            return Market.Shenzhen;
        }
    }
}
