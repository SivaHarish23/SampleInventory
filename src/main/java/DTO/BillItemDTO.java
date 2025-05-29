package DTO;

import java.math.BigDecimal;

public class BillItemDTO {
    private Integer id, bill_id, product_id, quantity;
    private String bill_type;
    private BigDecimal rate, amount;

    public BillItemDTO() {
    }

    public BillItemDTO(Integer id, Integer bill_id, Integer product_id, Integer quantity, String bill_type, BigDecimal rate, BigDecimal amount) {
        this.id = id;
        this.bill_id = bill_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.bill_type = bill_type;
        this.rate = rate;
        this.amount = amount;
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

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
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
        return "BillItemDTO{" +
                "id=" + id +
                ", bill_id=" + bill_id +
                ", product_id=" + product_id +
                ", quantity=" + quantity +
                ", bill_type='" + bill_type + '\'' +
                ", rate=" + rate +
                ", amount=" + amount +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer bill_id;
        private Integer product_id;
        private Integer quantity;
        private String bill_type;
        private BigDecimal rate;
        private BigDecimal amount;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder billId(Integer bill_id) {
            this.bill_id = bill_id;
            return this;
        }

        public Builder productId(Integer product_id) {
            this.product_id = product_id;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder billType(String bill_type) {
            this.bill_type = bill_type;
            return this;
        }

        public Builder rate(BigDecimal rate) {
            this.rate = rate;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public BillItemDTO build() {
            return new BillItemDTO(id, bill_id, product_id, quantity, bill_type, rate, amount);
        }
    }
}
