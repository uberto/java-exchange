package com.gamasoft.example.collections;

import com.gamasoft.example.model.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

public class ExchangeNull implements Exchange {
    private Bid nullBid = new Bid(0, new Trader("nullTrader"), new Stock("NN", "Null Stock"), 0);
    private Queue<Transaction> nullTransList = new LinkedList<>();
    private SortedSet<Bid> nullBidsSorted = new TreeSet<>();

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {
        return nullBid;
    }

    @Override
    public Bid buy(Trader trader, Stock stock, double maxPrice) {
        return nullBid;
    }

    @Override
    public Queue<Transaction> getTransactions() {
        return nullTransList;
    }

    @Override
    public SortedSet<Bid> getBuyBidsList(Stock stock) {
        return nullBidsSorted;
    }

    @Override
    public SortedSet<Bid> getSellBidsList(Stock stock) {
        return nullBidsSorted;
    }


}
