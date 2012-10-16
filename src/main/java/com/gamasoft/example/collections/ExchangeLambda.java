package com.gamasoft.example.collections;

import com.gamasoft.example.model.*;

import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.functions.Mapper;
import java.util.functions.Predicate;

public class ExchangeLambda implements Exchange {

    private Map<Stock, SortedSet<Bid>> sellBids = new ConcurrentHashMap<>();
    private Map<Stock, SortedSet<Bid>> buyBids = new ConcurrentHashMap<>();
    private Queue<Transaction> transactions = new ConcurrentLinkedQueue<>();
    private AtomicInteger nextBidId = new AtomicInteger(0);


    private Bid createBid(Trader trader, Stock stock, double price) {
        return new Bid(newBidId(), trader, stock, price);
    }

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {

        Bid bid = createBid(trader, stock, minPrice);
        operation(bid,
                current -> minPrice <= current.getPrice(),
                SortedSet::last,
                buyBids.get(stock),
                sellBids,
                offer -> new Transaction(offer, bid, offer.getPrice()));
        return bid;
    }


    @Override
    public Bid buy(Trader trader, Stock stock, double maxPrice) {

        Bid bid = createBid(trader, stock, maxPrice);
        operation(bid,
                current -> maxPrice >= current.getPrice(),
                SortedSet::first,
                sellBids.get(stock),
                buyBids,
                offer -> new Transaction(bid, offer, offer.getPrice()));
        return bid;
    }

    private void operation(Bid bid,
                           Predicate<Bid> isGoodMatch,
                           Mapper<SortedSet<Bid>, Bid> bidMapper,
                           SortedSet<Bid> bidsToMatch,
                           Map<Stock, SortedSet<Bid>> bidsToAdd,
                           TransactionFactory transactionFactory) {


        if (bidsToMatch != null) {
            synchronized (bidsToMatch) {
                if (bidsToMatch.size() > 0) {
                    if (canMatchTransaction(bidsToMatch, bidMapper, isGoodMatch, transactionFactory)) {
                        return;
                    }
                }

            }
        }
        SortedSet<Bid> bidList = getBids(bid.getStock(), bidsToAdd);
        synchronized (bidList) {
            if (!bidList.add(bid)) {
                throw new RuntimeException("impossible to add " + bid + "  to bidList " + bidList);
            }
        }
    }

    private SortedSet<Bid> getBids(Stock stock, Map<Stock, SortedSet<Bid>> bids) {
        SortedSet<Bid> stockBids = bids.get(stock);
        if (stockBids == null) {
            stockBids = new TreeSet<>();
            bids.put(stock, stockBids);
        }
        return stockBids;
    }

    private long newBidId() {
        return nextBidId.incrementAndGet();
    }


    private boolean canMatchTransaction(SortedSet<Bid> offers, Mapper<SortedSet<Bid>, Bid> setBidMapper,
                                        Predicate<Bid> isGoodMatch, TransactionFactory transactionFactory) {

        Bid offer = setBidMapper.map(offers);
        if (isGoodMatch.test(offer)) {
            if (!offers.remove(offer)) {
                throw new RuntimeException("impossible to remove " + offer + " from set content " + offers);
            }

            transactions.add(transactionFactory.make(offer));
            return true;
        }

        return false;
    }

    @Override
    public synchronized SortedSet<Bid> getBuyBidsList(Stock stock) {
        SortedSet<Bid> stockBids = buyBids.get(stock);
        if (stockBids == null) {
            stockBids = new TreeSet<>();
            buyBids.put(stock, stockBids);
        }
        return stockBids;
    }

    @Override
    public synchronized SortedSet<Bid> getSellBidsList(Stock stock) {
        SortedSet<Bid> stockBids = sellBids.get(stock);
        if (stockBids == null) {
            stockBids = new TreeSet<>();
            sellBids.put(stock, stockBids);
        }
        return stockBids;
    }

    @Override
    public synchronized Queue<Transaction> getTransactions() {
        return transactions;
    }

}
