package com.venus.domain.vo;

import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * Created by erix-mac on 15/9/27.
 */
public interface CSVSupport {
    public String[] getCSVHeader();
    public String[] getCSVRecord();
}
