package Model;

import DTO.InvoiceLineItemDTO;

import java.math.BigDecimal;

public class InvoiceLineItem {
    private Integer id;
    private Integer invoice_id;
    private Integer product_id;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal amount;

    public InvoiceLineItem(InvoiceLineItemDTO invoiceLineItem){
        this.id = (invoiceLineItem.getId()!=null) ? Integer.parseInt(invoiceLineItem.getId().substring(4)) : null;
        this.invoice_id = (invoiceLineItem.getInvoice_id()!=null) ? Integer.parseInt(invoiceLineItem.getInvoice_id().substring(4)) : null;
        this.product_id = (invoiceLineItem.getProduct_id()!=null) ? Integer.parseInt(invoiceLineItem.getProduct_id().substring(4)) : null;
        this.quantity = invoiceLineItem.getQuantity();
        this.rate = invoiceLineItem.getRate();
        this.amount = invoiceLineItem.getRate().multiply(BigDecimal.valueOf(invoiceLineItem.getQuantity()));    }


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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setInvoice_id(Integer invoice_id) {
        this.invoice_id = invoice_id;
    }

    public void setProduct_id(Integer product_id) {
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
}