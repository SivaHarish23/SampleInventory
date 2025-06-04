package DTO;

public class InsufficientStockDTO {
    private String product_id;
    private String product_name;
    private Integer required_quantity;
    private Integer stock_on_hand;

    public InsufficientStockDTO() {
    }

    public InsufficientStockDTO(String product_id, String product_name, Integer required_quantity, Integer stock_on_hand) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.required_quantity = required_quantity;
        this.stock_on_hand = stock_on_hand;
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

    public Integer getRequired_quantity() {
        return required_quantity;
    }

    public void setRequired_quantity(Integer required_quantity) {
        this.required_quantity = required_quantity;
    }

    public Integer getStock_on_hand() {
        return stock_on_hand;
    }

    public void setStock_on_hand(Integer stock_on_hand) {
        this.stock_on_hand = stock_on_hand;
    }
}
