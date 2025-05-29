package DAO;

import DTO.BillItemDTO;
import Model.BillItem;
import Model.Product;
import Service.ProductService;
import Util.DBConnection;
import com.sun.org.apache.bcel.internal.generic.ARETURN;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillItemDAO {
    public boolean insertBillItems(BillItem billItem, Connection conn) throws SQLException {
        String sql = "INSERT INTO bill_items (bill_id, bill_type, product_id, quantity, rate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, billItem.getBill_id());
            preparedStatement.setString(2, billItem.getBill_type());
            preparedStatement.setInt(3, billItem.getProduct_id());
            preparedStatement.setInt(4, billItem.getQuantity());
            preparedStatement.setBigDecimal(5, billItem.getRate());

            int rowsAffected = preparedStatement.executeUpdate(); // returns number of rows inserted
            return rowsAffected > 0;
        }
    }

    // Read: Get by ID
    public BillItemDTO getBillItemById(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM bill_items WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return extractBillItemFromResultSet(rs);
            }
        }
        return null;
    }

    // Read: Get all by bill_id
    public List<BillItemDTO> getBillItemsByBillId(int billId, String billType, Connection conn) throws SQLException {
        String sql = "SELECT * FROM bill_items WHERE bill_id = ? AND bill_type = ?";
        List<BillItemDTO> items = new ArrayList<>();

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, billId);
            preparedStatement.setString(2,billType);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                items.add(extractBillItemFromResultSet(rs));
            }
        }
        return items;
    }

    // Update
    public boolean updateBillItem(BillItemDTO billItem ,Connection conn) throws SQLException {

        StringBuilder sql = new StringBuilder("UPDATE bill_items SET ");
        List<Object> values = new ArrayList<>();

        if (billItem.getBill_type() != null) {
            sql.append("bill_type = ?, ");
            values.add(billItem.getBill_type());
        }
        if (billItem.getProduct_id() != null) {
            sql.append("product_id = ?, ");
            values.add(billItem.getProduct_id());
        }
        if (billItem.getQuantity() != null) {
            sql.append("quantity = ?, ");
            values.add(billItem.getQuantity());
        }
        if (billItem.getRate() != null) {
            sql.append("rate = ?, ");
            values.add(billItem.getRate());
        }

        if (values.isEmpty()) return false; // nothing to update

        sql.setLength(sql.length() - 2); // remove last comma and space
        sql.append(" WHERE id = ?");
        values.add(billItem.getId());

        System.out.println("Updating BillItem ID: " + billItem.getId() + " with quantity: " + billItem.getQuantity());
        System.out.println(sql);
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                preparedStatement.setObject(i + 1, values.get(i));
            }

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Update result for item ID " + billItem.getId() + ": " + rowsAffected + " row(s) affected.");
            return rowsAffected > 0;
        }
    }


    // Delete by ID
    public boolean deleteBillItemById(int id) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);  // Start transaction

            try (PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM bill_items WHERE id = ?")) {
                preparedStatement.setInt(1, id);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    // Delete all items by bill_id
    public boolean deleteBillItemsByBillId(int billId, String billType) throws SQLException {
        String sql = "DELETE FROM bill_items WHERE bill_id = ? AND bill_type = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, billId);
            preparedStatement.setString(2,billType);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected);
            return rowsAffected > 0;
        }
    }

    // Utility: Extract DTO from ResultSet
    private BillItemDTO extractBillItemFromResultSet(ResultSet rs) throws SQLException {
        BillItemDTO item = new BillItemDTO.Builder()
                .id(rs.getInt("id"))
                .billId(rs.getInt("bill_id"))
                .billType(rs.getString("bill_type"))
                .productId(rs.getInt("product_id"))
                .quantity(rs.getInt("quantity"))
                .rate(rs.getBigDecimal("rate"))
                .amount(rs.getBigDecimal("amount"))
                .build();
        return item;
    }
}
