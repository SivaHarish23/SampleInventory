package DTO;

import java.math.BigDecimal;

public class ProductDTO {
    private Integer id;
    private String name;
    private BigDecimal cost_price;
    private BigDecimal selling_price;
    private Integer opening_stock;

    public ProductDTO() {
    }

    public ProductDTO(Integer id, String name, BigDecimal cost_price, BigDecimal selling_price, Integer opening_stock) {
        this.id = id;
        this.name = name;
        this.cost_price = cost_price;
        this.selling_price = selling_price;
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

    public Integer getOpening_stock() {
        return opening_stock;
    }

    public void setOpening_stock(Integer opening_stock) {
        this.opening_stock = opening_stock;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cost_price=" + cost_price +
                ", selling_price=" + selling_price +
                ", opening_stock=" + opening_stock +
                '}';
    }
}