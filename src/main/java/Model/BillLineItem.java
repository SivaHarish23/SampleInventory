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

    public BillLineItem(BillLineItemDTO billLineItem){
        this.id = (billLineItem.getId()!=null) ? Integer.parseInt(billLineItem.getId().substring(4)) : null;
        this.bill_id = (billLineItem.getBill_id()!=null) ? Integer.parseInt(billLineItem.getBill_id().substring(4)) : null;
        this.product_id = (billLineItem.getProduct_id()!=null) ? Integer.parseInt(billLineItem.getProduct_id().substring(4)) : null;
        this.quantity = billLineItem.getQuantity();
        this.rate = billLineItem.getRate();
        this.amount = billLineItem.getRate().multiply(BigDecimal.valueOf(billLineItem.getQuantity()));    }

    // Private constructor
    private BillLineItem(Builder builder) {
        this.id = builder.id;
        this.bill_id = builder.bill_id;
        this.product_id = builder.product_id;
        this.quantity = builder.quantity;
        this.rate = builder.rate;
        this.amount = builder.amount;
    }

    public BillLineItem() {

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBill_id() {
        return bill_id;
    }

    public void setBill_id(Integer bill_id) {
        this.bill_id = bill_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "BillLineItem{" +
                "id=" + id +
                ", bill_id=" + bill_id +
                ", product_id=" + product_id +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", amount=" + amount +
                '}';
    }
}
