package com.gamasoft.example.collections;

import com.gamasoft.example.model.*;

import java.util.Optional;
import java.util.Queue;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ExchangeLambda implements Exchange {

    private BidsForStockMap sellBids = new BidsForStockMapTreeSet(new ConcurrentHashMap<>(),
            SortedSet::first,
            (newBid, bestBid) -> newBid.getPrice() >= bestBid.getPrice());


    private BidsForStockMap buyBids = new BidsForStockMapTreeSet(new ConcurrentHashMap<>(),
            SortedSet::last,
            (newBid, bestBid) -> newBid.getPrice() <= bestBid.getPrice());


    private Queue<Transaction> transactions = new ConcurrentLinkedQueue<>();
    private AtomicInteger nextBidId = new AtomicInteger(0);


    private Bid createBid(Trader trader, Stock stock, double price) {
        return new Bid(newBidId(), trader, stock, price);
    }

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {

        Bid bid = createBid(trader, stock, minPrice);
        operation(bid,
                buyBids.get(stock),
                sellBids.get(stock), offer -> new Transaction(offer, bid, offer.getPrice()));
        return bid;
    }


    @Override
    public Bid buy(Trader trader, Stock stock, double maxPrice) {

        Bid bid = createBid(trader, stock, maxPrice);
        operation(bid,
                sellBids.get(stock),
                buyBids.get(stock), offer -> new Transaction(bid, offer, offer.getPrice()));
        return bid;
    }

    private void operation(Bid bid,
                           BidSet bidsToMatch,
                           BidSet bidList, TransactionFactory transactionFactory) {


        Optional<Bid> bidOptional = bidsToMatch.extractMatchingBid(bid);
        if (bidOptional.isPresent()) {
            transactions.add(transactionFactory.make(bidOptional.get()));
        } else {
               bidList.add(bid);
        }
    }

    private long newBidId() {
        return nextBidId.incrementAndGet();
    }


    private boolean canMatchTransaction(Bid newBid, BidSet offers, TransactionFactory transactionFactory) {


        return false;
    }

    @Override
    public synchronized SortedSet<Bid> getBuyBidsList(Stock stock) {

        return buyBids.get(stock).asSet();
    }

    @Override
    public synchronized SortedSet<Bid> getSellBidsList(Stock stock) {

        return sellBids.get(stock).asSet();
    }

    @Override
    public synchronized Queue<Transaction> getTransactions() {
        return transactions;
    }

}
