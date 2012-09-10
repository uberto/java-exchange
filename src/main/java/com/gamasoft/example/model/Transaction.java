package com.gamasoft.example.model;

public class Transaction {

    private Trader buyer;
    private Trader seller;
    private Stock stock;
    private double price;

    public Transaction(Trader buyer, Trader seller, Stock stock, double price) {
        this.buyer = buyer;
        this.seller = seller;
        this.stock = stock;
        this.price = price;
    }

    public Trader getBuyer() {
        return buyer;
    }

    public Trader getSeller() {
        return seller;
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

        Transaction that = (Transaction) o;

        if (Double.compare(that.price, price) != 0) return false;
        if (buyer != null ? !buyer.equals(that.buyer) : that.buyer != null) return false;
        if (seller != null ? !seller.equals(that.seller) : that.seller != null) return false;
        if (stock != null ? !stock.equals(that.stock) : that.stock != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = buyer != null ? buyer.hashCode() : 0;
        result = 31 * result + (seller != null ? seller.hashCode() : 0);
        result = 31 * result + (stock != null ? stock.hashCode() : 0);
        temp = price != +0.0d ? Double.doubleToLongBits(price) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "buyer=" + buyer +
                ", seller=" + seller +
                ", stock=" + stock +
                ", price=" + price +
                '}';
    }
}
