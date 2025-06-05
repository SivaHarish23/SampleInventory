package DTO;

import Model.BillLineItem;
import Model.PurchaseBill;
import Model.PurchaseBillStatus;
import Util.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PurchaseBillDTO {
    private String id;
    private String bill_date;
    private String vendor_id;
    private BigDecimal amount;
    private String status;
    private String notes;
    private String created_at;
    private String updated_at;
    private List<BillLineItemDTO> bill_line_items;

    public PurchaseBillDTO() {
    }

    public PurchaseBillDTO(PurchaseBill purchaseBill){
        this.id = (purchaseBill.getId()!=null) ? "BIL-" + purchaseBill.getId() : null;
        this.bill_date = (purchaseBill.getBill_date()!=null) ? TimeUtil.epochToString(purchaseBill.getBill_date()).substring(0,10) : null;
        this.vendor_id = (purchaseBill.getVendor_id()!=null) ? "VEN-" + purchaseBill.getVendor_id() : null;
        this.amount = purchaseBill.getAmount();
        this.status = (purchaseBill.getStatus()!=null) ? PurchaseBillStatus.getString(purchaseBill.getStatus()) : null;
        this.notes = purchaseBill.getNotes();
        this.created_at = (purchaseBill.getCreated_at()!=null) ? TimeUtil.epochToString(purchaseBill.getCreated_at()) : null;
        this.updated_at = (purchaseBill.getUpdated_at()!=null) ? TimeUtil.epochToString(purchaseBill.getUpdated_at()) : null;

        List<BillLineItem> billLineItems = purchaseBill.getBill_line_items();

        List<BillLineItemDTO> billLineItemDTOs= new ArrayList<>();
        if(billLineItems!=null) for(BillLineItem bill : billLineItems){
            bill.setBill_id(null);
            billLineItemDTOs.add(new BillLineItemDTO(bill));
        }

        this.bill_line_items = billLineItemDTOs;
    }


    // Private constructor
    private PurchaseBillDTO(Builder builder) {
        this.id = builder.id;
        this.bill_date = builder.bill_date;
        this.vendor_id = builder.vendor_id;
        this.amount = builder.amount;
        this.status = builder.status;
        this.notes = builder.notes;
        this.created_at = builder.created_at;
        this.updated_at = builder.updated_at;
        this.bill_line_items = builder.bill_line_items;
    }

    // Builder class
    public static class Builder {
        private String id;
        private String bill_date;
        private String vendor_id;
        private BigDecimal amount;
        private String status;
        private String notes;
        private String created_at;
        private String updated_at;
        private List<BillLineItemDTO> bill_line_items;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder bill_date(String bill_date) {
            this.bill_date = bill_date;
            return this;
        }

        public Builder vendor_id(String vendor_id) {
            this.vendor_id = vendor_id;
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

        public Builder bill_line_items(List<BillLineItemDTO> bill_line_items) {
            this.bill_line_items = bill_line_items;
            return this;
        }

        public PurchaseBillDTO build() {
            return new PurchaseBillDTO(this);
        }
    }


    // Getters
    public String getId() {
        return id;
    }

    public String getBill_date() {
        return bill_date;
    }

    public String getVendor_id() {
        return vendor_id;
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

    public List<BillLineItemDTO> getBillLineItems() {
        return bill_line_items;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
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

    public void setBillLineItems(List<BillLineItemDTO> bill_line_items) {
        this.bill_line_items = bill_line_items;
    }

    @Override
    public String toString() {
        return "PurchaseBillDTO{" +
                "id='" + id + '\'' +
                ", bill_date='" + bill_date + '\'' +
                ", vendor_id='" + vendor_id + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", bill_line_items=" + bill_line_items +
                '}';
    }
}
