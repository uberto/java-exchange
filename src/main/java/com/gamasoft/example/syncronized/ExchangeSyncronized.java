package com.gamasoft.example.syncronized;

import com.gamasoft.example.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ExchangeSyncronized implements Exchange {

    private Map<Stock, List<Bid>> sellBids = new HashMap<>();
    private Map<Stock, List<Bid>> buyBids = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    public Bid sell(Trader trader, Stock stock, double minPrice) {
        Bid sell = new Bid(newBidId(), trader, stock, minPrice);
        List<Bid> offers = buyBids.get(stock);
        if (offers != null) {
            for (Bid offer : offers) {
                if (offer.getPrice() >= minPrice) {
                    offers.remove(offer);
                    transactions.add(new Transaction(offer, sell, minPrice));
                    return sell;
                }
            }
        }
        List<Bid> stockBids = getSellBidsList(stock);
        stockBids.add(sell);
        return sell;
    }

    private long newBidId() {
        return ThreadLocalRandom.current().nextLong(1_000_000, 1_000_000_000);
    }

    private List<Bid> getSellBidsList(Stock stock) {
        return addListToMap(stock, sellBids);
    }

    private List<Bid> addListToMap(Stock stock, Map<Stock, List<Bid>> map) {
        List<Bid> stockBids = map.get(stock);
        if (stockBids == null) {
            stockBids = new ArrayList<>();
            map.put(stock, stockBids);
        }
        return stockBids;
    }

    @Override
    public Bid buy(Trader trader, Stock stock, double maxPrice) {
        Bid bid = new Bid(newBidId(), trader, stock, maxPrice);
        List<Bid> offers = sellBids.get(stock);
        if (!appendTransaction(bid, offers)) {
            List<Bid> stockBids = getBuyBidsList(stock);
            stockBids.add(bid);
        }
        return bid;
    }

    private boolean appendTransaction(Bid buy, List<Bid> offers) {
        if (offers != null) {
            for (Bid offer : offers) {
                if (offer.getPrice() <= buy.getPrice()) {
                    offers.remove(offer);
                    transactions.add(new Transaction(buy, offer, buy.getPrice()));
                    return true;
                }
            }
        }
        return false;
    }

    private List<Bid> getBuyBidsList(Stock stock) {
        return addListToMap(stock, buyBids);

    }

    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
