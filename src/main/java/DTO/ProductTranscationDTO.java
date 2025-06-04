package DTO;

import java.lang.String;
import java.math.BigDecimal;

public class ProductTranscationDTO {

    private String product_id;
    private String product_name;
    private Integer quantity;
    private String bill_date;

    private BigDecimal stock_value;

    public ProductTranscationDTO() {
    }

    public ProductTranscationDTO(String product_id, String product_name, Integer quantity, String bill_date) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.bill_date = bill_date;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = "PRO-"+product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public BigDecimal getStock_value() {
        return stock_value;
    }

    public void setStock_value(BigDecimal stock_value) {
        this.stock_value = stock_value;
    }
}
