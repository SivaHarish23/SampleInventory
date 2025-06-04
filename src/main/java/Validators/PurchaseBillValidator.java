package Validators;

import DTO.BillLineItemDTO;
import DTO.PurchaseBillDTO;
import Model.PurchaseBillStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import Util.PrefixValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public class PurchaseBillValidator {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public Map<String, List<String>> validateForCreate(PurchaseBillDTO bill) throws SQLException {
        return validate(bill, false);
    }

    public Map<String, List<String>> validateForUpdate(PurchaseBillDTO bill) throws SQLException {
        return validate(bill, true);
    }

    public Map<String, List<String>> validate(PurchaseBillDTO bill, boolean isUpdate) throws SQLException {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        // Vendor ID check
        List<String> vendorErrors = new ArrayList<>();
        if (bill.getVendor_id() == null || bill.getVendor_id().trim().isEmpty()) {
            if(!isUpdate) vendorErrors.add("Vendor ID is required.");
        } else {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(bill.getVendor_id(), PrefixValidator.EntityType.VENDOR);
            if (error != null) {
                vendorErrors.add(error);
            }
        }
        if (!vendorErrors.isEmpty()) errors.put("vendor_id", vendorErrors);

        // Bill date validation
        if (bill.getBill_date() == null || bill.getBill_date().trim().isEmpty()) {
            if(!isUpdate) errors.put("bill_date", Collections.singletonList("Bill Date is required"));
        } else {
            try {
                LocalDate.parse(bill.getBill_date(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                errors.put("bill_date", Collections.singletonList("Invalid bill date format. Use yyyy-MM-dd."));
            }
        }

        if (bill.getStatus() == null || bill.getStatus().trim().isEmpty()) {
            if(!isUpdate) errors.put("status", Collections.singletonList("Status is required"));
        } else {
            try {
                PurchaseBillStatus.valueOf(bill.getStatus());
            } catch (IllegalArgumentException e) {
                errors.put("status", Collections.singletonList("Invalid status. Must be PAID or RECEIVED."));
            }
        }

        // Validate bill_line_items
        if (bill.getBillLineItems() == null || bill.getBillLineItems().isEmpty()) {
            errors.put("bill_line_items", Collections.singletonList("At least one line item is required."));
        } else {
            for (BillLineItemDTO item : bill.getBillLineItems()) {
                List<String> itemErrors = new ArrayList<>();

                // ID check
                if (isUpdate) {
                    if (item.getId() == null || item.getId().trim().isEmpty()) {
                        itemErrors.add("Line item ID is required for update.");
                    } else {
                        PrefixValidator validator = new PrefixValidator();
                        String error = validator.validatePrefixedId(item.getId(), "bill");
                        if (error != null) {
                            itemErrors.add(error);
                        }
                    }
                } else {
                    if (item.getId() != null && !item.getId().trim().isEmpty()) {
                        itemErrors.add("Line item ID should not be provided during creation.");
                    }
                }

                // Product ID check
                if (item.getProduct_id() == null || item.getProduct_id().trim().isEmpty()) {
                    itemErrors.add("Product ID is required.");
                } else {
                    PrefixValidator validator = new PrefixValidator();
                    String error = validator.validatePrefixedId(item.getProduct_id(), PrefixValidator.EntityType.PRODUCT);
                    if (error != null) {
                        itemErrors.add(error);
                    }
                }

                // Quantity and rate validation
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    itemErrors.add("Quantity must be greater than 0.");
                }

                if (item.getRate() == null || item.getRate().compareTo(BigDecimal.ZERO) <= 0) {
                    itemErrors.add("Rate must be greater than 0.");
                }

                if (!itemErrors.isEmpty()) {
                    String key = item.getProduct_id() != null ? item.getProduct_id() : "UNKNOWN_PRODUCT";
                    errors.put(key, itemErrors);
                }
            }
        }
        return errors;
    }
}