package com.venus.domain.enums;

import com.google.common.collect.Lists;
import com.venus.domain.enums.Market;
import com.venus.domain.enums.MarketDataProvider;

import java.util.List;

/**
 * Created by erix-mac on 15/8/3.
 */
public enum StockIndex {
    ShanghaiCompositeIndex("Shanghai Index","000001", Market.Shanghai), ShenzhenComponentIndex("ShenzhenComponentIndex","399001",Market.Shenzhen), GrowthEnterpriseIndex("GrowthEnterpriseIndex","399006",Market.Shenzhen);

    private static final List<String> INDEXS = Lists.newArrayList("000001","399001","399006");

    private String name;
    private String code;
    private Market market;

    private StockIndex(String name, String code, Market market){
        this.name = name;
        this.code = code;
        this.market = market;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getCode(MarketDataProvider provider){
        return provider.getMarketCode(this.code);
    }

    public static boolean isIndex(String code){
        return INDEXS.contains(code);
    }

}
