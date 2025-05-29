package Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SalesBill implements Bill{
    private int id, customer_id;
    private String status;
    private LocalDate bill_date;
    private LocalDateTime created_at, updated_at;
    private BigDecimal amount;

    public SalesBill() {
    }

    public SalesBill(int id, int customer_id, String status, LocalDate bill_date, LocalDateTime created_at, LocalDateTime updated_at, BigDecimal amount) {
        this.id = id;
        this.customer_id = customer_id;
        this.status = status;
        this.bill_date = bill_date;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.amount = amount;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public LocalDate getBill_date() {
        return bill_date;
    }

    @Override
    public void setBill_date(LocalDate bill_date) {
        this.bill_date = bill_date;
    }

    @Override
    public LocalDateTime getCreated_at() {
        return created_at;
    }

    @Override
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    @Override
    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    @Override
    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private int customer_id;
        private String status;
        private LocalDate bill_date;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;
        private BigDecimal amount;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder customerId(int customer_id) {
            this.customer_id = customer_id;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder billDate(LocalDate bill_date) {
            this.bill_date = bill_date;
            return this;
        }

        public Builder createdAt(LocalDateTime created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder updatedAt(LocalDateTime updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SalesBill build() {
            return new SalesBill(id, customer_id, status, bill_date, created_at, updated_at, amount);
        }
    }
}


