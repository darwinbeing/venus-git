package com.venus.domain.enums;

/**
 * Created by erix-mac on 15/8/1.
 */
public enum TimePeriod {

    DAILY("daily","d"),WEEKLY("weekly","w"),MONTHLY("monthly","m"),LIVE("live","live"),NONE("none","none");

    private String period;
    private String name;

    private TimePeriod(String name, String period){
        this.name = name;
        this.period = period;
    }

    public String getName(){
        return this.name;
    }

    public String getPeriod(){
        return this.period;
    }
}
