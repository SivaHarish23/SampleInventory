package DAO;

import DTO.ProductUpdateDTO;
import Model.Product;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public boolean insert(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, cost_price, selling_price, stock_in_hand, opening_stock) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setBigDecimal(2, product.getCost_price());
            preparedStatement.setBigDecimal(3, product.getSelling_price());
            preparedStatement.setInt(4, product.getStock_in_hand());
            preparedStatement.setInt(5, product.getOpening_stock());

            int rowsAffected = preparedStatement.executeUpdate(); // returns number of rows inserted
            return rowsAffected > 0;
        }
    }

    public List<Product> getAllRows() throws SQLException {
        List<Product> allProducts = new ArrayList<>();
        String sql = "SELECT * from products";
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                allProducts.add(new Product.Builder().id(resultSet.getInt("id")).name(resultSet.getString("name")).cost_price(resultSet.getBigDecimal("cost_price")).selling_price(resultSet.getBigDecimal("selling_price")).stock_in_hand(resultSet.getInt("stock_in_hand")).opening_stock(resultSet.getInt("opening_stock")).ordered_stock(resultSet.getInt("ordered_stock")).committed_stock(resultSet.getInt("committed_stock")).build());
            }
        }
        return allProducts;
    }

    public Product findProduct(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return null;
            return new Product.Builder().id(resultSet.getInt("id")).name(resultSet.getString("name")).cost_price(resultSet.getBigDecimal("cost_price")).selling_price(resultSet.getBigDecimal("selling_price")).stock_in_hand(resultSet.getInt("stock_in_hand")).opening_stock(resultSet.getInt("opening_stock")).ordered_stock(resultSet.getInt("ordered_stock")).committed_stock(resultSet.getInt("committed_stock")).build();
        }
    }

    public boolean updateProduct(ProductUpdateDTO dto) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE products SET ");
        List<Object> values = new ArrayList<>();

        if (dto.getName() != null) {
            sql.append("name = ?, ");
            values.add(dto.getName());
        }
        if (dto.getCost_price() != null) {
            sql.append("cost_price = ?, ");
            values.add(dto.getCost_price());
        }
        if (dto.getSelling_price() != null) {
            sql.append("selling_price = ?, ");
            values.add(dto.getSelling_price());
        }
        if (dto.getStock_in_hand() != null) {
            sql.append("stock_in_hand = ?, ");
            values.add(dto.getStock_in_hand());
        }
        if (dto.getCommitted_stock() != null) {
            sql.append("committed_stock = ?, ");
            values.add(dto.getCommitted_stock());
        }
        if (dto.getOrdered_stock() != null) {
            sql.append("ordered_stock = ?, ");
            values.add(dto.getOrdered_stock());
        }
        if (dto.getOpening_stock() != null) {
            sql.append("opening_stock = ?, ");
            values.add(dto.getOpening_stock());
        }

        if (values.isEmpty()) return false; // nothing to update

        sql.setLength(sql.length() - 2); // remove last comma and space
        sql.append(" WHERE id = ?");
        values.add(dto.getId());

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                preparedStatement.setObject(i + 1, values.get(i));
            }
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }
}