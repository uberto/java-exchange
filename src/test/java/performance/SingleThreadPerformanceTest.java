package performance;

import com.gamasoft.example.collections.ExchangeLambda;
import com.gamasoft.example.collections.ExchangeNull;
import com.gamasoft.example.collections.ExchangeSyncronized;
import com.gamasoft.example.collections.ExchangeUnsafe;
import com.gamasoft.example.model.Exchange;
import com.gamasoft.example.model.Stock;
import com.gamasoft.example.model.Trader;
import com.gamasoft.example.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.gamasoft.example.model.ExchangeTest.transactionVerification;
import static com.gamasoft.example.model.ExchangeTest.verifyOrderedList;
import static java.lang.Math.round;


@Category(PerformanceTests.class)
@RunWith(value = Parameterized.class)
public class SingleThreadPerformanceTest {

    public static final int STOCKS_NUMBER = 100;
    public static final int TRADERS_NUMBER = 100;
    public static final int BIDS_BLOCK = 5_000;
    public static final int TIMES = 50;
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
        Object[][] data = new Object[][] { { new ExchangeNull() }, { new ExchangeUnsafe() }, { new ExchangeSyncronized() }, {new ExchangeLambda()}};
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
            System.out.flush();

            for (Stock stock : stocks) {
                verifyOrderedList(exchange.getBuyBidsList(stock));
                verifyOrderedList(exchange.getSellBidsList(stock));
            }

        }


//        outputResult();
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
