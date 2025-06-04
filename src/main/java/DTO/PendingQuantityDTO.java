package DTO;

public class PendingQuantityDTO {


    private String customer_id;
    private String customer_name;
    private String vendor_id;
    private String vendor_name;
    private String product_id;
    private String product_name;
    private Integer pending_quantity;

    public PendingQuantityDTO() {
    }

    public PendingQuantityDTO(String vendor_name, String customer_name, String product_name, Integer pending_quantity) {
        this.vendor_name = vendor_name;
        this.customer_name = customer_name;
        this.product_name = product_name;
        this.pending_quantity = pending_quantity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = "PRO-" + product_id;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = "VEN-"+vendor_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = "CUS-"+customer_id;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Integer getPending_quantity() {
        return pending_quantity;
    }

    public void setPending_quantity(Integer pending_quantity) {
        this.pending_quantity = pending_quantity;
    }
}
