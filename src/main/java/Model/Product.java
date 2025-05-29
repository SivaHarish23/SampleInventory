package Model;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private BigDecimal cost_price;
    private BigDecimal selling_price;
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
        this.opening_stock = builder.opening_stock;
    }

    public static class Builder {
        private int id;
        private String name;
        private BigDecimal cost_price;
        private BigDecimal selling_price;
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
                ", opening_stock=" + opening_stock +
                '}';
    }
}