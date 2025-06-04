package DTO;

import Model.InvoiceLineItem;
import Model.InvoiceLineItem;
import Model.SalesInvoice;
import Model.SalesInvoiceStatus;
import Util.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private List<InvoiceLineItemDTO> invoice_line_items;

    public SalesInvoiceDTO(SalesInvoice salesInvoice){
        this.id = (salesInvoice.getId()!=null) ? "INV-" + salesInvoice.getId() : null;
        this.invoice_date = (salesInvoice.getInvoice_date()!=null) ? TimeUtil.epochToString(salesInvoice.getInvoice_date()) : null;
        this.customer_id = (salesInvoice.getCustomer_id()!=null) ? "CUS-" + salesInvoice.getCustomer_id() : null;
        this.amount = salesInvoice.getAmount();
        this.status = (salesInvoice.getStatus()!=null) ? SalesInvoiceStatus.getString(salesInvoice.getStatus()) : null;
        this.notes = salesInvoice.getNotes();
        this.created_at = (salesInvoice.getCreated_at()!=null) ? TimeUtil.epochToString(salesInvoice.getCreated_at()) : null;
        this.updated_at = (salesInvoice.getUpdated_at()!=null) ? TimeUtil.epochToString(salesInvoice.getUpdated_at()) : null;

        List<InvoiceLineItem> invoiceLineItems = salesInvoice.getInvoice_line_items();

        List<InvoiceLineItemDTO> invoiceLineItemDTOs= new ArrayList<>();
        if(invoiceLineItems!=null) for(InvoiceLineItem invoice : invoiceLineItems){
            invoice.setInvoice_id(null);
            invoiceLineItemDTOs.add(new InvoiceLineItemDTO(invoice));
        }

        this.invoice_line_items = invoiceLineItemDTOs;
    }
    
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

    public List<InvoiceLineItemDTO> getInvoiceLineItems() {
        return invoice_line_items;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setInvoiceLineItems(List<InvoiceLineItemDTO> invoiceLineItems) {
        this.invoice_line_items = invoiceLineItems;
    }
}
