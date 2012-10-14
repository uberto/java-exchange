package com.gamasoft.example.model;

import java.util.Queue;
import java.util.SortedSet;

public interface Exchange {

    public Bid sell(Trader trader, Stock stock, double minPrice);

    public Bid buy(Trader trader, Stock stock, double maxPrice);

    public Queue<Transaction> getTransactions();

    public SortedSet<Bid> getBuyBidsList(Stock stock);

    public SortedSet<Bid> getSellBidsList(Stock stock);

    public String getName() default {
        return getClass().getSimpleName();
    }
}
