package performance;

import com.gamasoft.example.model.Bid;
import com.gamasoft.example.model.Stock;
import com.gamasoft.example.model.Trader;
import com.gamasoft.example.model.Transaction;
import com.gamasoft.example.syncronized.ExchangeSyncronized;
import com.google.common.collect.SortedMultiset;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.round;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SingleThreadPerformanceTest {

    public static final int STOCKS_NUMBER = 100;
    public static final int TRADERS_NUMBER = 100;
    public static final int BIDS_BLOCK = 500_000;
    public static final int TIMES = 10;
    private ExchangeSyncronized exchange;
    private Trader[] traders = new Trader[TRADERS_NUMBER];
    private Stock[] stocks = new Stock[STOCKS_NUMBER];


    private Random priceGenerator = new Random( System.currentTimeMillis() );

    @Before
    public void setUp() throws Exception {
        exchange = new ExchangeSyncronized();

        for (int i = 0; i < STOCKS_NUMBER; i++) {
            stocks[i] = new Stock("S" + Integer.toHexString(i), "Stock " + i);
        }
        for (int i = 0; i < TRADERS_NUMBER; i++) {
            traders[i] = new Trader("Trader " + i);
        }
    }

    @Test
    public void buyAndSellAFewTimes() throws Exception {
        Stock stock = stocks[0];
        for (int i = 0; i < 1000; i++) {
            exchange.buy(randomTrader(), stock, randomPrice());

            assertThat(exchange.getBuyBidsList(stock).size(), is(exchange.getSellBidsList(stock).size() + 1));

            exchange.sell(randomTrader(), stock, randomPrice());
            assertThat(exchange.getBuyBidsList(stock).size(), is(exchange.getSellBidsList(stock).size()));

            assertThat((exchange.getBuyBidsList(stock).size() + exchange.getSellBidsList(stock).size()) / 2 + exchange.getTransactions().size(), is(i + 1));
        }
//        assertThat(exchange.getTransactions().size(), is(24));
        System.out.println("getTransactions().size() " + exchange.getTransactions().size());


        verifyOrderedList(exchange.getBuyBidsList(stock));
        verifyOrderedList(exchange.getSellBidsList(stock));

        transactionVerification(exchange.getTransactions());
    }

    private void verifyOrderedList(SortedMultiset<Bid> buyList) {
        for (Bid bid : buyList) {

//            System.out.println("price " + bid.getPrice());
            assertTrue(bid.getPrice() >= buyList.firstEntry().getElement().getPrice());
            assertTrue(bid.getPrice() <= buyList.lastEntry().getElement().getPrice());
        }
    }

    private void transactionVerification(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {

            assertTrue(transaction.getBuy().getPrice() >= transaction.getSell().getPrice());
            assertTrue(transaction.getBuy().getStock().equals(transaction.getSell().getStock()));

            assertTrue(transaction.getBuy().getPrice() == transaction.getPrice() ||
                    transaction.getSell().getPrice() == transaction.getPrice());
        }
    }


    @Test
    public void buyAndSellManyTimes() throws Exception {
        System.out.println("\n\n-----");

        for (int j = 0; j < TIMES; j++) {

            long start = System.nanoTime();
            for (int i = 0; i < BIDS_BLOCK; i++) {
                exchange.buy(randomTrader(), randomStock(), randomPrice());
                exchange.sell(randomTrader(), randomStock(), randomPrice());
            }
            long ms = (System.nanoTime() - start) / 1000;
            List<Transaction> transactions = exchange.getTransactions();

            int trans = transactions.size();
            transactionVerification(transactions);

            transactions.clear();
            System.out.println(j + " done " + BIDS_BLOCK * 2 + " bids in " + ms + " microsec.  transactions: " + trans + " (" + percent(trans) + "%)");
        }


//        outputResult();
    }

    private void outputResult() {
        for (int i = 0; i < STOCKS_NUMBER; i++) {
            Stock stock = stocks[i];
            SortedMultiset<Bid> buyBidsList = exchange.getBuyBidsList(stock);
            System.out.println(" buy " + stock + "  " + buyBidsList.size() + "  at " + buyBidsList.lastEntry().getElement().getPrice() + " "+ buyBidsList.firstEntry().getElement().getPrice());
            SortedMultiset<Bid> sellBidsList = exchange.getSellBidsList(stock);
            System.out.println(" sell " + stock + "  "+ sellBidsList.size() + "  at " + sellBidsList.firstEntry().getElement().getPrice() + " " + sellBidsList.lastEntry().getElement().getPrice());

        }
    }


    private int percent(int trans) {
        return (100 * trans) / BIDS_BLOCK;
    }

    private Stock randomStock() {
        return stocks[ThreadLocalRandom.current().nextInt(STOCKS_NUMBER)];

    }

    private Trader randomTrader() {
        return traders[ThreadLocalRandom.current().nextInt(TRADERS_NUMBER)];
    }

    private double randomPrice() {
        return (int) round(10000.0 * priceGenerator.nextDouble()) / 10000.0 * 6 + 9;
//        return (int) round(10000.0 * ThreadLocalRandom.current().nextDouble(9, 15)) / 10000.0;
    }
}
