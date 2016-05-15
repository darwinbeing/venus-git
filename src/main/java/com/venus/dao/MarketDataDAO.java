package com.venus.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.venus.domain.HistoricalData;
import com.venus.domain.enums.TimePeriod;
import com.venus.service.market.BeanContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by erix-mac on 16/1/10.
 */
@Repository
public class MarketDataDAO {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class MarketDataMapper implements RowMapper<HistoricalData> {
        public HistoricalData mapRow(ResultSet rs, int rowNum) throws SQLException {
            HistoricalData data = new HistoricalData();

            data.setPeriod(TimePeriod.DAILY);
            data.setCode(rs.getString("CODE"));
            data.setName(rs.getString("NAME"));
            data.setDate(rs.getDate("MKT_DATE"));
            data.setOpen(rs.getDouble("PRICE_OPEN"));
            data.setHigh(rs.getDouble("PRICE_HIGH"));
            data.setLow(rs.getDouble("PRICE_LOW"));
            data.setClose(rs.getDouble("PRICE_CLOSE"));
            data.setVolume(rs.getLong("VOLUME"));
            data.setVolumeAmount(rs.getDouble("VOLUME_AMT"));

            return data;
        }
    }

    public List<HistoricalData> getAllMarketData() {
        return this.jdbcTemplate.query("SELECT ID,CODE,NAME,MKT_DATE,PRICE_OPEN,PRICE_HIGH,PRICE_LOW, PRICE_CLOSE,VOLUME,VOLUME_AMT FROM venus.MKT_DAILY_DATA", new MarketDataMapper());
    }

    public List<HistoricalData> getAllMarketData(String code) {
        return this.jdbcTemplate.query("SELECT ID,CODE,NAME,MKT_DATE,PRICE_OPEN,PRICE_HIGH,PRICE_LOW, PRICE_CLOSE,VOLUME,VOLUME_AMT FROM venus.MKT_DAILY_DATA WHERE CODE=? AND MKT_DATE > '2005-01-01'", new Object[]{code}, new MarketDataMapper());
    }

    public HistoricalData getLatestMarketData(String code){
        String sql = "SELECT * FROM MKT_DAILY_DATA WHERE CODE = ? ORDER BY MKT_DATE DESC limit 1";
        return this.jdbcTemplate.queryForObject(sql,new Object[]{code}, new MarketDataMapper());
    }

    public void insert(final List<HistoricalData> datas){
        String sql = "insert into MKT_DAILY_DATA(CODE,NAME,MKT_DATE,PRICE_OPEN,PRICE_HIGH,PRICE_LOW, PRICE_CLOSE,VOLUME,VOLUME_AMT) VALUES(?,?,?,?,?,?,?,?,?)";

        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                HistoricalData d = datas.get(i);

                ps.setString(1,d.getCode());
                ps.setString(2,d.getName());
                ps.setDate(3, new Date(d.getDate().getTime()));
                ps.setDouble(4, d.getOpen());
                ps.setDouble(5,d.getHigh());
                ps.setDouble(6,d.getLow());
                ps.setDouble(7,d.getClose());
                ps.setLong(8,d.getVolume());
                ps.setDouble(9,d.getVolumeAmount());
            }

            @Override
            public int getBatchSize() {
                return datas.size();
            }
        });
    }


    public int getMarketCount(String code) {
        return this.jdbcTemplate.queryForObject("select count(*) from MKT_DAILY_DATA WHERE CODE=?", new Object[]{code}, Integer.class);
    }


    public static void main(String[] args) {

        MarketDataDAO dao = BeanContext.getBean(MarketDataDAO.class);

        int count = dao.getMarketCount("600577");

        System.out.println("Count: " + count);
    }
}
