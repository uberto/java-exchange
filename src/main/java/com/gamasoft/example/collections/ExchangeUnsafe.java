package com.gamasoft.example.collections;

import com.gamasoft.example.model.*;

import java.util.*;

public class ExchangeUnsafe implements Exchange {
    private Map<Stock, SortedSet<Bid>> sellBids = new HashMap<>();
    private Map<Stock, SortedSet<Bid>> buyBids = new HashMap<>();
    private Queue<Transaction> transactions = new LinkedList<>();
    private int nextBidId = 1;

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {
        Bid bid = new Bid(newBidId(), trader, stock, minPrice);
        SortedSet<Bid> offers = buyBids.get(stock);
        if (!appendSellTransaction(bid, offers)) {
            SortedSet<Bid> sellBidsList = getSellBidsList(stock);
            if (!sellBidsList.add(bid)){
                throw new RuntimeException("impossible to add " + bid + "  to sellList " + sellBidsList);
            }
        }
        return bid;
    }



    @Override
    public Bid buy(Trader trader, Stock stock, double maxPrice) {
        Bid bid = new Bid(newBidId(), trader, stock, maxPrice);
        SortedSet<Bid> offers = sellBids.get(stock);
        if (!appendBuyTransaction(bid, offers)) {
            SortedSet<Bid> buyBidsList = getBuyBidsList(stock);
            if (!buyBidsList.add(bid)){
                throw new RuntimeException("impossible to add " + bid + "  to buyList " + buyBidsList);
            }
        }
        return bid;
    }

    private long newBidId() {
        return nextBidId++;
    }



    private SortedSet<Bid> addListToMap(Stock stock, Map<Stock, SortedSet<Bid>> map) {
        SortedSet<Bid> stockBids = map.get(stock);
        if (stockBids == null) {
            stockBids = new TreeSet<>();
            map.put(stock, stockBids);
        }
        return stockBids;
    }


    private boolean appendBuyTransaction(Bid buy, SortedSet<Bid> offers) {
        if (offers != null && offers.size() > 0) {
            Bid offer = offers.first();
            if (offer.getPrice() <= buy.getPrice()) {
                if (!offers.remove(offer)){
                    throw new RuntimeException("impossible to remove " + offer + "  set content " + offers);
                }
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
                if (!offers.remove(offer)){
                    throw new RuntimeException("impossible to remove " + offer + "  set content " + offers);
                }
                transactions.add(new Transaction(offer, sell, offer.getPrice()));
                return true;
            }

        }
        return false;
    }

    @Override
    public SortedSet<Bid> getBuyBidsList(Stock stock) {
        return addListToMap(stock, buyBids);
    }

    @Override
    public SortedSet<Bid> getSellBidsList(Stock stock) {
        return addListToMap(stock, sellBids);
    }

    @Override
    public Queue<Transaction> getTransactions() {
        return transactions;
    }


}
