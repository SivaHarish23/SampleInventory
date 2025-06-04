package Util;


import DAO.BillLineItemDAO;
import DAO.InvoiceLineItemDAO;
import Service.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;


public class PrefixValidator {

    private final CustomerService customerService = new CustomerService();
    private final ProductService productService = new ProductService();
    private final PurchaseBillServiceImpl purchaseBillService = new PurchaseBillServiceImpl();
    private final SalesInvoiceServiceImpl salesInvoiceService = new SalesInvoiceServiceImpl();
    private final InvoiceLineItemDAO invoiceLineItemDAO = new InvoiceLineItemDAO();
    private final BillLineItemDAO billLineItemDAO = new BillLineItemDAO();
    private final VendorService vendorService = new VendorService();

    private final Map<EntityType, EntityChecker> entityCheckers = new HashMap<>();

    public PrefixValidator() {
        entityCheckers.put(EntityType.CUSTOMER, id -> customerService.exists(id));
        entityCheckers.put(EntityType.PRODUCT, id -> productService.exists(id));
        entityCheckers.put(EntityType.VENDOR, id -> vendorService.exists(id));
        entityCheckers.put(EntityType.BILL, id -> purchaseBillService.exists(id));
        entityCheckers.put(EntityType.INVOICE, id -> salesInvoiceService.exists(id));
    }

    /**
     * ✅ Recommended strict validation.
     * Verifies format, prefix match with expected entity, and ID existence.
     */
    public String validatePrefixedId(String prefixedId, EntityType expectedType) throws SQLException {
        if (prefixedId == null || !prefixedId.contains("-")) {
            return "Invalid ID format: " + prefixedId;
        }

        String[] parts = prefixedId.split("-");
        if (parts.length != 2) {
            return "Invalid ID format: " + prefixedId;
        }

        String prefix = parts[0];
        int id;
        try {
            id = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "Invalid numeric ID in: " + prefixedId;
        }

        EntityType actualType = EntityType.fromPrefix(prefix);
        if (actualType == null) {
            return "Unknown prefix: " + prefix;
        }

        if (!actualType.equals(expectedType)) {
            return "Expected " + expectedType.getEntityName() + " ID, but got " + actualType.getEntityName();
        }

        EntityChecker checker = entityCheckers.get(expectedType);
        if (checker == null || !checker.exists(id)) {
            return expectedType.getEntityName() + " with ID " + id + " does not exist.";
        }

        return null; // Valid
    }

    /**
     * ✅ Special-case validation when type is ITEM, requiring bill/invoice context.
     */
    public String validatePrefixedId(String prefixedId, String itemTypeIfITM) throws SQLException {
        if (prefixedId == null || !prefixedId.contains("-")) {
            return "Invalid ID format: " + prefixedId;
        }

        String[] parts = prefixedId.split("-");
        if (parts.length != 2) {
            return "Invalid ID format: " + prefixedId;
        }

        String prefix = parts[0];
        int id;
        try {
            id = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "Invalid numeric ID in: " + prefixedId;
        }

        EntityType type = EntityType.fromPrefix(prefix);
        if (type == null) {
            return "Unknown prefix: " + prefix;
        }

        if (type == EntityType.ITEM) {
            if (itemTypeIfITM == null || itemTypeIfITM.isEmpty()) {
                return "Specify whether ITM-" + id + " is from an 'invoice' or 'bill'";
            }

            boolean exists;
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                switch (itemTypeIfITM.toLowerCase()) {
                    case "invoice":
                        exists = invoiceLineItemDAO.readInvoiceItemById(id, conn) != null;
                        break;
                    case "bill":
                        exists = billLineItemDAO.readBillItemById(id, conn) != null;
                        break;
                    default:
                        return "Invalid item type: " + itemTypeIfITM + " (expected 'invoice' or 'bill')";
                }
            }

            if (!exists) {
                return "Line item " + prefixedId + " not found in " + itemTypeIfITM + " items";
            }

            return null;
        }

        // Fallback for other types
        EntityChecker checker = entityCheckers.get(type);
        if (checker == null || !checker.exists(id)) {
            return type.getEntityName() + " with ID " + id + " does not exist.";
        }

        return null;
    }

    /**
     * 🟡 Backward-compatible version (less strict).
     * Not recommended unless you don’t care what type is passed.
     */
    public String validatePrefixedId(String prefixedId) throws SQLException {
        if (prefixedId == null || !prefixedId.contains("-")) {
            return "Invalid format.";
        }

        String[] parts = prefixedId.split("-");
        if (parts.length != 2) {
            return "Invalid format.";
        }

        String prefix = parts[0];
        int id;

        try {
            id = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "Invalid ID format.";
        }

        EntityType type = EntityType.fromPrefix(prefix);
        if (type == null) {
            return "Unknown prefix: " + prefix;
        }

        EntityChecker checker = entityCheckers.get(type);
        if (checker == null || !checker.exists(id)) {
            return type.getEntityName() + " with ID " + id + " does not exist.";
        }

        return null; // Valid
    }

    public enum EntityType {
        CUSTOMER("CUS", "Customer"),
        PRODUCT("PRO", "Product"),
        VENDOR("VEN", "Vendor"),
        BILL("BIL", "PurchaseBill"),
        INVOICE("INV", "SalesInvoice"),
        ITEM("ITM", "Item");

        private final String prefix;
        private final String name;

        EntityType(String prefix, String name) {
            this.prefix = prefix;
            this.name = name;
        }

        public static EntityType fromPrefix(String prefix) {
            for (EntityType type : values()) {
                if (type.getPrefix().equals(prefix)) return type;
            }
            return null;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getEntityName() {
            return name;
        }
    }

    public interface EntityChecker {
        boolean exists(int id) throws SQLException;
    }
}

