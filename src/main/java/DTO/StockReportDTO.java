package DTO;

import java.math.BigDecimal;

public class StockReportDTO {
    private String product_id;
    private String product_name;
    private Integer current_stock;
    private BigDecimal stock_value;

//    public static StockReportDTO unMask(StockReportDTO stock){
//        StockReportDTO s = new StockReportDTO();
//        s.setProductId();
//    }

    public StockReportDTO() {
    }

    public StockReportDTO(String product_id, String product_name, Integer current_stock, BigDecimal stock_value) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.current_stock = current_stock;
        this.stock_value = stock_value;
    }

    public String getProductId() {
        return product_id;
    }

    public void setProductId(String product_id) {
        this.product_id = product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public Integer getCurrentStock() {
        return current_stock;
    }

    public void setCurrentStock(Integer current_stock) {
        this.current_stock = current_stock;
    }

    public BigDecimal getStockValue() {
        return stock_value;
    }

    public void setStockValue(BigDecimal stock_value) {
        this.stock_value = stock_value;
    }
}
