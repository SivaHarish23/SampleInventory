package Validators;

import DAO.BillLineItemDAO;
import DAO.ProductDAO;
import DAO.VendorDAO;
import DTO.BillLineItemDTO;
import DTO.PurchaseBillDTO;
import Model.PurchaseBillStatus;
import Util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.*;

public class PurchaseBillValidator {

    private final VendorDAO vendorDAO;
    private final ProductDAO productDAO;
    private final BillLineItemDAO billLineItemDAO;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public PurchaseBillValidator(VendorDAO vendorDAO, ProductDAO productDAO, BillLineItemDAO billLineItemDAO) {
        this.vendorDAO = vendorDAO;
        this.productDAO = productDAO;
        this.billLineItemDAO = billLineItemDAO;
    }

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
            vendorErrors.add("Vendor ID is required.");
        } else {
            try {
                int vendorId = Integer.parseInt(bill.getVendor_id().replaceAll("\\D", ""));
                if (vendorDAO.getPartyById(vendorId) == null) {
                    vendorErrors.add("Vendor does not exist.");
                }
            } catch (NumberFormatException e) {
                vendorErrors.add("Invalid vendor ID format.");
            }
        }
        if (!vendorErrors.isEmpty()) errors.put("vendor_id", vendorErrors);

        // Bill date validation
        try {
            LocalDate.parse(bill.getBill_date(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            errors.put("bill_date", Collections.singletonList("Invalid bill date format. Use yyyy-MM-dd."));
        }

        // Status validation (optional)
        if (bill.getStatus() != null && !bill.getStatus().trim().isEmpty()) {
            try {
                PurchaseBillStatus.valueOf(bill.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.put("status", Collections.singletonList("Invalid status value."));
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
                        try (Connection conn = DBConnection.getInstance().getConnection()) {
                            int id = Integer.parseInt(item.getId().replaceAll("\\D", ""));
                            if (billLineItemDAO.readBillItemById(id, conn) == null) {
                                itemErrors.add("Line item ID does not exist.");
                            }
                        } catch (NumberFormatException e) {
                            itemErrors.add("Invalid line item ID format.");
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
                    try {
                        int productId = Integer.parseInt(item.getProduct_id().replaceAll("\\D", ""));
                        if (productDAO.findProduct(productId) == null) {
                            itemErrors.add("Product does not exist.");
                        }
                    } catch (NumberFormatException e) {
                        itemErrors.add("Invalid product ID format.");
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