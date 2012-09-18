package performance;

import com.gamasoft.example.collections.ExchangeNull;
import com.gamasoft.example.collections.ExchangeSyncronized;
import com.gamasoft.example.collections.ExchangeUnsafe;
import com.gamasoft.example.model.*;
import com.google.common.collect.SortedMultiset;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.round;
import static org.junit.Assert.assertTrue;


@Category(PerformanceTests.class)
@RunWith(value = Parameterized.class)
public class SingleThreadPerformanceTest {

    public static final int STOCKS_NUMBER = 100;
    public static final int TRADERS_NUMBER = 100;
    public static final int BIDS_BLOCK = 5_000;
    public static final int TIMES = 100;
    private Trader[] traders = new Trader[TRADERS_NUMBER];
    private Stock[] stocks = new Stock[STOCKS_NUMBER];


    private Random priceGenerator = new Random( System.currentTimeMillis() );

    private Exchange exchange;

    public SingleThreadPerformanceTest(Exchange exchange) {
        System.out.flush();
        System.out.println("\n-----");
        System.out.println("Testing with " + exchange.getClass().getSimpleName() + "\n");
        this.exchange = exchange;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { new ExchangeNull() }, { new ExchangeUnsafe() }, { new ExchangeSyncronized() }};
        return Arrays.asList(data);
    }



    @Before
    public void setUp() throws Exception {

        for (int i = 0; i < STOCKS_NUMBER; i++) {
            stocks[i] = new Stock("S" + Integer.toHexString(i), "Stock " + i);
        }
        for (int i = 0; i < TRADERS_NUMBER; i++) {
            traders[i] = new Trader("Trader " + i);
        }
    }


    private void transactionVerification(Queue<Transaction> transactions) {
        for (Transaction transaction : transactions) {

            assertTrue(transaction.getBuy().getPrice() >= transaction.getSell().getPrice());
            assertTrue(transaction.getBuy().getStock().equals(transaction.getSell().getStock()));

            assertTrue(transaction.getBuy().getPrice() == transaction.getPrice() ||
                    transaction.getSell().getPrice() == transaction.getPrice());
        }
    }


    @Test
    public void buyAndSellManyTimes() throws Exception {


        for (int j = 0; j < TIMES; j++) {

            long start = System.nanoTime();
            for (int i = 0; i < BIDS_BLOCK; i++) {
                exchange.buy(randomTrader(), randomStock(), randomPrice());
                exchange.sell(randomTrader(), randomStock(), randomPrice());
            }
            long ms = (System.nanoTime() - start) / 1000;
            Queue<Transaction> transactions = exchange.getTransactions();

            int trans = transactions.size();
            transactionVerification(transactions);

            transactions.clear();
            System.out.println(j + " done " + BIDS_BLOCK * 2 + " bids in " + ms + " microsec.  (avg." + ms/(BIDS_BLOCK * 2.0) +" microsec.) transactions: " + trans + " (" + percent(trans) + "%)");

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


    private double percent(int trans) {
        return (100.0 * trans) / BIDS_BLOCK;
    }

    private Stock randomStock() {
        return stocks[ThreadLocalRandom.current().nextInt(STOCKS_NUMBER)];

    }

    private Trader randomTrader() {
        return traders[ThreadLocalRandom.current().nextInt(TRADERS_NUMBER)];
    }

    private double randomPrice() {
        return (int) round(10000.0 * priceGenerator.nextDouble()) / 10000.0 * 6 + 9;
    }
}
