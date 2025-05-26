package Model;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int stockInHand;
    private int committedStock;
    private int orderedStock;
    private int openingStock;

    public Product() {
    }

    private Product(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.costPrice = builder.costPrice;
        this.sellingPrice = builder.sellingPrice;
        this.stockInHand = builder.stockInHand;
        this.committedStock = builder.committedStock;
        this.orderedStock = builder.orderedStock;
        this.openingStock = builder.openingStock;
    }

    // Getters and setters (setters optional if using builder)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public int getStockInHand() { return stockInHand; }
    public void setStockInHand(int stockInHand) { this.stockInHand = stockInHand; }

    public int getCommittedStock() { return committedStock; }
    public void setCommittedStock(int committedStock) { this.committedStock = committedStock; }

    public int getOrderedStock() { return orderedStock; }
    public void setOrderedStock(int orderedStock) { this.orderedStock = orderedStock; }

    public int getOpeningStock() { return openingStock; }
    public void setOpeningStock(int openingStock) { this.openingStock = openingStock; }

    public static class Builder {
        private int id;
        private String name;
        private BigDecimal costPrice;
        private BigDecimal sellingPrice;
        private int stockInHand = 0;
        private int committedStock = 0;
        private int orderedStock = 0;
        private int openingStock = 0;

        public Builder id(int id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder costPrice(BigDecimal costPrice) { this.costPrice = costPrice; return this; }
        public Builder sellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; return this; }
        public Builder stockInHand(int stockInHand) { this.stockInHand = stockInHand; return this; }
        public Builder committedStock(int committedStock) { this.committedStock = committedStock; return this; }
        public Builder orderedStock(int orderedStock) { this.orderedStock = orderedStock; return this; }
        public Builder openingStock(int openingStock) { this.openingStock = openingStock; return this; }

        public Product build() {
            return new Product(this);
        }
    }
}
