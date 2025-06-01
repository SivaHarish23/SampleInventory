package Model;

import java.math.BigDecimal;

public class Product {
    private Integer id;
    private String name;
    private BigDecimal cost_price;
    private BigDecimal selling_price;
    private Integer opening_stock;
    private Long created_at;
    private Long updated_at;

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public Long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }

    private Product(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.cost_price = builder.cost_price;
        this.selling_price = builder.selling_price;
        this.opening_stock = builder.opening_stock;
        this.created_at = builder.created_at;
        this.updated_at = builder.updated_at;
    }

    public static class Builder {
        private Integer id;
        private String name;
        private BigDecimal cost_price;
        private BigDecimal selling_price;
        private Integer opening_stock = 0;
        private Long created_at;
        private Long updated_at;

        public Builder id(Integer id) {
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

        public Builder created_at(Long created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder updated_at(Long updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cost_price=" + cost_price +
                ", selling_price=" + selling_price +
                ", opening_stock=" + opening_stock +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}