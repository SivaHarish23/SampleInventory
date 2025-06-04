package Validators;

import DAO.ProductDAO;
import DTO.ProductDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProductValidator {

    private final ProductDAO productDAO = new ProductDAO();

    public Map<String, String> validate(ProductDTO product) throws SQLException {
        Map<String, String> errors = new HashMap<>();

        // Product Name Validation
        String name = product.getName();
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Product name is required and missing.");
        } else {
            if (productDAO.findProductByName(name.trim()) != null) {
                errors.put("name", "Product name already exists.");
            }
        }

        // Cost Price Validation
        BigDecimal costPrice = product.getCost_price();
        if (costPrice == null) {
            errors.put("cost_price", "Cost price is required and missing.");
        } else if (costPrice.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("cost_price", "Cost price must be greater than 0.");
        }

        // Selling Price Validation
        BigDecimal sellingPrice = product.getSelling_price();
        if (sellingPrice == null) {
            errors.put("selling_price", "Selling price is required and missing.");
        } else if (sellingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("selling_price", "Selling price must be greater than 0.");
        }

        // Opening Stock Validation
        Integer openingStock = product.getOpening_stock();
        if (openingStock == null) {
            errors.put("opening_stock", "Opening stock is required and missing.");
        } else if (openingStock < 0) {
            errors.put("opening_stock", "Opening stock must be 0 or greater.");
        }

        return errors;
    }
    public Map<String, String> validateForUpdate(ProductDTO product) throws SQLException {
        Map<String, String> errors = new HashMap<>();
        boolean hasAtLeastOneField = false;

        // Product Name Validation
        String name = product.getName();
        if (name != null) {
            hasAtLeastOneField = true;
            if (name.trim().isEmpty()) {
                errors.put("name", "Product name is provided but empty.");
            } else {
                if (productDAO.findProductByName(name.trim()) != null) {
                    errors.put("name", "Product name already exists.");
                }
            }
        }

        // Cost Price Validation
        BigDecimal costPrice = product.getCost_price();
        if (costPrice != null) {
            hasAtLeastOneField = true;
            if (costPrice.compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("cost_price", "Cost price must be greater than 0.");
            }
        }

        // Selling Price Validation
        BigDecimal sellingPrice = product.getSelling_price();
        if (sellingPrice != null) {
            hasAtLeastOneField = true;
            if (sellingPrice.compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("selling_price", "Selling price must be greater than 0.");
            }
        }

        // Opening Stock Validation
        Integer openingStock = product.getOpening_stock();
        if (openingStock != null) {
            hasAtLeastOneField = true;
            if (openingStock < 0) {
                errors.put("opening_stock", "Opening stock must be 0 or greater.");
            }
        }

        // Check if nothing was provided at all
        if (!hasAtLeastOneField) {
            errors.put("general", "At least one field must be provided for update.");
        }

        return errors;
    }
}