package com.gamasoft.example.syncronized;

import com.gamasoft.example.model.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ExchangeSyncronized implements Exchange {

    private Map<Stock, SortedSet<Bid>> sellBids = new HashMap<>();
    private Map<Stock, SortedSet<Bid>> buyBids = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {
        Bid bid = new Bid(newBidId(), trader, stock, minPrice);
        SortedSet<Bid> offers = buyBids.get(stock);
        if (!appendSellTransaction(bid, offers)) {
            SortedSet<Bid> stockBids = getSellBidsList(stock);
            stockBids.add(bid);
        }
        return bid;


    }



    @Override
    public Bid buy(Trader trader, Stock stock, double maxPrice) {
        Bid bid = new Bid(newBidId(), trader, stock, maxPrice);
        SortedSet<Bid> offers = sellBids.get(stock);
        if (!appendBuyTransaction(bid, offers)) {
            SortedSet<Bid> stockBids = getBuyBidsList(stock);
            stockBids.add(bid);
        }
        return bid;
    }

    private long newBidId() {
        return ThreadLocalRandom.current().nextLong(1_000_000, 1_000_000_000);
    }

    private SortedSet<Bid> getSellBidsList(Stock stock) {
        return addListToMap(stock, sellBids);
    }

    private SortedSet<Bid> addListToMap(Stock stock, Map<Stock, SortedSet<Bid>> map) {
        SortedSet<Bid> stockBids = map.get(stock);
        if (stockBids == null) {
            stockBids = new TreeSet<>(new Comparator<Bid>() {
                @Override
                public int compare(Bid b1, Bid b2) {
                    return (int) (b1.getPrice() - b2.getPrice());
                }
            });
            map.put(stock, stockBids);
        }
        return stockBids;
    }


    private boolean appendBuyTransaction(Bid buy, SortedSet<Bid> offers) {
        if (offers != null && offers.size() > 0) {
            Bid offer = offers.first();
            if (offer.getPrice() <= buy.getPrice()) {
                offers.remove(offer);
                transactions.add(new Transaction(buy, offer, offer.getPrice()));
                return true;
            }

        }
        return false;
    }

    private boolean appendSellTransaction(Bid sell, SortedSet<Bid> offers) {
        if (offers != null && offers.size() > 0) {
            Bid offer = offers.last();
            if (sell.getPrice() <= offer.getPrice()) {
                offers.remove(offer);
                transactions.add(new Transaction(offer, sell, offer.getPrice()));
                return true;
            }

        }
        return false;
    }

    private SortedSet<Bid> getBuyBidsList(Stock stock) {
        return addListToMap(stock, buyBids);

    }

    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
