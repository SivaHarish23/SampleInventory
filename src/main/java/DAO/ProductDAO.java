package DAO;

import DTO.ProductUpdateDTO;
import Model.Product;
import Util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Create
    public int save(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, cost_price, selling_price, stock_in_hand, committed_stock, ordered_stock, opening_stock) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getCostPrice());
            ps.setBigDecimal(3, product.getSellingPrice());
            ps.setInt(4, product.getStockInHand());
            ps.setInt(5, product.getCommittedStock());
            ps.setInt(6, product.getOrderedStock());
            ps.setInt(7, product.getOpeningStock());
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Creating product failed, no rows affected.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                    return product.getId();
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            }
        }
    }

    // Read by id
    public Product getById(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product.Builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .costPrice(rs.getBigDecimal("cost_price"))
                            .sellingPrice(rs.getBigDecimal("selling_price"))
                            .stockInHand(rs.getInt("stock_in_hand"))
                            .committedStock(rs.getInt("committed_stock"))
                            .orderedStock(rs.getInt("ordered_stock"))
                            .openingStock(rs.getInt("opening_stock"))
                            .build();
                }
            }
        }
        return null;
    }

    // Read all
    public List<Product> getAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(new Product.Builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .costPrice(rs.getBigDecimal("cost_price"))
                        .sellingPrice(rs.getBigDecimal("selling_price"))
                        .stockInHand(rs.getInt("stock_in_hand"))
                        .committedStock(rs.getInt("committed_stock"))
                        .orderedStock(rs.getInt("ordered_stock"))
                        .openingStock(rs.getInt("opening_stock"))
                        .build());
            }
        }
        return products;
    }

    // Update full product
    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, cost_price = ?, selling_price = ?, stock_in_hand = ?, committed_stock = ?, ordered_stock = ?, opening_stock = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getCostPrice());
            ps.setBigDecimal(3, product.getSellingPrice());
            ps.setInt(4, product.getStockInHand());
            ps.setInt(5, product.getCommittedStock());
            ps.setInt(6, product.getOrderedStock());
            ps.setInt(7, product.getOpeningStock());
            ps.setInt(8, product.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // Delete
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Partial update
    public boolean partialUpdate(int id, ProductUpdateDTO dto) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE products SET ");
        List<Object> values = new ArrayList<>();

        if (dto.getName() != null) {
            sql.append("name = ?, ");
            values.add(dto.getName());
        }
        if (dto.getCostPrice() != null) {
            sql.append("cost_price = ?, ");
            values.add(dto.getCostPrice());
        }
        if (dto.getSellingPrice() != null) {
            sql.append("selling_price = ?, ");
            values.add(dto.getSellingPrice());
        }
        if (dto.getStockInHand() != null) {
            sql.append("stock_in_hand = ?, ");
            values.add(dto.getStockInHand());
        }
        if (dto.getCommittedStock() != null) {
            sql.append("committed_stock = ?, ");
            values.add(dto.getCommittedStock());
        }
        if (dto.getOrderedStock() != null) {
            sql.append("ordered_stock = ?, ");
            values.add(dto.getOrderedStock());
        }
        if (dto.getOpeningStock() != null) {
            sql.append("opening_stock = ?, ");
            values.add(dto.getOpeningStock());
        }

        if (values.isEmpty()) return false; // nothing to update

        sql.setLength(sql.length() - 2); // remove last comma
        sql.append(" WHERE id = ?");
        values.add(id);

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            return ps.executeUpdate() > 0;
        }
    }
}
