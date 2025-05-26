package DTO;

import java.math.BigDecimal;

public class ProductUpdateDTO {
    private Integer id;
    private String name;  // nullable means not provided
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private Integer stockInHand;
    private Integer committedStock;
    private Integer orderedStock;
    private Integer openingStock;

    // Getters and Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Integer getStockInHand() { return stockInHand; }
    public void setStockInHand(Integer stockInHand) { this.stockInHand = stockInHand; }

    public Integer getCommittedStock() { return committedStock; }
    public void setCommittedStock(Integer committedStock) { this.committedStock = committedStock; }

    public Integer getOrderedStock() { return orderedStock; }
    public void setOrderedStock(Integer orderedStock) { this.orderedStock = orderedStock; }

    public Integer getOpeningStock() { return openingStock; }
    public void setOpeningStock(Integer openingStock) { this.openingStock = openingStock; }
}

