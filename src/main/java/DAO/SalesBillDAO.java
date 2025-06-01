//package DAO;
//
//import DTO.SalesBillDTO;
//import Model.SalesInvoice;
//import Util.DBConnection;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SalesBillDAO {
//    public int createSalesBill(SalesInvoice bill, Connection conn) throws SQLException {
//        String sql = "INSERT INTO sales_bills (bill_date, customer_id, amount, status) VALUES (?, ?, ?, ?)";
//        try (PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            preparedStatement.setString(1, bill.getBill_date() + "");
//            preparedStatement.setInt(2, bill.getCustomer_id());
//            preparedStatement.setBigDecimal(3, bill.getAmount());
//            preparedStatement.setString(4, bill.getStatus());
//
//            int rowsAffected = preparedStatement.executeUpdate(); // returns number of rows inserted
//            if (rowsAffected > 0) {
//                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
//                    if (rs.next()) {
//                        return rs.getInt(1); // the generated id
//                    } else
//                        throw new SQLException("Inserting sales bill failed, no ID obtained.");
//                }
//            } else {
//                throw new SQLException("Inserting sales bill failed, no rows affected.");
//            }
//
//        }
//    }
//
//    // READ: By ID
//    public SalesBillDTO getSalesBillById(int id) throws SQLException {
//        String sql = "SELECT * FROM sales_bills WHERE id = ?";
//        try (Connection conn = DBConnection.getInstance().getConnection();
//             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
//
//            preparedStatement.setInt(1, id);
//            ResultSet rs = preparedStatement.executeQuery();
//
//            if (rs.next()) {
//                return extractSalesBill(rs);
//            }
//        }
//        return null;
//    }
//
//    // READ: All bills
//    public List<SalesBillDTO> getAllSalesBills() throws SQLException {
//        String sql = "SELECT * FROM sales_bills ORDER BY id DESC";
//        List<SalesBillDTO> bills = new ArrayList<>();
//
//        try (Connection conn = DBConnection.getInstance().getConnection();
//             PreparedStatement preparedStatement = conn.prepareStatement(sql);
//             ResultSet rs = preparedStatement.executeQuery()) {
//
//            while (rs.next()) {
//                bills.add(extractSalesBill(rs));
//            }
//        }
//
//        return bills;
//    }
//
//    // UPDATE
//    public boolean updateSalesBill(SalesBillDTO bill, Connection conn) throws SQLException {
//        StringBuilder sql = new StringBuilder("UPDATE sales_bills SET ");
//        List<Object> values = new ArrayList<>();
//
//        if (bill.getBill_date() != null) {
//            sql.append("bill_date = ?, ");
//            values.add(bill.getBill_date());
//        }
//        if (bill.getCustomer_id() != null) {
//            sql.append("customer_id = ?, ");
//            values.add(bill.getCustomer_id());
//        }
//        if (bill.getAmount() != null) {
//            sql.append("amount = ?, ");
//            values.add(bill.getAmount());
//        }
//        if (bill.getStatus() != null) {
//            sql.append("status = ?, ");
//            values.add(bill.getStatus());
//        }
//
//        if (values.isEmpty()) return false; // nothing to update
//
//        sql.setLength(sql.length() - 2); // remove last comma and space
//        sql.append(" WHERE id = ?");
//        values.add(bill.getId());
//
//        try (PreparedStatement preparedStatement = conn.prepareStatement(sql.toString())) {
//            for (int i = 0; i < values.size(); i++) {
//                preparedStatement.setObject(i + 1, values.get(i));
//            }
//            return preparedStatement.executeUpdate() > 0;
//        }
//    }
//
//    // DELETE
//    public boolean deleteSalesBillById(int id) throws SQLException {
//        String sql = "DELETE FROM sales_bills WHERE id = ?";
//        try (Connection conn = DBConnection.getInstance().getConnection();
//             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
//
//            preparedStatement.setInt(1, id);
//            int rowsAffected = preparedStatement.executeUpdate();
//            return rowsAffected > 0;
//        }
//    }
//
//    // Helper: Extract SalesBill from ResultSet
//    private SalesBillDTO extractSalesBill(ResultSet rs) throws SQLException {
//        return new SalesBillDTO.Builder()
//                .id(rs.getInt("id"))
//                .billDate(String.valueOf(rs.getDate("bill_date")))
//                .customerId(rs.getInt("customer_id"))
//                .amount(rs.getBigDecimal("amount"))
//                .status(rs.getString("status"))
//                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
//                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
//                .build();
//    }
//
//}