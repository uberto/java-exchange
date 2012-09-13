package com.gamasoft.example.model;

import com.gamasoft.example.collections.ExchangeSyncronized;
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
        Bid b2 = exchange.sell(traderB, myStock, 12);

        assertThat(b1.getId(), is(1L));
        assertThat(b2.getId(), is(2L));
        assertThat(exchange.getTransactions().size(), is(1));
        assertThat(exchange.getTransactions().get(0), is(new Transaction(b1, b2, 12)));

    }

    @Test
    public void unacceptableBids() throws Exception {

        Stock myStock = new Stock("ABC", "AB Corp");
        Bid b1 = exchange.buy(traderA, myStock, 10);
        Bid s1 = exchange.sell(traderB, myStock, 12);

        assertThat(s1.getId(), not(b1.getId()));

        assertThat(exchange.getTransactions().size(), is(0));

        Bid s2 = exchange.sell(traderB, myStock, 9);
        Bid s3 = exchange.sell(traderB, myStock, 15);
        assertThat(exchange.getTransactions().size(), is(1));
        assertThat(exchange.getTransactions().get(0), is(new Transaction(b1, s2, 10)));

        assertThat(exchange.getTransactions().get(0).toString(), is("Transaction{buy=Bid{id=1, trader=Trader{name='Al'}, stock=Stock{name='AB Corp', ticker='ABC'}, price=10.0}, sell=Bid{id=3, trader=Trader{name='Ben'}, stock=Stock{name='AB Corp', ticker='ABC'}, price=9.0}, price=10.0}"));


    }

    @Test
    public void getAlwaysTheLowestSell() throws Exception {

        Stock myStock = new Stock("ABC", "AB Corp");
        Bid s1 = exchange.sell(traderB, myStock, 9);
        Bid s2 = exchange.sell(traderB, myStock, 12);
        Bid s3 = exchange.sell(traderB, myStock, 10);
        Bid s4 = exchange.sell(traderB, myStock, 15);
        assertThat(exchange.getTransactions().size(), is(0));

        Bid b4 = exchange.buy(traderA, myStock, 12);
        assertThat(exchange.getTransactions().size(), is(1));
        Bid b5 = exchange.buy(traderA, myStock, 12);
        assertThat(exchange.getTransactions().size(), is(2));
        Bid b6 = exchange.buy(traderA, myStock, 12);
        assertThat(exchange.getTransactions().size(), is(3));

        assertThat(exchange.getTransactions().get(0).getPrice(), is(9.0));
        assertThat(exchange.getTransactions().get(0), is(new Transaction(b4, s1, 9)));
        assertThat(exchange.getTransactions().get(1), is(new Transaction(b5, s3, 10)));
        assertThat(exchange.getTransactions().get(2), is(new Transaction(b6, s2, 12)));

    }

    @Test
    public void ignoreOtherStocks() throws Exception {

        Stock abc = new Stock("ABC", "AB Corp");
        Stock jkl = new Stock("JKL", "JK Lmt");
        Bid b1 = exchange.buy(traderA, abc, 12);
        Bid s1 = exchange.sell(traderB, jkl, 12);
        assertThat(exchange.getTransactions().size(), is(0));
        Bid b2 = exchange.buy(traderA, jkl, 12);
        assertThat(exchange.getTransactions().size(), is(1));

        assertThat(exchange.getTransactions().get(0), is(new Transaction(b2, s1, 12)));

    }


}
