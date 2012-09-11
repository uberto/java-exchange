package com.gamasoft.example.model;

public class Transaction {

    private final Bid buy;
    private final Bid sell;
    private final double price;

    public Transaction(Bid buy, Bid sell, double price) {
        this.buy = buy;
        this.sell = sell;
        this.price = price;
    }

    public Bid getBuy() {
        return buy;
    }

    public Bid getSell() {
        return sell;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (Double.compare(that.price, price) != 0) return false;
        if (buy != null ? !buy.equals(that.buy) : that.buy != null) return false;
        if (sell != null ? !sell.equals(that.sell) : that.sell != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = buy != null ? buy.hashCode() : 0;
        result = 31 * result + (sell != null ? sell.hashCode() : 0);
        temp = price != +0.0d ? Double.doubleToLongBits(price) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "buy=" + buy +
                ", sell=" + sell +
                ", price=" + price +
                '}';
    }
}
