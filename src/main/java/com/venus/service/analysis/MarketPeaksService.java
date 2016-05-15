package com.venus.service.analysis;

import com.google.common.collect.Maps;
import com.venus.domain.MarketPeak;
import com.venus.domain.Stock;
import com.venus.domain.enums.Peak;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by erix-mac on 15/11/1.
 */
@Service
public class MarketPeaksService {

    private static Map<Stock, Map<Peak, List<MarketPeak>>> stockPeaks_1Year = Maps.newConcurrentMap();
    private static Map<Stock, Map<Peak, List<MarketPeak>>> stockPeaks_3Year = Maps.newConcurrentMap();


    @PostConstruct
    private void init() {
    }

}
