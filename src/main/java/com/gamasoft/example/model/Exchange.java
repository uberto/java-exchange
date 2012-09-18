package com.gamasoft.example.model;

import com.google.common.collect.SortedMultiset;

import java.util.Queue;

public interface Exchange {

    public Bid sell(Trader trader, Stock stock, double minPrice);

    public Bid buy(Trader trader, Stock stock, double maxPrice);

    public Queue<Transaction> getTransactions();

    public SortedMultiset<Bid> getBuyBidsList(Stock stock);

    public SortedMultiset<Bid> getSellBidsList(Stock stock);
}
