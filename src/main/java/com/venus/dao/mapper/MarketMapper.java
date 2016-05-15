package com.venus.dao.mapper;

import com.venus.domain.HistoricalData;

import java.util.List;

/**
 * Created by erix-mac on 16/1/11.
 */
public interface MarketMapper {

    public List<HistoricalData> getAllHistoryData();
}
