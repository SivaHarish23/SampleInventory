package DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseBillDTO {
    private Integer id, vendor_id;
    private String status;
    private String bill_date;
    private LocalDateTime created_at, updated_at;
    private BigDecimal amount;
    public List<BillItemDTO> items;

    public PurchaseBillDTO() {
    }

    public PurchaseBillDTO(Integer id, Integer vendor_id, String status, String bill_date, LocalDateTime created_at, LocalDateTime updated_at, BigDecimal amount, List<BillItemDTO> items) {
        this.id = id;
        this.vendor_id = vendor_id;
        this.status = status;
        this.bill_date = bill_date;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.amount = amount;
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(Integer vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<BillItemDTO> getItems() {
        return items;
    }

    public void setItems(List<BillItemDTO> items) {
        this.items = items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id, vendor_id;
        private String status, bill_date;
        private LocalDateTime created_at, updated_at;
        private BigDecimal amount;
        private List<BillItemDTO> items;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder vendorId(Integer vendor_id) {
            this.vendor_id = vendor_id;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder billDate(String bill_date) {
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

        public Builder items(List<BillItemDTO> items) {
            this.items = items;
            return this;
        }

        public PurchaseBillDTO build() {
            return new PurchaseBillDTO(id, vendor_id, status, bill_date, created_at, updated_at, amount, items);
        }
    }

    @Override
    public String toString() {
        return "PurchaseBillDTO{" +
                "id=" + id +
                ", vendor_id=" + vendor_id +
                ", status='" + status + '\'' +
                ", bill_date='" + bill_date + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", amount=" + amount +
                ", items=" + items +
                '}';
    }
}
