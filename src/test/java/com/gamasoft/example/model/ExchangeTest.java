package com.gamasoft.example.model;

import com.gamasoft.example.syncronized.ExchangeSyncronized;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
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
        Bid b1 = exchange.buy(traderA, myStock, 12);
        Bid b2 =exchange.sell(traderB, myStock, 12);

        assertThat(exchange.getTransactions().size(), is( 1));
        assertThat(exchange.getTransactions().get(0), is(new Transaction(b1, b2, 12)));

    }

    @Test
    public void unacceptableBid() throws Exception {

        Stock myStock = new Stock("ABC", "AB Corp");
        Bid b1 = exchange.buy(traderA, myStock, 10);
        Bid b2 = exchange.sell(traderB, myStock, 12);

        assertThat(b2.getId(), not(b1.getId()));

        assertThat(exchange.getTransactions().size(), is( 0));

        Bid b3 = exchange.sell(traderB, myStock, 9);
        assertThat(exchange.getTransactions().size(), is( 1));
        assertThat(exchange.getTransactions().get(0), is(new Transaction(b1, b3, 9)));

        assertThat(exchange.getTransactions().get(0).toString(), is("Transaction{buy=Bid{id=90401895, trader=Trader{name='Al'}, stock=Stock{name='AB Corp', ticker='ABC'}, price=10.0}, sell=Bid{id=783977366, trader=Trader{name='Ben'}, stock=Stock{name='AB Corp', ticker='ABC'}, price=9.0}, price=9.0}"));


    }

}
