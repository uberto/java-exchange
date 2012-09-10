package com.gamasoft.example.syncronized;

import com.gamasoft.example.model.Exchange;
import com.gamasoft.example.model.Stock;
import com.gamasoft.example.model.Trader;
import com.gamasoft.example.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeSyncronized implements Exchange {

    private Map<Stock, List<Double>> sellBids = new HashMap<>();
    private Map<Stock, List<Double>> buyBids = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    public void sell(Trader trader, Stock stock, double minPrice) {

    }

    @Override
    public void buy(Trader trader, Stock stock, double maxPrice) {

    }

    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
