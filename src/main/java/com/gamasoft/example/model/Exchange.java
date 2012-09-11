package com.gamasoft.example.model;

import java.util.List;

public interface Exchange {

    public Bid sell(Trader trader, Stock stock, double minPrice);

    public Bid buy(Trader trader, Stock stock, double maxPrice);

    public List<Transaction> getTransactions();
}
