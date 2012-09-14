package com.gamasoft.example.model;

public class Bid implements Comparable<Bid> {

    private final long id;
    private final Trader trader;
    private final Stock stock;
    private final double price;

    public Bid(long id, Trader trader, Stock stock, double price) {
        this.id = id;
        this.trader = trader;
        this.stock = stock;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public Trader getTrader() {
        return trader;
    }

    public Stock getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bid bid = (Bid) o;

        if (id != bid.id) return false;
        if (Double.compare(bid.price, price) != 0) return false;
        if (stock != null ? !stock.equals(bid.stock) : bid.stock != null) return false;
        if (trader != null ? !trader.equals(bid.trader) : bid.trader != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (trader != null ? trader.hashCode() : 0);
        result = 31 * result + (stock != null ? stock.hashCode() : 0);
        temp = price != +0.0d ? Double.doubleToLongBits(price) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id=" + id +
                ", trader=" + trader +
                ", stock=" + stock +
                ", price=" + price +
                '}';
    }

    @Override
    public int compareTo(Bid o) {
        return (int) (10000 * (this.getPrice() - o.getPrice()));
    }
}
