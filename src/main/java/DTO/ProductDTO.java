package DTO;

import java.math.BigDecimal;

public class ProductDTO {
    private int id;
    private String name;
    private BigDecimal cost_price;
    private BigDecimal selling_price;
    private int stock_in_hand;
    private int committed_stock;
    private int ordered_stock;
    private int opening_stock;

    public ProductDTO() {
    }

    public ProductDTO(int id, String name, BigDecimal cost_price, BigDecimal selling_price, int stock_in_hand, int committed_stock, int ordered_stock, int opening_stock) {
        this.id = id;
        this.name = name;
        this.cost_price = cost_price;
        this.selling_price = selling_price;
        this.stock_in_hand = stock_in_hand;
        this.committed_stock = committed_stock;
        this.ordered_stock = ordered_stock;
        this.opening_stock = opening_stock;
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

    @Override
    public String toString() {
        return "ProductDTO{" +
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