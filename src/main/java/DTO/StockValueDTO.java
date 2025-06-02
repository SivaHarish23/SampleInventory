package DTO;

import java.math.BigDecimal;

public class StockValueDTO {

    private String product_id;
    private String product_name;
    private BigDecimal stock_value;

    public StockValueDTO() {
    }

    public StockValueDTO(Integer product_id, String product_name, BigDecimal stock_value) {
        this.product_id = "PRO-"+product_id;
        this.product_name = product_name;
        this.stock_value = stock_value;
    }

    public StockValueDTO(String product_id, String product_name, BigDecimal stock_value) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.stock_value = stock_value;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public BigDecimal getStock_value() {
        return stock_value;
    }

    public void setStock_value(BigDecimal stock_value) {
        this.stock_value = stock_value;
    }
}
