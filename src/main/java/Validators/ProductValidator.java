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

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.put("name", "Product name is required.");
        } else if (productDAO.findProductByName(product.getName())!=null) {
            errors.put("name", "Product name must be unique.");
        }

        if (product.getCost_price() == null || product.getCost_price().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("cost_price", "Cost price must be greater than 0.");
        }

        if (product.getSelling_price() == null || product.getSelling_price().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("selling_price", "Selling price must be greater than 0.");
        }

        if (product.getOpening_stock() == null || product.getOpening_stock() < 0) {
            errors.put("opening_stock", "Opening stock must be greater than 0.");
        }

        return errors;
    }
}
