package DTO;

import java.math.BigDecimal;

public class StockReportDTO {
    private Integer productId;
    private String productName;
    private Integer currentStock;
    private BigDecimal stockValue;

    public StockReportDTO() {
    }

    public StockReportDTO(Integer productId, String productName, Integer currentStock, BigDecimal stockValue) {
        this.productId = productId;
        this.productName = productName;
        this.currentStock = currentStock;
        this.stockValue = stockValue;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }

    public void setStockValue(BigDecimal stockValue) {
        this.stockValue = stockValue;
    }
}
