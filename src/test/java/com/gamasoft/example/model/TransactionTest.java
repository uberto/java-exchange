package com.gamasoft.example.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class TransactionTest {
    @Test
    public void testEquals() throws Exception {
        Stock stock = new Stock("A", "Abc");
        Trader trader = new Trader("Ben");
        Bid b1 = new Bid(1, trader, stock, 12);
        Bid b2 = new Bid(2, trader, stock, 13);
        Transaction t1 = new Transaction(b1, b2, 13);
        Transaction t2 = new Transaction(b1, b2, 13);
        Transaction t3 = new Transaction(b1, b2, 15);

        assertThat(t1.getPrice(), is(13.0));
        assertThat(t1.getBuy(), is(b1));
        assertThat(t1.getSell(), is(b2));
        assertThat(t1, is(t2));
        assertThat(t2, not(t3));

    }
}
