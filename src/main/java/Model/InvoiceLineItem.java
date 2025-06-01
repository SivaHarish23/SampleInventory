package Model;

import java.math.BigDecimal;

public class InvoiceLineItem {
    private Integer id;
    private Integer invoice_id;
    private Integer product_id;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal amount;

    // Private constructor
    private InvoiceLineItem(Builder builder) {
        this.id = builder.id;
        this.invoice_id = builder.invoice_id;
        this.product_id = builder.product_id;
        this.quantity = builder.quantity;
        this.rate = builder.rate;
        this.amount = builder.amount;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getInvoice_id() {
        return invoice_id;
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
        private Integer invoice_id;
        private Integer product_id;
        private Integer quantity;
        private BigDecimal rate;
        private BigDecimal amount;

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setInvoice_id(Integer invoice_id) {
            this.invoice_id = invoice_id;
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

        public InvoiceLineItem build() {
            return new InvoiceLineItem(this);
        }
    }
}