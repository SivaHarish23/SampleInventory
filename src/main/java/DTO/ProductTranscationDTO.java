package DTO;

import java.sql.Date;

public class ProductTranscationDTO {

    private int product_id;
    private String product_name;
    private int quantity;
    private Date bill_date;

    public ProductTranscationDTO() {
    }

    public ProductTranscationDTO(int product_id, String product_name, int quantity, Date bill_date) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.bill_date = bill_date;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getBill_date() {
        return bill_date;
    }

    public void setBill_date(Date bill_date) {
        this.bill_date = bill_date;
    }
}
