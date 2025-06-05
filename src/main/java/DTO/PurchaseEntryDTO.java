package DTO;

import java.math.BigDecimal;

public class PurchaseEntryDTO {

    private final Integer quantity;
    private final BigDecimal rate;

    public PurchaseEntryDTO( Integer quantity, BigDecimal rate) {
        this.quantity = quantity;
        this.rate = rate;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
