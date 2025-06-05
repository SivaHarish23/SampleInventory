package DAO;

import Model.Product;
import Util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public boolean isProductUsed(int productId) throws SQLException {
        String sql = "SELECT EXISTS (" +
                "SELECT 1 FROM bill_line_items WHERE product_id = ? " +
                "UNION " +
                "SELECT 1 FROM invoice_line_items WHERE product_id = ?" +
                ")";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }

    public Product insert(Product product) throws SQLException {
        Product productDTO = new Product();
        String sql = "INSERT INTO products (name, cost_price, selling_price, opening_stock, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setBigDecimal(2, product.getCost_price());
            preparedStatement.setBigDecimal(3, product.getSelling_price());
            preparedStatement.setInt(4, product.getOpening_stock());
            preparedStatement.setLong(5, Instant.now().getEpochSecond());
            preparedStatement.setLong(6, Instant.now().getEpochSecond());

            return getResultRow(conn,preparedStatement, null);
        }
    }

    public List<Product> getAllRows() throws SQLException {
        List<Product> allProducts = new ArrayList<>();
        String sql = "SELECT * from products";
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                allProducts.add(extractProduct(resultSet));
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
            return extractProduct(resultSet);
        }
    }public Product findProductByName(String name) throws SQLException {
        String sql = "SELECT * FROM products WHERE name = ?";
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return null;
            return extractProduct(resultSet);
        }
    }

    public Product updateProduct(Product dto) throws SQLException {
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
        if (dto.getOpening_stock() != null) {
            sql.append("opening_stock = ?, ");
            values.add(dto.getOpening_stock());
        }
        if (dto.getCreated_at() != null) {
            sql.append("opening_stock = ?, ");
            values.add(dto.getCreated_at());
        }

        if (values.isEmpty()) return null; // nothing to update

        sql.append("updated_at = ?, ");
        values.add(Instant.now().getEpochSecond());

        sql.setLength(sql.length() - 2); // remove last comma and space
        sql.append(" WHERE id = ?");
        int productId = dto.getId();
        values.add(productId);

        Product productDTO = new Product();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++)
                preparedStatement.setObject(i + 1, values.get(i));
            return getResultRow(conn,preparedStatement, productId);
        }
    }

    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    private Product getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM products WHERE id = ?";

        if(preparedStatement.executeUpdate() > 0){
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return extractProduct(rs);
                }
            }
        }
        return null;
    }

    // Helper: Extract PurchaseBill from ResultSet
    private Product extractProduct(ResultSet resultSet) throws SQLException {
        return new Product.Builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .cost_price(resultSet.getBigDecimal("cost_price"))
                .selling_price(resultSet.getBigDecimal("selling_price"))
                .opening_stock(resultSet.getInt("opening_stock"))
                .created_at(resultSet.getLong("created_at"))
                .updated_at(resultSet.getLong("updated_at"))
                .build();
    }

}