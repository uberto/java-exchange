package com.gamasoft.example.collections;

import com.gamasoft.example.model.Bid;
import com.gamasoft.example.model.Stock;

import java.util.*;
import java.util.functions.BiPredicate;
import java.util.functions.Mapper;

public class BidsForStockMapTreeSet implements BidsForStockMap {
    private Map<Stock, BidSet> map;
    private Mapper<SortedSet<Bid>, Bid> bidMapper;
    private BiPredicate<Bid, Bid> isThePriceRight;

    public BidsForStockMapTreeSet(Map<Stock, BidSet> map,
                                  Mapper<SortedSet<Bid>, Bid> bidMapper,
                                  BiPredicate<Bid, Bid> isThePriceRight) {
        this.map = map;
        this.bidMapper = bidMapper;
        this.isThePriceRight = isThePriceRight;
    }

    @Override
    public Map<Stock, BidSet> asMap() {
        return map;
    }

    @Override
    public BidSet get(Stock stock) {
        BidSet stockBids = map.get(stock);
        if (stockBids == null) {
            stockBids = new BidSetLamda(bidMapper, isThePriceRight);
            map.put(stock, stockBids);
        }
        return stockBids;
    }

}
