package Service;

import DAO.ProductDAO;
import DTO.ProductDTO;
import Model.Product;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

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
        return productDAO.deleteProduct(id);
    }

}