package DTO;

import Model.InvoiceLineItem;

import java.math.BigDecimal;
import java.util.List;

public class SalesInvoiceDTO {
    private String id;
    private String invoice_date;
    private String customer_id;
    private BigDecimal amount;
    private String status;
    private String notes;
    private String created_at;
    private String updated_at;
    private List<InvoiceLineItem> invoiceLineItems;

    public SalesInvoiceDTO(Builder builder) {
    }


    // Getters
    public String getId() {
        return id;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public List<InvoiceLineItem> getInvoiceLineItems() {
        return invoiceLineItems;
    }

    // Builder Class
    public static class Builder {
        private String id;
        private String invoice_date;
        private String customer_id;
        private BigDecimal amount;
        private String status;
        private String notes;
        private String created_at;
        private String updated_at;
        private List<InvoiceLineItem> invoiceLineItems;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder invoice_date(String invoice_date) {
            this.invoice_date = invoice_date;
            return this;
        }

        public Builder customer_id(String customer_id) {
            this.customer_id = customer_id;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
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

        public Builder invoiceLineItems(List<InvoiceLineItem> invoiceLineItems) {
            this.invoiceLineItems = invoiceLineItems;
            return this;
        }

        public SalesInvoiceDTO build() {
            return new SalesInvoiceDTO(this);
        }
    }
}
