/*
package com.venus.dao;

import com.venus.dao.mapper.MarketMapper;
import com.venus.domain.HistoricalData;
import com.venus.service.market.BeanContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

*/
/**
 * Created by erix-mac on 16/1/12.
 *//*

@Repository
public class MyBatisDAO {

    @Autowired
    private MarketMapper marketMapper;

    public List<HistoricalData> getAllHistoryData(){
        return this.marketMapper.getAllHistoryData();
    }

    public static void main(String[] args){

        MyBatisDAO dao = BeanContext.getBean(MyBatisDAO.class);

        List<HistoricalData> marketDatas = dao.getAllHistoryData();

        for ( HistoricalData data : marketDatas){
            System.out.println("HistoricalData: " + data.toString());

        }

        System.out.println("END");

    }
}
*/
