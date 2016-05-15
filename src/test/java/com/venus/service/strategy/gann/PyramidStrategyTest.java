package com.venus.service.strategy.gann;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.venus.base.AbstractTestCase;
import com.venus.domain.LiveData;
import com.venus.domain.Trade;
import com.venus.domain.vo.TimeWindow;
import com.venus.service.trade.TraderService1;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static com.venus.service.trade.TraderServiceTest.*;

import java.util.List;
import java.util.Map;

@Slf4j
public class PyramidStrategyTest extends AbstractTestCase{

    @Autowired
    TraderService1 traderService;

    private final static long capital  = 50000;

    @Test
    public void testPyramidBestStrategy() throws Exception {

        TimeWindow window = full_2015_callapse;

        double[][] BUY = {
                new double[]{0.5,0.4,0.1},
                new double[]{0.5,0.3,0.2},
                new double[]{0.3,0.3,0.3}
        };

        double[][] SELL = {
                new double[]{1},
                new double[]{0.6,0.4},
                new double[]{0.5,0.5},
        };

        Map<String,List<Trade>> trades = Maps.newHashMap();

        for ( int i=0;i<BUY.length;i++ ){
            for ( int j=0;j<SELL.length;j++ ){
                PyramidStrategy.updatePyramidArray(BUY[i],SELL[j]);
                List<Trade> result = this.traderService.autoTrade(window, capital);

                String key = toKeyString(BUY[i]) + " | " + toKeyString(SELL[j]);
                trades.put(key,result);
            }
        }

        for (Map.Entry<String, List<Trade>> trade : trades.entrySet()) {
            System.out.print("{ " + trade.getKey() + " }");
            printTradeResult(window, trade.getValue());
        }
    }

    private String toKeyString(double[] data){
        StringBuilder sb = new StringBuilder();
        for ( double d : data ){
            sb.append(d).append("_");
        }

        return sb.toString();
    }

}