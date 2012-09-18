package performance;

import com.gamasoft.example.collections.ExchangeNull;
import com.gamasoft.example.collections.ExchangeSyncronized;
import com.gamasoft.example.model.*;
import com.google.common.collect.SortedMultiset;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.round;
import static org.junit.Assert.assertTrue;


@Category(PerformanceTests.class)
@RunWith(value = Parameterized.class)
public class MultiThreadPerformanceTest {

    public static final int THREAD_POOL_SIZE = 50;
    public static final int BIDS_BLOCK = 5_000;
    public static final int TIMES = 20;

    public static final int STOCKS_NUMBER = 100;
    public static final int TRADERS_NUMBER = 100;
    private Trader[] traders = new Trader[TRADERS_NUMBER];
    private Stock[] stocks = new Stock[STOCKS_NUMBER];

    private static final Executor taskExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private Exchange exchange;

    public MultiThreadPerformanceTest(Exchange exchange) {
        System.out.flush();
        System.out.println("\n-----");
        System.out.println("Testing Multithread with " + exchange.getClass().getSimpleName() + "\n");

        this.exchange = exchange;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { new ExchangeNull() }, { new ExchangeSyncronized() }};   //, { new ExchangeUnsafe() }
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


    private void verifyOrderedList(SortedMultiset<Bid> buyList) {
        for (Bid bid : buyList) {

//            System.out.println("price " + bid.getPrice());
            assertTrue(bid.getPrice() >= buyList.firstEntry().getElement().getPrice());
            assertTrue(bid.getPrice() <= buyList.lastEntry().getElement().getPrice());
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

            long ms = innerTest();

            Queue<Transaction> transactions = exchange.getTransactions();

            int trans = transactions.size();
            transactionVerification(transactions);

            transactions.clear();
            double bidsDone = BIDS_BLOCK * 2.0 * THREAD_POOL_SIZE;
            System.out.println(j + " done " + bidsDone + " bids in " + ms + " microsec.  (avg." + ms/(bidsDone) +" microsec.) transactions: " + trans + " (" + percent(trans) + "%)");

            System.out.flush();

            for (Stock stock : stocks) {
                verifyOrderedList(exchange.getBuyBidsList(stock));
                verifyOrderedList(exchange.getSellBidsList(stock));
            }
        }


//        outputResult();
    }

    private long innerTest() {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(THREAD_POOL_SIZE);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException("start latch interruption", e);
                }


                try {
                    for (int i = 0; i < BIDS_BLOCK; i++) {
                        exchange.buy(randomTrader(), randomStock(), randomPrice());
                        exchange.sell(randomTrader(), randomStock(), randomPrice());
                    }
                } finally {
                    endLatch.countDown();
                }

            }
        };



        for (int i = 0; i < THREAD_POOL_SIZE; i++) {

            taskExecutor.execute(task);
        }
        long start = System.nanoTime();

        startLatch.countDown();
        try {
            endLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("end latch interruption", e);
        }

        long timeElapsed = (System.nanoTime() - start) / 1000;
        return timeElapsed;
    }


    private double percent(int trans) {
        return (100.0 * trans) / (THREAD_POOL_SIZE * BIDS_BLOCK);
    }

    private Stock randomStock() {
        return stocks[ThreadLocalRandom.current().nextInt(STOCKS_NUMBER)];

    }

    private Trader randomTrader() {
        return traders[ThreadLocalRandom.current().nextInt(TRADERS_NUMBER)];
    }

    private double randomPrice() {
        return (int) round(10000.0 * ThreadLocalRandom.current().nextDouble(9, 15)) / 10000.0;
    }
}
