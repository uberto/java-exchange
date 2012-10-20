package com.gamasoft.example.model;

import com.gamasoft.example.collections.ExchangeLambda;
import com.gamasoft.example.collections.ExchangeSyncronized;
import com.gamasoft.example.collections.ExchangeUnsafe;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.SortedSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;

@RunWith(value = Parameterized.class)
public class ExchangeTest {

    public static final int NUMBER_OF_BIDS = 1000;
    private Trader traderA;
    private Trader traderB;

    private final Exchange exchange;

    public ExchangeTest(Class<Exchange> exchangeClass) throws IllegalAccessException, InstantiationException {
        System.out.flush();
        System.out.println("Testing with " + exchangeClass.getSimpleName());
        this.exchange = exchangeClass.newInstance();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{{ExchangeSyncronized.class}, {ExchangeUnsafe.class}, {ExchangeLambda.class}};
        return Arrays.asList(data);
    }


    @Before
    public void setUp() throws Exception {
        traderA = new Trader("Al");
        traderB = new Trader("Ben");

    }


    @Test
    public void acceptEqualBid() throws Exception {

        Stock myStock = new Stock("ABC", "AB Corp");
        Bid b1 = exchange.buy(traderA, myStock, 12);
        Bid b2 = exchange.sell(traderB, myStock, 12);

        assertThat(b1.getId(), is(1L));
        assertThat(b2.getId(), is(2L));
        assertThat(exchange.getTransactions().size(), is(1));
        assertThat(exchange.getTransactions().poll(), is(new Transaction(b1, b2, 12)));

    }


    @Test
    public void acceptBid() throws Exception {

        Stock myStock = new Stock("ABC", "AB Corp");
        Bid b1 = exchange.buy(traderA, myStock, 12);
        Bid b2 = exchange.sell(traderB, myStock, 10);

        assertThat(b1.getId(), is(1L));
        assertThat(b2.getId(), is(2L));
        assertThat(exchange.getTransactions().size(), is(1));
        assertThat(exchange.getTransactions().poll(), is(new Transaction(b1, b2, 12)));

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
        assertThat(exchange.getTransactions().peek(), is(new Transaction(b1, s2, 10)));

        assertThat(exchange.getTransactions().peek().toString(), is("Transaction{buy=Bid{id=1, trader=Trader{name='Al'}, stock=Stock{name='AB Corp', ticker='ABC'}, price=10.0}, sell=Bid{id=3, trader=Trader{name='Ben'}, stock=Stock{name='AB Corp', ticker='ABC'}, price=9.0}, price=10.0}"));


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

        assertThat(exchange.getTransactions().peek().getPrice(), is(9.0));
        assertThat(exchange.getTransactions().poll(), is(new Transaction(b4, s1, 9)));
        assertThat(exchange.getTransactions().poll(), is(new Transaction(b5, s3, 10)));
        assertThat(exchange.getTransactions().poll(), is(new Transaction(b6, s2, 12)));

    }

    @Test
    public void getAlwaysTheHighestBuy() throws Exception {

        Stock myStock = new Stock("ABC", "AB Corp");
        Bid b1 = exchange.buy(traderB, myStock, 15);
        Bid b2 = exchange.buy(traderB, myStock, 12);
        Bid b3 = exchange.buy(traderB, myStock, 13);
        Bid b4 = exchange.buy(traderB, myStock, 11);
        assertThat(exchange.getTransactions().size(), is(0));

        Bid s1 = exchange.sell(traderA, myStock, 12);
        assertThat(exchange.getTransactions().size(), is(1));
        Bid s2 = exchange.sell(traderA, myStock, 12);
        assertThat(exchange.getTransactions().size(), is(2));
        Bid s3 = exchange.sell(traderA, myStock, 12);
        assertThat(exchange.getTransactions().size(), is(3));

        assertThat(exchange.getTransactions().peek().getPrice(), is(15.0));
        assertThat(exchange.getTransactions().poll(), is(new Transaction(b1, s1, 15)));
        assertThat(exchange.getTransactions().poll(), is(new Transaction(b3, s2, 13)));
        assertThat(exchange.getTransactions().poll(), is(new Transaction(b2, s3, 12)));

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

        assertThat(exchange.getTransactions().poll(), is(new Transaction(b2, s1, 12)));

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

        assertThat(exchange.getBuyBidsList(stock).last().getPrice(), lessThan(exchange.getSellBidsList(stock).first().getPrice()));

        verifyOrderedList(exchange.getBuyBidsList(stock));
        verifyOrderedList(exchange.getSellBidsList(stock));

        assertThat(exchange.getTransactions().size(), is(NUMBER_OF_BIDS / 2 - 1));
        System.out.println("getTransactions().size() " + exchange.getTransactions().size());

    }

    private double sellPrice(int i) {
        return 13 - (2.0 * i) / NUMBER_OF_BIDS;
    }

    private double buyPrice(int i) {
        return 10 + (2.0 * i) / NUMBER_OF_BIDS;
    }


    public static void verifyOrderedList(SortedSet<Bid> bids) {
        if (bids.size() > 0) {
            double firstPrice = bids.first().getPrice();
            double lastPrice = bids.last().getPrice();
            for (Bid bid : bids) {
                MatcherAssert.assertThat(bid.getPrice(), greaterThanOrEqualTo(firstPrice));
                MatcherAssert.assertThat(bid.getPrice(), lessThanOrEqualTo(lastPrice));
            }
        }
    }

    public static void transactionVerification(Queue<Transaction> transactions) {
        for (Transaction transaction : transactions) {

            MatcherAssert.assertThat(transaction.getBuy().getStock(), is(transaction.getSell().getStock()));

            double buyPrice = transaction.getBuy().getPrice();
            double sellPrice = transaction.getSell().getPrice();
            double transactionPrice = transaction.getPrice();


            MatcherAssert.assertThat(buyPrice, greaterThanOrEqualTo(sellPrice));
            MatcherAssert.assertThat(transactionPrice, anyOf(
                    closeTo(buyPrice, 0.0001),
                    closeTo(sellPrice, 0.0001)));
        }
    }

    public static void outputResult(Stock[] stocks, Exchange exchange) {

        for (Stock stock : stocks) {
            SortedSet<Bid> buyBidsList = exchange.getBuyBidsList(stock);
            System.out.println(" buy " + stock + "  " + buyBidsList.size() + "  at " + buyBidsList.last().getPrice() + " " + buyBidsList.first().getPrice());
            SortedSet<Bid> sellBidsList = exchange.getSellBidsList(stock);
            System.out.println(" sell " + stock + "  " + sellBidsList.size() + "  at " + sellBidsList.first().getPrice() + " " + sellBidsList.last().getPrice());

        }
    }


}
