package com.gamasoft.example.collections;

import com.gamasoft.example.model.*;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ExchangeSyncronized implements Exchange {

    private Map<Stock, SortedMultiset<Bid>> sellBids = new ConcurrentHashMap<>();
    private Map<Stock, SortedMultiset<Bid>> buyBids = new ConcurrentHashMap<>();
    private Queue<Transaction> transactions = new ConcurrentLinkedQueue<>();
    private AtomicInteger nextBidId = new AtomicInteger(0);

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {
        Bid bid = new Bid(newBidId(), trader, stock, minPrice);
        SortedMultiset<Bid> offers = buyBids.get(stock);
        if (!appendSellTransaction(bid, offers)) {
            SortedMultiset<Bid> sellBidsList = getSellBidsList(stock);
            synchronized (sellBidsList) {
                if (!sellBidsList.add(bid)) {
                    throw new RuntimeException("impossible to add " + bid + "  to sellList " + sellBidsList);
                }
            }
        }
        return bid;
    }


    @Override
    public Bid buy(Trader trader, Stock stock, double maxPrice) {
        Bid bid = new Bid(newBidId(), trader, stock, maxPrice);
        SortedMultiset<Bid> offers = sellBids.get(stock);
        if (!appendBuyTransaction(bid, offers)) {
            SortedMultiset<Bid> buyBidsList = getBuyBidsList(stock);
            synchronized (buyBidsList) {
                if (!buyBidsList.add(bid)) {
                    throw new RuntimeException("impossible to add " + bid + "  to buyList " + buyBidsList);
                }
            }
        }
        return bid;
    }

    private long newBidId() {
        return nextBidId.incrementAndGet();
    }


    private SortedMultiset<Bid> addListToMap(Stock stock, Map<Stock, SortedMultiset<Bid>> map) {
        SortedMultiset<Bid> stockBids = map.get(stock);
        if (stockBids == null) {
            stockBids = TreeMultiset.create();
            map.put(stock, stockBids);
        }
        return stockBids;
    }


    private boolean appendBuyTransaction(Bid buy, SortedMultiset<Bid> offers) {
        if (offers != null && offers.size() > 0) {
            synchronized (offers) {
                Bid offer = offers.firstEntry().getElement();
                if (offer.getPrice() <= buy.getPrice()) {
                    if (!offers.remove(offer)) {
                        throw new RuntimeException("impossible to remove " + offer + "  set content " + offers);
                    }
                    transactions.add(new Transaction(buy, offer, offer.getPrice()));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean appendSellTransaction(Bid sell, SortedMultiset<Bid> offers) {
        if (offers != null && offers.size() > 0) {
            synchronized (offers) {
                Bid offer = offers.lastEntry().getElement();
                if (sell.getPrice() <= offer.getPrice()) {
                    if (!offers.remove(offer)) {
                        throw new RuntimeException("impossible to remove " + offer + "  set content " + offers);
                    }
                    transactions.add(new Transaction(offer, sell, offer.getPrice()));
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public synchronized SortedMultiset<Bid> getBuyBidsList(Stock stock) {
        return addListToMap(stock, buyBids);
    }

    @Override
    public synchronized SortedMultiset<Bid> getSellBidsList(Stock stock) {
        return addListToMap(stock, sellBids);
    }

    @Override
    public synchronized Queue<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return "ExchangeSyncronized";
    }
}
