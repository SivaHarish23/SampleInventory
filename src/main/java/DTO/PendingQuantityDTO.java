package DTO;

public class PendingQuantityDTO {
    private String vendor_name;
    private String customer_name;
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
