package Model;

import DTO.StockAvailablityReportDTO;

public class StockAvailablityReport {

    private Integer product_id;
    private String product_name;
    private Integer ordered_stock;
    private Integer quatity_in;
    private Integer quatity_out;
    private Integer committed_stock;
    private Integer stock_on_hand;

    public StockAvailablityReport(StockAvailablityReportDTO stock){
        this.product_id = Integer.parseInt(stock.getProduct_id().substring(4));
        this.product_name = stock.getProduct_name();
        this.ordered_stock = stock.getOrdered_stock();
        this.quatity_in = stock.getQuatity_in();
        this.quatity_out = stock.getQuatity_out();
        this.committed_stock = stock.getCommitted_stock();
        this.stock_on_hand = stock.getStock_on_hand();
    }

    // Private constructor
    private StockAvailablityReport(Builder builder) {
        this.product_id = builder.product_id;
        this.product_name = builder.product_name;
        this.ordered_stock = builder.ordered_stock;
        this.quatity_in = builder.quatity_in;
        this.quatity_out = builder.quatity_out;
        this.committed_stock = builder.committed_stock;
        this.stock_on_hand = builder.stock_on_hand;
    }

    // Static inner Builder class
    public static class Builder {
        private Integer product_id;
        private String product_name;
        private Integer ordered_stock;
        private Integer quatity_in;
        private Integer quatity_out;
        private Integer committed_stock;
        private Integer stock_on_hand;

        public Builder productId(Integer product_id) {
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

        public Builder quantityIn(Integer quatity_in) {
            this.quatity_in = quatity_in;
            return this;
        }

        public Builder quantityOut(Integer quatity_out) {
            this.quatity_out = quatity_out;
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

        public StockAvailablityReport build() {
            return new StockAvailablityReport(this);
        }
    }

    // Optional: Add getters here if needed


    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
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
        return quatity_in;
    }

    public void setQuatity_in(Integer quatity_in) {
        this.quatity_in = quatity_in;
    }

    public Integer getQuatity_out() {
        return quatity_out;
    }

    public void setQuatity_out(Integer quatity_out) {
        this.quatity_out = quatity_out;
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
}
