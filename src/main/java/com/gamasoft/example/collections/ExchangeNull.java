package com.gamasoft.example.collections;

import com.gamasoft.example.model.*;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import java.util.ArrayList;
import java.util.List;

public class ExchangeNull implements Exchange {
    private Bid nullBid = new Bid(0, new Trader("nullTrader"), new Stock("NN", "Null Stock"), 0);
    private List<Transaction> nullTransList = new ArrayList<>();
    private SortedMultiset<Bid> nullBidsSorted = TreeMultiset.create();

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {
        return nullBid;
    }

    @Override
    public Bid buy(Trader trader, Stock stock, double maxPrice) {
        return nullBid;
    }

    @Override
    public List<Transaction> getTransactions() {
        return nullTransList;
    }

    @Override
    public SortedMultiset<Bid> getBuyBidsList(Stock stock) {
        return nullBidsSorted;
    }

    @Override
    public SortedMultiset<Bid> getSellBidsList(Stock stock) {
        return nullBidsSorted;
    }
}
