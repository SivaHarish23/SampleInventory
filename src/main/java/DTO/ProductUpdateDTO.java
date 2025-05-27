package DTO;

import java.math.BigDecimal;

public class ProductUpdateDTO {
    private Integer id;
    private String name;
    private BigDecimal cost_price;
    private BigDecimal selling_price;
    private Integer stock_in_hand;
    private Integer committed_stock;
    private Integer ordered_stock;
    private Integer opening_stock;

    public ProductUpdateDTO() {
    }

    public ProductUpdateDTO(Integer id, String name, BigDecimal cost_price, BigDecimal selling_price, Integer stock_in_hand, Integer committed_stock, Integer ordered_stock, Integer opening_stock) {
        this.id = id;
        this.name = name;
        this.cost_price = cost_price;
        this.selling_price = selling_price;
        this.stock_in_hand = stock_in_hand;
        this.committed_stock = committed_stock;
        this.ordered_stock = ordered_stock;
        this.opening_stock = opening_stock;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getStock_in_hand() {
        return stock_in_hand;
    }

    public void setStock_in_hand(Integer stock_in_hand) {
        this.stock_in_hand = stock_in_hand;
    }

    public Integer getCommitted_stock() {
        return committed_stock;
    }

    public void setCommitted_stock(Integer committed_stock) {
        this.committed_stock = committed_stock;
    }

    public Integer getOrdered_stock() {
        return ordered_stock;
    }

    public void setOrdered_stock(Integer ordered_stock) {
        this.ordered_stock = ordered_stock;
    }

    public Integer getOpening_stock() {
        return opening_stock;
    }

    public void setOpening_stock(Integer opening_stock) {
        this.opening_stock = opening_stock;
    }
}