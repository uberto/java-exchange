package com.gamasoft.example.collections;

import com.gamasoft.example.model.Bid;

import java.util.Optional;
import java.util.SortedSet;

public interface BidSet  {

    public Optional<Bid> extractMatchingBid(Bid newBid);

    public SortedSet<Bid> asSet();

    void add(Bid bid);

}
