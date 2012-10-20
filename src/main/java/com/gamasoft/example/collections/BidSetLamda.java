package com.gamasoft.example.collections;

import com.gamasoft.example.model.Bid;

import java.util.Collections;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.functions.BiPredicate;
import java.util.functions.Mapper;

public class BidSetLamda implements BidSet {
    private Mapper<SortedSet<Bid>, Bid> bidMapper;
    private BiPredicate<Bid, Bid> isThePriceRight;

    private SortedSet<Bid> set = new TreeSet<>();

    public BidSetLamda(Mapper<SortedSet<Bid>, Bid> bidMapper, BiPredicate<Bid, Bid> thePriceRight) {
        this.bidMapper = bidMapper;
        this.isThePriceRight = thePriceRight;
    }

    private Bid getBestBid() {
        return bidMapper.map(set);
    }

    @Override
    synchronized public Optional<Bid> extractMatchingBid(Bid newBid) {
        if (!set.isEmpty()) {
            Bid bestOldBid = getBestBid();
            if (isThePriceRight.test(newBid, bestOldBid)) {

                if (!set.remove(bestOldBid)) {
                    throw new RuntimeException("impossible to remove " + bestOldBid + " from set content " + set);
                }
                return new Optional<>(bestOldBid);
            }
        }
        return Optional.empty();
    }

    @Override
    public SortedSet<Bid> asSet() {
        return Collections.unmodifiableSortedSet(set);
    }

    @Override
    synchronized public void add(Bid bid) {
        if (!set.add(bid)){
            throw new RuntimeException("impossible to add " + bid + "  to bidList " + this);
        }
    }
}
