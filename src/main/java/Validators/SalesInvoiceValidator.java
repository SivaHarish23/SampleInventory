package Validators;

import DAO.CustomerDAO;
import DAO.InvoiceLineItemDAO;
import DAO.ProductDAO;
import DTO.InvoiceLineItemDTO;
import DTO.SalesInvoiceDTO;
import Model.SalesInvoiceStatus;
import Util.DBConnection;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.gson.GsonBuilder;

public class SalesInvoiceValidator {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final CustomerDAO customerDAO;
    private final ProductDAO productDAO;
    private final InvoiceLineItemDAO invoiceLineItemDAO;

    public SalesInvoiceValidator(CustomerDAO customerDAO, ProductDAO productDAO, InvoiceLineItemDAO invoiceLineItemDAO) {
        this.customerDAO = customerDAO;
        this.productDAO = productDAO;
        this.invoiceLineItemDAO = invoiceLineItemDAO;
    }

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
            customerErrors.add("Customer ID is required.");
        } else {
            try {
                int customerId = Integer.parseInt(invoice.getCustomer_id().replaceAll("\\D", ""));
                if (customerDAO.getPartyById(customerId) == null) {
                    customerErrors.add("Customer does not exist.");
                }
            } catch (NumberFormatException e) {
                customerErrors.add("Invalid customer ID format.");
            }
        }
        if (!customerErrors.isEmpty()) errors.put("customer_id", customerErrors);

        // Validate invoice_date
        try {
            LocalDateTime.parse(invoice.getInvoice_date(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            errors.put("invoice_date", Collections.singletonList("Invalid date format. Use yyyy-MM-dd HH:mm:ss."));
        }

        // Validate status
        try {
            SalesInvoiceStatus.fromString(invoice.getStatus());
        } catch (IllegalArgumentException e) {
            errors.put("status", Collections.singletonList("Invalid status. Must be PAID or DELIVERED."));
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
                        try (Connection conn = DBConnection.getInstance().getConnection()) {
                            int id = Integer.parseInt(item.getId().replaceAll("\\D", ""));
                            if (invoiceLineItemDAO.readInvoiceItemById(id, conn) == null) {
                                itemErrors.add("Line item does not exist.");
                            }
                        } catch (NumberFormatException e) {
                            itemErrors.add("Invalid line item ID format.");
                        }
                    }
                } else {
                    if (item.getId() != null && !item.getId().trim().isEmpty()) {
                        itemErrors.add("Line item ID should not be set when creating.");
                    }
                }

                // Validate product_id
                try {
                    int productId = Integer.parseInt(item.getProduct_id().replaceAll("\\D", ""));
                    if (productDAO.findProduct(productId) == null) {
                        itemErrors.add("Product does not exist.");
                    }
                } catch (NumberFormatException e) {
                    itemErrors.add("Invalid product ID format.");
                }

                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    itemErrors.add("Quantity must be > 0.");
                }

                if (item.getRate() == null || item.getRate().compareTo(BigDecimal.ZERO) <= 0) {
                    itemErrors.add("Rate must be > 0.");
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