package com.gamasoft.example.collections;

import com.gamasoft.example.model.*;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import java.util.*;

public class ExchangeUnsafe implements Exchange {
    private Map<Stock, SortedMultiset<Bid>> sellBids = new HashMap<>();
    private Map<Stock, SortedMultiset<Bid>> buyBids = new HashMap<>();
    private Queue<Transaction> transactions = new LinkedList<>();
    private int nextBidId = 1;

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {
        Bid bid = new Bid(newBidId(), trader, stock, minPrice);
        SortedMultiset<Bid> offers = buyBids.get(stock);
        if (!appendSellTransaction(bid, offers)) {
            SortedMultiset<Bid> sellBidsList = getSellBidsList(stock);
            if (!sellBidsList.add(bid)){
                throw new RuntimeException("impossible to add " + bid + "  to sellList " + sellBidsList);
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
            if (!buyBidsList.add(bid)){
                throw new RuntimeException("impossible to add " + bid + "  to buyList " + buyBidsList);
            }
        }
        return bid;
    }

    private long newBidId() {
        return nextBidId++;
    }



    private SortedMultiset<Bid> addListToMap(Stock stock, Map<Stock, SortedMultiset<Bid>> map) {
        SortedMultiset<Bid> stockBids = map.get(stock);
        if (stockBids == null) {
            stockBids = TreeMultiset.create(new Comparator<Bid>() {
                @Override
                public int compare(Bid o1, Bid o2) {
                    return (int) (10000 * (o1.getPrice() - o2.getPrice()));
                }
            });
            map.put(stock, stockBids);
        }
        return stockBids;
    }


    private boolean appendBuyTransaction(Bid buy, SortedMultiset<Bid> offers) {
        if (offers != null && offers.size() > 0) {
            Bid offer = offers.firstEntry().getElement();
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

    private boolean appendSellTransaction(Bid sell, SortedMultiset<Bid> offers) {
        if (offers != null && offers.size() > 0) {
            Bid offer = offers.lastEntry().getElement();
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
    public SortedMultiset<Bid> getBuyBidsList(Stock stock) {
        return addListToMap(stock, buyBids);
    }

    @Override
    public SortedMultiset<Bid> getSellBidsList(Stock stock) {
        return addListToMap(stock, sellBids);
    }

    @Override
    public Queue<Transaction> getTransactions() {
        return transactions;
    }


}
