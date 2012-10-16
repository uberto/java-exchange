package com.gamasoft.example.model;

public interface TransactionFactory {

    Transaction make(Bid offer);

}
