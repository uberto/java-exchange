package com.gamasoft.example.collections;

import com.gamasoft.example.model.Stock;

import java.util.Map;

public interface BidsForStockMap {

    public Map<Stock, BidSet> asMap();

    BidSet get(Stock stock);
}
