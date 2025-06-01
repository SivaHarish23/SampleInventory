package Model;

import DTO.BillLineItemDTO;
import Service.BillLineItemService;

import java.math.BigDecimal;

public class BillLineItem {
    private Integer id;
    private Integer bill_id;
    private Integer product_id;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal amount;

    // Private constructor
    private BillLineItem(Builder builder) {
        this.id = builder.id;
        this.bill_id = builder.bill_id;
        this.product_id = builder.product_id;
        this.quantity = builder.quantity;
        this.rate = builder.rate;
        this.amount = builder.amount;
    }

    public BillLineItem(BillLineItemDTO dto){
        this.id = dto.getId() == null ? 0 : Integer.parseInt(dto.getId().substring(4));
        this.bill_id = Integer.parseInt(dto.getBill_id().substring(4));
        this.product_id = Integer.parseInt(dto.getProduct_id().substring(4));
        this.quantity = dto.getQuantity();
        this.rate = dto.getRate();
        this.amount = dto.getRate().multiply(BigDecimal.valueOf(dto.getQuantity()));
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getBill_id() {
        return bill_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // Builder class
    public static class Builder {
        private Integer id;
        private Integer bill_id;
        private Integer product_id;
        private Integer quantity;
        private BigDecimal rate;
        private BigDecimal amount;

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setBill_id(Integer bill_id) {
            this.bill_id = bill_id;
            return this;
        }

        public Builder setProduct_id(Integer product_id) {
            this.product_id = product_id;
            return this;
        }

        public Builder setQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setRate(BigDecimal rate) {
            this.rate = rate;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public BillLineItem build() {
            return new BillLineItem(this);
        }
    }
}
