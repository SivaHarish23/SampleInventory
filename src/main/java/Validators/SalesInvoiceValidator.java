package Validators;

import DAO.CustomerDAO;
import DAO.InvoiceLineItemDAO;
import DAO.ProductDAO;
import DTO.InvoiceLineItemDTO;
import DTO.SalesInvoiceDTO;
import Model.SalesInvoiceStatus;
import Util.DBConnection;
import Util.PrefixValidator;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.gson.GsonBuilder;

public class SalesInvoiceValidator {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public Map<String, List<String>> validateForCreate(SalesInvoiceDTO invoice) throws SQLException {
        return validate(invoice, false);
    }

    public Map<String, List<String>> validateForUpdate(SalesInvoiceDTO invoice) throws SQLException {
        return validate(invoice, true);
    }

    public Map<String, List<String>> validate(SalesInvoiceDTO invoice, boolean isUpdate) throws SQLException {
        Map<String, List<String>> errors = new LinkedHashMap<>();


        // Validate customer_id
        List<String> customerErrors = new ArrayList<>();
        if (invoice.getCustomer_id() == null || invoice.getCustomer_id().trim().isEmpty()) {
            if(!isUpdate) customerErrors.add("Customer ID is required.");
        } else {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(invoice.getCustomer_id(), PrefixValidator.EntityType.CUSTOMER);
            if (error != null) {
                customerErrors.add(error);
            }
        }
        if (!customerErrors.isEmpty()) errors.put("customer_id", customerErrors);

        // Validate invoice_date
        if (invoice.getInvoice_date() == null || invoice.getInvoice_date().trim().isEmpty()) {
            if(!isUpdate) errors.put("invoice_date", Collections.singletonList("Invoice date is required"));
        } else {
            try {
                LocalDate.parse(invoice.getInvoice_date(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                errors.put("invoice_date", Collections.singletonList("Invalid date format. Use yyyy-MM-dd."));
            }
        }

        // Validate status
        if (invoice.getStatus() == null || invoice.getStatus().trim().isEmpty()) {
            if(!isUpdate) errors.put("status", Collections.singletonList("Status is required"));
        } else {
            try {
                SalesInvoiceStatus.fromString(invoice.getStatus());
            } catch (IllegalArgumentException e) {
                errors.put("status", Collections.singletonList("Invalid status. Must be PAID or DELIVERED."));
            }
        }

        // Validate invoice_line_items
        if (invoice.getInvoiceLineItems() == null || invoice.getInvoiceLineItems().isEmpty()) {
            errors.put("invoice_line_items", Collections.singletonList("At least one line item is required."));
        } else {
            for (InvoiceLineItemDTO item : invoice.getInvoiceLineItems()) {
                List<String> itemErrors = new ArrayList<>();

                if (isUpdate) {
                    if (item.getId() == null || item.getId().trim().isEmpty()) {
                        itemErrors.add("Line item ID is required for update.");
                    } else {
                        PrefixValidator validator = new PrefixValidator();
                        String error = validator.validatePrefixedId(item.getId(), "invoice");
                        if (error != null) {
                            itemErrors.add(error);
                        }
                    }
                } else {
                    if (item.getId() != null && !item.getId().trim().isEmpty()) {
                        itemErrors.add("Line item ID should not be set when creating.");
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