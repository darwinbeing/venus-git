package com.venus.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class AbstractMarketData extends Stock implements Comparable<AbstractMarketData>, Serializable {


    protected long id;
    protected Date date;
    protected double yesterdayClose;
    protected double open;
    protected double high;
    protected double low;
    protected double close;
    protected double adjClose;
    protected long volume;
    protected double volumeAmount;

    public double getPercentage(){
        if ( this.yesterdayClose == 0 )
            return 0;
        else
            return (this.close - this.yesterdayClose) * 100/this.yesterdayClose;
    }


    @Override
    public int compareTo(AbstractMarketData o) {
        return this.date.compareTo(o.getDate());
    }


}
