package Model;

import javafx.util.Builder;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private BigDecimal cost_price;
    private BigDecimal selling_price;
    private int stock_in_hand;
    private int committed_stock;
    private int ordered_stock;
    private int opening_stock;

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost_price() {
        return cost_price;
    }

    public void setCost_price(BigDecimal cost_price) {
        this.cost_price = cost_price;
    }

    public BigDecimal getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(BigDecimal selling_price) {
        this.selling_price = selling_price;
    }

    public int getStock_in_hand() {
        return stock_in_hand;
    }

    public void setStock_in_hand(int stock_in_hand) {
        this.stock_in_hand = stock_in_hand;
    }

    public int getCommitted_stock() {
        return committed_stock;
    }

    public void setCommitted_stock(int committed_stock) {
        this.committed_stock = committed_stock;
    }

    public int getOrdered_stock() {
        return ordered_stock;
    }

    public void setOrdered_stock(int ordered_stock) {
        this.ordered_stock = ordered_stock;
    }

    public int getOpening_stock() {
        return opening_stock;
    }

    public void setOpening_stock(int opening_stock) {
        this.opening_stock = opening_stock;
    }

    private Product(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.cost_price = builder.cost_price;
        this.selling_price = builder.selling_price;
        this.stock_in_hand = builder.stock_in_hand;
        this.committed_stock = builder.committed_stock;
        this.ordered_stock = builder.ordered_stock;
        this.opening_stock = builder.opening_stock;
    }

    public static class Builder {
        private int id;
        private String name;
        private BigDecimal cost_price;
        private BigDecimal selling_price;
        private int stock_in_hand = 0;
        private int committed_stock = 0;
        private int ordered_stock = 0;
        private int opening_stock = 0;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder cost_price(BigDecimal cost_price) {
            this.cost_price = cost_price;
            return this;
        }

        public Builder selling_price(BigDecimal selling_price) {
            this.selling_price = selling_price;
            return this;
        }

        public Builder stock_in_hand(int stock_in_hand) {
            this.stock_in_hand = stock_in_hand;
            return this;
        }

        public Builder committed_stock(int committed_stock) {
            this.committed_stock = committed_stock;
            return this;
        }

        public Builder ordered_stock(int ordered_stock) {
            this.ordered_stock = ordered_stock;
            return this;
        }

        public Builder opening_stock(int opening_stock) {
            this.opening_stock = opening_stock;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cost_price=" + cost_price +
                ", selling_price=" + selling_price +
                ", stock_in_hand=" + stock_in_hand +
                ", committed_stock=" + committed_stock +
                ", ordered_stock=" + ordered_stock +
                ", opening_stock=" + opening_stock +
                '}';
    }
}