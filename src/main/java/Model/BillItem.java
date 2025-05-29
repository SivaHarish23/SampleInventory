package Model;

import java.math.BigDecimal;

public class BillItem {
    private int id, bill_id, product_id, quantity;
    private String bill_type;
    private BigDecimal rate, amount;

    public BillItem() {
    }

    private BillItem(Builder builder) {
        this.id = builder.id;
        this.bill_id = builder.bill_id;
        this.product_id = builder.product_id;
        this.quantity = builder.quantity;
        this.bill_type = builder.bill_type;
        this.rate = builder.rate;
        this.amount = builder.amount;
    }

    public BillItem(int id, int bill_id, int product_id, int quantity, String bill_type, BigDecimal rate, BigDecimal amount) {
        this.id = id;
        this.bill_id = bill_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.bill_type = bill_type;
        this.rate = rate;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBill_id() {
        return bill_id;
    }

    public void setBill_id(int bill_id) {
        this.bill_id = bill_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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

    public static class Builder {
        private int id, bill_id, product_id, quantity;
        private String bill_type;
        private BigDecimal rate, amount;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder bill_id(int bill_id) {
            this.bill_id = bill_id;
            return this;
        }

        public Builder product_id(int product_id) {
            this.product_id = product_id;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder bill_type(String bill_type) {
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

        public BillItem build() {
            return new BillItem(this);
        }
    }

    @Override
    public String toString() {
        return "Bill_Items{" +
                "id=" + id +
                ", bill_id=" + bill_id +
                ", product_id=" + product_id +
                ", quantity=" + quantity +
                ", bill_type='" + bill_type + '\'' +
                ", rate=" + rate +
                ", amount=" + amount +
                '}';
    }
}
