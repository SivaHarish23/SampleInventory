package Service;

import DAO.ProductDAO;
import DTO.ProductDTO;
import Model.Product;
import Validators.ProductValidator;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();
    private final ProductValidator validator = new ProductValidator();

    public Map<String, String> validate(ProductDTO product) throws SQLException {
        return validator.validate(product);
    }

    public Product addProduct(Product productDTO) throws SQLException {
        Product product = new Product.Builder()
                .name(productDTO.getName())
                .cost_price(productDTO.getCost_price())
                .selling_price(productDTO.getSelling_price())
                .opening_stock(productDTO.getOpening_stock())
                .created_at(Instant.now().getEpochSecond())
                .updated_at(Instant.now().getEpochSecond())
                .build();

        return productDAO.insert(product);
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAllRows();
    }

    public Product getProductById(Integer id) throws SQLException {
        return productDAO.findProduct(id);
    }

    public Product updateProduct(Product dto) throws SQLException {
        return productDAO.updateProduct(dto);
    }

    public boolean deleteProduct(Integer id) throws SQLException {
        if (productDAO.isProductUsed(id)) {
            throw new IllegalStateException("Cannot delete: Product (PRO- " + id + ") is used in purchase or sales transactions.");
        }
        return productDAO.deleteProduct(id);
    }

    public boolean exists(int id) throws SQLException {
        return productDAO.findProduct(id)!=null;
    }
}