package Model;

import DTO.BillLineItemDTO;
import DTO.PurchaseBillDTO;
import Util.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PurchaseBill {
    private Integer id;
    private Long bill_date;
    private Integer vendor_id;
    private BigDecimal amount;
    private Integer status;
    private String notes;
    private Long created_at;
    private Long updated_at;
    private List<BillLineItem> bill_line_items;

    public PurchaseBill(PurchaseBillDTO purchaseBill){
        try{
            this.id = (purchaseBill.getId() != null) ? Integer.parseInt(purchaseBill.getId().substring(4)) : null;
            this.bill_date = (purchaseBill.getBill_date() != null) ? TimeUtil.stringToEpoch(purchaseBill.getBill_date()) : null;
            this.vendor_id = (purchaseBill.getVendor_id() != null) ? Integer.parseInt(purchaseBill.getVendor_id().substring(4)) : null;
            this.amount = purchaseBill.getAmount();
            this.status = (purchaseBill.getStatus() != null) ? PurchaseBillStatus.getCode(purchaseBill.getStatus()) : null;
            this.notes = purchaseBill.getNotes();
            this.created_at = (purchaseBill.getCreated_at() != null) ? TimeUtil.stringToEpoch(purchaseBill.getCreated_at()) : null;
            this.updated_at = (purchaseBill.getUpdated_at() != null) ? TimeUtil.stringToEpoch(purchaseBill.getUpdated_at()) : null;

            List<BillLineItemDTO> billLineItemDTOs = purchaseBill.getBillLineItems();

            //attaching bill id to bills items
            List<BillLineItem> billLineItems = new ArrayList<>();
            if (billLineItemDTOs != null)
                for (BillLineItemDTO billDTO : billLineItemDTOs) {
                    billDTO.setBill_id(purchaseBill.getId());

                    billLineItems.add(new BillLineItem(billDTO));
                }

            this.bill_line_items = billLineItems;
        } catch (Exception e) {
            System.out.println("Purchase Bill Constructor : " + e);
            e.printStackTrace();
            throw e;
        }
    }


    // Private constructor used by the Builder
    private PurchaseBill(Builder builder) {
        this.id = builder.id;
        this.bill_date = builder.bill_date;
        this.vendor_id = builder.vendor_id;
        this.amount = builder.amount;
        this.status = builder.status;
        this.notes = builder.notes;
        this.created_at = builder.created_at;
        this.updated_at = builder.updated_at;
    }

    public PurchaseBill() {

    }


    // Builder class
    public static class Builder {
        private Integer id;
        private Long bill_date;
        private Integer vendor_id;
        private BigDecimal amount;
        private Integer status;
        private String notes;
        private Long created_at;
        private Long updated_at;

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setBill_date(Long bill_date) {
            this.bill_date = bill_date;
            return this;
        }

        public Builder setVendor_id(Integer vendor_id) {
            this.vendor_id = vendor_id;
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

        // Build method to return the final PurchaseBill object
        public PurchaseBill build() {
            return new PurchaseBill(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getBill_date() {
        return bill_date;
    }

    public void setBill_date(Long bill_date) {
        this.bill_date = bill_date;
    }

    public Integer getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(Integer vendor_id) {
        this.vendor_id = vendor_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public Long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }

    public List<BillLineItem> getBill_line_items() {
        return bill_line_items;
    }

    public void setBill_line_items(List<BillLineItem> bill_line_items) {
        this.bill_line_items = bill_line_items;
    }

    @Override
    public String toString() {
        return "PurchaseBill{" +
                "id=" + id +
                ", bill_date=" + bill_date +
                ", vendor_id=" + vendor_id +
                ", amount=" + amount +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", bill_line_items=" + bill_line_items +
                '}';
    }
}
