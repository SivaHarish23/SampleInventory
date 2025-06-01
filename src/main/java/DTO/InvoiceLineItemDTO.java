package DTO;

import java.math.BigDecimal;

public class InvoiceLineItemDTO {
    private String id;
    private String invoice_id;
    private String product_id;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal amount;

    // Private constructor
    private InvoiceLineItemDTO(Builder builder) {
        this.id = builder.id;
        this.invoice_id = builder.invoice_id;
        this.product_id = builder.product_id;
        this.quantity = builder.quantity;
        this.rate = builder.rate;
        this.amount = builder.amount;
    }

    // Getters (optional - add as needed)
    public String getId() {
        return id;
    }

    public String getInvoice_id() {
        return invoice_id;
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
        private String invoice_id;
        private String product_id;
        private Integer quantity;
        private BigDecimal rate;
        private BigDecimal amount;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder invoice_id(String invoice_id) {
            this.invoice_id = invoice_id;
            return this;
        }

        public Builder product_id(String product_id) {
            this.product_id = product_id;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
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

        public InvoiceLineItemDTO build() {
            return new InvoiceLineItemDTO(this);
        }
    }
}