package DTO;

public class pendingProductsReportDTO {
    private int product_id;
    private String product_name;
    private int pending_quantity;

    // Constructors, getters, setters

    public pendingProductsReportDTO() {
    }

    public pendingProductsReportDTO(int product_id, String product_name, int pending_quantity) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.pending_quantity = pending_quantity;
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

    public int getPending_quantity() {
        return pending_quantity;
    }

    public void setPending_quantity(int pending_quantity) {
        this.pending_quantity = pending_quantity;
    }
}