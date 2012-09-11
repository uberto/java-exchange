package com.gamasoft.example.model;

public class Stock {

    private final String name;

    private final String ticker;

    public Stock(String ticker, String name) {
        this.name = name;
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stock stock = (Stock) o;

        if (name != null ? !name.equals(stock.name) : stock.name != null) return false;
        if (ticker != null ? !ticker.equals(stock.ticker) : stock.ticker != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (ticker != null ? ticker.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "name='" + name + '\'' +
                ", ticker='" + ticker + '\'' +
                '}';
    }
}
