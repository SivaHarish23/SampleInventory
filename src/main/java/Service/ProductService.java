package Service;

import DAO.ProductDAO;
import DTO.ProductDTO;
import DTO.ProductUpdateDTO;
import Model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    public boolean addProduct(ProductDTO productDTO) throws SQLException {
        Product product = new Product.Builder()
                .name(productDTO.getName())
                .cost_price(productDTO.getCost_price())
                .selling_price(productDTO.getSelling_price())
                .opening_stock(productDTO.getOpening_stock())//stock in hand = opening stock during product creation
                .build();

        return productDAO.insert(product);
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAllRows();
    }

    public Product getProductById(int id) throws SQLException {
        return productDAO.findProduct(id);
    }

    public boolean updateProduct(ProductUpdateDTO dto) throws SQLException {
        return productDAO.updateProduct(dto);
    }

    public boolean deleteProduct(int id) throws SQLException {
        return productDAO.deleteProduct(id);
    }

}