package DTO;

import Model.StockAvailablityReport;

public class StockAvailablityReportDTO {

    private String product_id;
    private String product_name;
    private Integer ordered_stock;
    private Integer quantity_in;
    private Integer quantity_out;
    private Integer committed_stock;
    private Integer stock_on_hand;

    public StockAvailablityReportDTO(StockAvailablityReport stock){
        this.product_id = "PRO-" + stock.getProduct_id();
        this.product_name = stock.getProduct_name();
        this.ordered_stock = stock.getOrdered_stock();
        this.quantity_in = stock.getQuatity_in();
        this.quantity_out = stock.getQuatity_out();
        this.committed_stock = stock.getCommitted_stock();
        this.stock_on_hand = stock.getStock_on_hand();
    }

    public StockAvailablityReportDTO(String product_id, String product_name, Integer ordered_stock, Integer quantity_in, Integer quantity_out, Integer committed_stock, Integer stock_on_hand) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.ordered_stock = ordered_stock;
        this.quantity_in = quantity_in;
        this.quantity_out = quantity_out;
        this.committed_stock = committed_stock;
        this.stock_on_hand = stock_on_hand;
    }

    // Private constructor to enforce use of Builder
    private StockAvailablityReportDTO(Builder builder) {
        this.product_id = builder.product_id;
        this.product_name = builder.product_name;
        this.ordered_stock = builder.ordered_stock;
        this.quantity_in = builder.quantity_in;
        this.quantity_out = builder.quantity_out;
        this.committed_stock = builder.committed_stock;
        this.stock_on_hand = builder.stock_on_hand;
    }

    // Static Builder class
    public static class Builder {
        private String product_id;
        private String product_name;
        private Integer ordered_stock;
        private Integer quantity_in;
        private Integer quantity_out;
        private Integer committed_stock;
        private Integer stock_on_hand;

        public Builder productId(String product_id) {
            this.product_id = product_id;
            return this;
        }

        public Builder productName(String product_name) {
            this.product_name = product_name;
            return this;
        }

        public Builder orderedStock(Integer ordered_stock) {
            this.ordered_stock = ordered_stock;
            return this;
        }

        public Builder quantityIn(Integer quantity_in) {
            this.quantity_in = quantity_in;
            return this;
        }

        public Builder quantityOut(Integer quantity_out) {
            this.quantity_out = quantity_out;
            return this;
        }

        public Builder committedStock(Integer committed_stock) {
            this.committed_stock = committed_stock;
            return this;
        }

        public Builder stockOnHand(Integer stock_on_hand) {
            this.stock_on_hand = stock_on_hand;
            return this;
        }

        public StockAvailablityReportDTO build() {
            return new StockAvailablityReportDTO(this);
        }
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

    public Integer getOrdered_stock() {
        return ordered_stock;
    }

    public void setOrdered_stock(Integer ordered_stock) {
        this.ordered_stock = ordered_stock;
    }

    public Integer getQuatity_in() {
        return quantity_in;
    }

    public void setQuatity_in(Integer quantity_in) {
        this.quantity_in = quantity_in;
    }

    public Integer getQuatity_out() {
        return quantity_out;
    }

    public void setQuatity_out(Integer quantity_out) {
        this.quantity_out = quantity_out;
    }

    public Integer getCommitted_stock() {
        return committed_stock;
    }

    public void setCommitted_stock(Integer committed_stock) {
        this.committed_stock = committed_stock;
    }

    public Integer getStock_on_hand() {
        return stock_on_hand;
    }

    public void setStock_on_hand(Integer stock_on_hand) {
        this.stock_on_hand = stock_on_hand;
    }

    // Optional: Add getters or toString() if required
}
