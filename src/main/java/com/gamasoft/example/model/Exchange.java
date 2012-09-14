package com.gamasoft.example.model;

import com.google.common.collect.SortedMultiset;

import java.util.List;

public interface Exchange {

    public Bid sell(Trader trader, Stock stock, double minPrice);

    public Bid buy(Trader trader, Stock stock, double maxPrice);

    public List<Transaction> getTransactions();

    public SortedMultiset<Bid> getBuyBidsList(Stock stock);

    public SortedMultiset<Bid> getSellBidsList(Stock stock);
}
