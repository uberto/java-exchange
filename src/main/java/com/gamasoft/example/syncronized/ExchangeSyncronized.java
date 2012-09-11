package com.gamasoft.example.syncronized;

import com.gamasoft.example.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeSyncronized implements Exchange {

    private Map<Stock, List<Bid>> sellBids = new HashMap<>();
    private Map<Stock, List<Bid>> buyBids = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    public void sell(Trader trader, Stock stock, double minPrice) {
       List<Bid> offers = buyBids.get(stock);
        if (offers != null){
            for (Bid offer : offers) {
                if (offer.getPrice() >= minPrice){
                    offers.remove(offer);
                    transactions.add(new Transaction(offer.getTrader(), trader, stock, minPrice));
                    return;
                }
            }
        }
        List<Bid> stockBids = sellBids.get(stock);
        if (stockBids == null){
            stockBids =  new ArrayList<>();
            sellBids.put(stock, stockBids);
        }
        stockBids.add(new Bid(1, trader, stock, minPrice));
    }

    @Override
    public void buy(Trader trader, Stock stock, double maxPrice) {
        List<Bid> offers = sellBids.get(stock);
        if (offers != null){
            for (Bid offer : offers) {
                if (offer.getPrice() <= maxPrice){
                    offers.remove(offer);
                    transactions.add(new Transaction(trader, offer.getTrader(), stock, maxPrice));
                    return;
                }
            }
        }
        List<Bid> stockBids = buyBids.get(stock);
        if (stockBids == null){
            stockBids =  new ArrayList<>();
            buyBids.put(stock, stockBids);
        }
        stockBids.add(new Bid(1, trader, stock, maxPrice));
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
