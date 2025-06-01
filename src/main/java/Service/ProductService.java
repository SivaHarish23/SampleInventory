package Service;

import DAO.ProductDAO;
import DTO.ProductDTO;
import Model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    public ProductDTO addProduct(ProductDTO productDTO) throws SQLException {
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

    public List<ProductDTO> getAllProducts() throws SQLException {
        return productDAO.getAllRows();
    }

    public ProductDTO getProductById(String id) throws SQLException {
        id = id.substring(4);
        return productDAO.findProduct(Integer.parseInt(id));
    }

    public ProductDTO updateProduct(ProductDTO dto) throws SQLException {
        dto.setId(dto.getId().substring(4));
        return productDAO.updateProduct(dto);
    }

    public boolean deleteProduct(String id) throws SQLException {
        id = id.substring(4);
        return productDAO.deleteProduct(Integer.parseInt(id));
    }

}