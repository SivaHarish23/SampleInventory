package Service;

import DAO.ProductDAO;
import DTO.ProductDTO;
import DTO.ProductUpdateDTO;
import Model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    public int addProduct(ProductDTO dto) throws SQLException {
        Product product = new Product();
        product.setName(dto.getName());
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setStockInHand(dto.getStockInHand());
        product.setCommittedStock(dto.getCommittedStock());
        product.setOrderedStock(dto.getOrderedStock());
        product.setOpeningStock(dto.getOpeningStock());
        return productDAO.save(product);
    }

    public Product getProduct(int id) throws SQLException {
        return productDAO.getById(id);
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAll();
    }

    public boolean updateProduct(int id, ProductDTO dto) throws SQLException {
        Product product = new Product();
        product.setId(id);
        product.setName(dto.getName());
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setStockInHand(dto.getStockInHand());
        product.setCommittedStock(dto.getCommittedStock());
        product.setOrderedStock(dto.getOrderedStock());
        product.setOpeningStock(dto.getOpeningStock());
        return productDAO.update(product);
    }

    public boolean deleteProduct(int id) throws SQLException {
        return productDAO.delete(id);
    }

    public boolean partialUpdateProduct(int id, ProductUpdateDTO dto) throws SQLException {
        return productDAO.partialUpdate(id, dto);
    }
}
