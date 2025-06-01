package Model;

import java.math.BigDecimal;

public class SalesInvoice {
    private Integer id;
    private Long invoice_date;
    private Integer customer_id;
    private BigDecimal amount;
    private Integer status;
    private String notes;
    private Long created_at;
    private Long updated_at;

    public SalesInvoice() {
    }

    // Private constructor used by the Builder
    private SalesInvoice(Builder builder) {
        this.id = builder.id;
        this.invoice_date = builder.invoice_date;
        this.customer_id = builder.customer_id;
        this.amount = builder.amount;
        this.status = builder.status;
        this.notes = builder.notes;
        this.created_at = builder.created_at;
        this.updated_at = builder.updated_at;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Long getInvoice_date() {
        return invoice_date;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public Long getUpdated_at() {
        return updated_at;
    }

    // Builder class
    public static class Builder {
        private Integer id;
        private Long invoice_date;
        private Integer customer_id;
        private BigDecimal amount;
        private Integer status;
        private String notes;
        private Long created_at;
        private Long updated_at;

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setInvoice_date(Long invoice_date) {
            this.invoice_date = invoice_date;
            return this;
        }

        public Builder setCustomer_id(Integer customer_id) {
            this.customer_id = customer_id;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder setStatus(Integer status) {
            this.status = status;
            return this;
        }

        public Builder setNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder setCreated_at(Long created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder setUpdated_at(Long updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public SalesInvoice build() {
            return new SalesInvoice(this);
        }
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setInvoice_date(Long invoice_date) {
        this.invoice_date = invoice_date;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }
}
