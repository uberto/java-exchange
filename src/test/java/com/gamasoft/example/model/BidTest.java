package com.gamasoft.example.model;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class BidTest {
    @Test
    public void testEquals() throws Exception {
        Stock stock = new Stock("A", "Abc");
        Trader trader = new Trader("Ben");
        Bid b1 = new Bid(1, trader, stock, 12);
        Bid b2 = new Bid(1, trader, stock, 12);
        Bid b3 = new Bid(2, trader, stock, 13);

        assertThat(b1.getId(), is(1L));
        assertThat(b1.getTrader(), is(new Trader("Ben")));
        assertThat(b1.getStock(), is(new Stock("A", "Abc")));
        assertThat(b1, is(b2));
        assertThat(b2, not(b3));

    }

    @Test
    public void testHash() throws Exception {

        Map<Bid, String> bids = new HashMap<>();

        Stock stock = new Stock("A", "Abc");
        Trader trader = new Trader("Ben");

        bids.put(new Bid(1, trader, stock, 123), "sample");

        assertThat(bids.get(new Bid(1, trader, stock, 123)), is("sample"));
        assertThat(bids.get(new Bid(1, trader, stock, 124)), nullValue());

    }
}
