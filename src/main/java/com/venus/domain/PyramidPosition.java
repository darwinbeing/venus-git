package com.venus.domain;

import com.venus.domain.enums.TradeDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by erix-mac on 15/10/19.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PyramidPosition {
    boolean isInitial = false;
    boolean isFullPosition = false;
    private int index = -1;
    private TradeDirection direction = TradeDirection.NONE;
    private long positions = -1;

}
