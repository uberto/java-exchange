package com.gamasoft.example.model;

import java.util.List;

public interface Exchange {

    public void sell(Trader trader, Stock stock, double minPrice);

    public void buy(Trader trader, Stock stock, double maxPrice);

    public List<Transaction> getTransactions();
}
