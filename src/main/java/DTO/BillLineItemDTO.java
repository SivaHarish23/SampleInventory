package DTO;

import Model.BillLineItem;

import java.math.BigDecimal;

public class BillLineItemDTO {
    private String id;
    private String bill_id;
    private String product_id;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal amount;

    public BillLineItemDTO(BillLineItem billLineItem){
        this.id = (billLineItem.getId()!=null) ? "ITM-" + billLineItem.getId() : null;
        this.bill_id = (billLineItem.getBill_id()!=null) ? "BIL-" + billLineItem.getBill_id() : null;
        this.product_id = (billLineItem.getProduct_id()!=null) ? "PRO-" + billLineItem.getProduct_id() : null;
        this.quantity = billLineItem.getQuantity();
        this.rate = billLineItem.getRate();
        this.amount = billLineItem.getRate().multiply(BigDecimal.valueOf(billLineItem.getQuantity()));
    }

    public BillLineItemDTO(String id, String bill_id, String product_id, Integer quantity, BigDecimal rate, BigDecimal amount) {
        this.id = id;
        this.bill_id = bill_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.rate = rate;
        this.amount = amount;
    }

    // Private constructor
    private BillLineItemDTO(Builder builder) {
        this.id = builder.id;
        this.bill_id = builder.bill_id;
        this.product_id = builder.product_id;
        this.quantity = builder.quantity;
        this.rate = builder.rate;
        this.amount = builder.amount;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getBill_id() {
        return bill_id;
    }

    public String getProduct_id() {
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
        private String id;
        private String bill_id;
        private String product_id;
        private Integer quantity;
        private BigDecimal rate;
        private BigDecimal amount;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setBill_id(String bill_id) {
            this.bill_id = bill_id;
            return this;
        }

        public Builder setProduct_id(String product_id) {
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

        public BillLineItemDTO build() {
            return new BillLineItemDTO(this);
        }
    }

    public BillLineItemDTO() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "BillLineItemDTO{" +
                "id='" + id + '\'' +
                ", bill_id='" + bill_id + '\'' +
                ", product_id='" + product_id + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", amount=" + amount +
                '}';
    }
}

