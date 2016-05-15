package com.venus.service.market;

import com.venus.base.AbstractTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class MarketServiceTest extends AbstractTestCase {

    @Autowired
    MarketService marketService;

    @Test
    public void testUpdateMarketDataIncremental() throws Exception {
        this.marketService.updateMarketData("600577");

    }
}