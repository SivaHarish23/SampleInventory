package DTO;

import Model.Product;
import Util.TimeUtil;

import java.math.BigDecimal;

public class ProductDTO {
    private String id; // e.g., "PRO-1"
    private String name;
    private BigDecimal cost_price;
    private BigDecimal selling_price;
    private Integer opening_stock;
    private String created_at;  // formatted datetime string
    private String updated_at;  // formatted datetime string

    public ProductDTO(Product p){
        this.id = (p.getId() != null) ? "PRO-" + p.getId() : null;
        this.name = p.getName();
        this.cost_price = p.getCost_price();
        this.selling_price = p.getSelling_price();
        this.opening_stock = p.getOpening_stock();
        this.created_at = (p.getCreated_at()!=null) ? TimeUtil.epochToString(p.getCreated_at()) : null;
        this.updated_at = (p.getUpdated_at()!=null) ? TimeUtil.epochToString(p.getUpdated_at()) : null;
    }
    
    public ProductDTO() {}

    private ProductDTO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.cost_price = builder.cost_price;
        this.selling_price = builder.selling_price;
        this.opening_stock = builder.opening_stock;
        this.created_at = builder.created_at;
        this.updated_at = builder.updated_at;
    }

    public static class Builder {
        private String id;
        private String name;
        private BigDecimal cost_price;
        private BigDecimal selling_price;
        private Integer opening_stock;
        private String created_at;
        private String updated_at;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder cost_price(BigDecimal cost_price) {
            this.cost_price = cost_price;
            return this;
        }

        public Builder selling_price(BigDecimal selling_price) {
            this.selling_price = selling_price;
            return this;
        }

        public Builder opening_stock(Integer opening_stock) {
            this.opening_stock = opening_stock;
            return this;
        }

        public Builder created_at(String created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder updated_at(String updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public ProductDTO build() {
            return new ProductDTO(this);
        }
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost_price() {
        return cost_price;
    }

    public void setCost_price(BigDecimal cost_price) {
        this.cost_price = cost_price;
    }

    public BigDecimal getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(BigDecimal selling_price) {
        this.selling_price = selling_price;
    }

    public Integer getOpening_stock() {
        return opening_stock;
    }

    public void setOpening_stock(Integer opening_stock) {
        this.opening_stock = opening_stock;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cost_price=" + cost_price +
                ", selling_price=" + selling_price +
                ", opening_stock=" + opening_stock +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}