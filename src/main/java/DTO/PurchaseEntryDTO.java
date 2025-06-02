package DTO;

import java.math.BigDecimal;

public class PurchaseEntryDTO {

//    private final Integer product_id;
    private final Integer quantity;
    private final BigDecimal rate;

    public PurchaseEntryDTO( Integer quantity, BigDecimal rate) {
//        this.product_id = product_id;
        this.quantity = quantity;
        this.rate = rate;
    }

//    public Integer getProduct_id() {
//        return product_id;
//    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
