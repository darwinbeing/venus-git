package com.venus.domain.enums;

/**
 * Created by erix-mac on 15/8/16.
 */
public enum TradeDirection {
    SELL(-1),NONE(0),BUY(1);

    private int value;

    private TradeDirection(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public boolean splitAdjustable(){
        return new Integer(1).equals(this.getValue());
    }
}
