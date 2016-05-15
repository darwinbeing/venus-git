package com.venus.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by erix-mac on 15/8/20.
 */
@Data
@NoArgsConstructor
public class Stock {
    private String code;
    private String name;

    public Stock(String code){
        this.code = code;
        this.name = code;
    }

    public Stock(String code, String name){
        this.code = code;
        this.name = name;
    }
}
