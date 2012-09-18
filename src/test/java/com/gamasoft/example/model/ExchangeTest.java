package com.gamasoft.example.model;

import com.gamasoft.example.collections.ExchangeSyncronized;
import com.gamasoft.example.collections.ExchangeUnsafe;
import com.google.common.collect.SortedMultiset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class ExchangeTest {

    public static final int NUMBER_OF_BIDS = 1000;
    private Trader traderA;
    private Trader traderB;

    private Exchange exchange;

    public ExchangeTest(Exchange exchange) {
        System.out.flush();
        System.out.println("Testing with " + exchange.getClass().getSimpleName());
        this.exchange = exchange;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { new ExchangeSyncronized() }, { new ExchangeUnsafe() }};
        return Arrays.asList(data);
    }


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

    @Test

    public void buyAndSellAFewTimes() throws Exception {
        Stock stock = new Stock("ABC", "AB Corp");
        for (int i = 0; i < NUMBER_OF_BIDS; i++) {
            exchange.buy(traderA, stock, buyPrice(i));

            assertThat(exchange.getBuyBidsList(stock).size(), is(exchange.getSellBidsList(stock).size() + 1));

            exchange.sell(traderB, stock, sellPrice(i));
            assertThat(exchange.getBuyBidsList(stock).size(), is(exchange.getSellBidsList(stock).size()));

            assertThat((exchange.getBuyBidsList(stock).size() + exchange.getSellBidsList(stock).size()) / 2 + exchange.getTransactions().size(), is(i + 1));
        }

        assertTrue(exchange.getBuyBidsList(stock).lastEntry().getElement().getPrice() < exchange.getSellBidsList(stock).firstEntry().getElement().getPrice());

        verifyOrderedList(exchange.getBuyBidsList(stock));
        verifyOrderedList(exchange.getSellBidsList(stock));

        assertThat(exchange.getTransactions().size(), is(NUMBER_OF_BIDS/2 - 1));
        System.out.println("getTransactions().size() " + exchange.getTransactions().size());

    }

    private double sellPrice(int i) {
        return 13 - (2.0 *i) / NUMBER_OF_BIDS;
    }

    private double buyPrice(int i) {
        return 10 + (2.0 *i) / NUMBER_OF_BIDS;
    }

    private void verifyOrderedList(SortedMultiset<Bid> bidList) {
        for (Bid bid : bidList) {

//            System.out.println("price " + bid.getPrice());
            assertTrue(bid.getPrice() >= bidList.firstEntry().getElement().getPrice());
            assertTrue(bid.getPrice() <= bidList.lastEntry().getElement().getPrice());
        }
    }

}
