package com.gamasoft.example.model;

import com.gamasoft.example.syncronized.ExchangeSyncronized;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExchangeTest {

    private Exchange exchange;
    private Trader traderA;
    private Trader traderB;

    @Before
    public void setUp() throws Exception {
        exchange = new ExchangeSyncronized();

        traderA = new Trader("Al");
        traderB = new Trader("Ben");

    }


    @Test
    public void acceptBid() throws Exception {

        Stock myStock = new Stock("ABC", "AB Corp");
        exchange.buy(traderA, myStock, 12);
        exchange.sell(traderB, myStock, 12);

        assertThat(exchange.getTransactions().size(), is( 1));
        assertThat(exchange.getTransactions().get(0), is(new Transaction(traderA, traderB, myStock, 12)));

    }

    @Test
    public void unacceptableBid() throws Exception {

        Stock myStock = new Stock("ABC", "AB Corp");
        exchange.buy(traderA, myStock, 10);
        exchange.sell(traderB, myStock, 12);

        assertThat(exchange.getTransactions().size(), is( 0));

        exchange.sell(traderB, myStock, 9);
        assertThat(exchange.getTransactions().size(), is( 1));
        assertThat(exchange.getTransactions().get(0), is(new Transaction(traderA, traderB, myStock, 9)));


    }

}
