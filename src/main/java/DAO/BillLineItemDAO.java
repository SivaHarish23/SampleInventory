package DAO;

import Model.BillLineItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class BillLineItemDAO {


    public BillLineItem createBillItem(BillLineItem dto, Connection conn) throws SQLException {
        String sql = "INSERT INTO bill_line_items (bill_id, product_id, quantity, rate, amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dto.getBill_id());
            stmt.setInt(2, dto.getProduct_id());
            stmt.setInt(3, dto.getQuantity());
            stmt.setBigDecimal(4, dto.getRate());
            stmt.setBigDecimal(5, dto.getAmount());
            try {
                return getResultRow(conn, stmt, null);
            } catch (SQLException e) {
                System.out.println("BillLineItemDAO : createBillItem : " + e.getMessage());
                throw e;
            }
        }
    }

    public BillLineItem readBillItemById(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bill_line_items WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) return extractBillLineItem(rs);
            } catch (SQLException e) {
                System.out.println("BillLineItemDAO : readBillItemById : " + e.getMessage());
                throw e;
            }
        }
        return null;
    }

    public List<BillLineItem> readAllBillItems(Connection conn) throws SQLException {
        List<BillLineItem> items = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bill_line_items")) {
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    items.add(extractBillLineItem(rs));
                }
            } catch (SQLException e) {
                System.out.println("BillLineItemDAO : readAllBillItems : " + e.getMessage());
                throw e;
            }
        }
        return items;
    }

    public List<BillLineItem> getItemsByBillId(int billId, Connection conn) throws SQLException {
        List<BillLineItem> items = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM bill_line_items WHERE bill_id = ?")) {
            pstmt.setInt(1, billId);
            ResultSet rss = pstmt.executeQuery();
            while (rss.next()) {
                items.add(extractBillLineItem(rss));
            }
        } catch (SQLException e) {
            System.out.println("BillLineItemDAO : getItemsByBillId : " + e.getMessage());
            throw e;
        }
        return items;
    }

    public BillLineItem updateBillItem(BillLineItem dto, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE bill_line_items SET ");
        List<Object> values = new ArrayList<>();

        if (dto.getProduct_id() != null) {
            sql.append("product_id = ?, ");
            values.add(dto.getProduct_id());
        }
        if (dto.getQuantity() != null) {
            sql.append("quantity = ?, ");
            values.add(dto.getQuantity());
        }
        if (dto.getRate() != null) {
            sql.append("rate = ?, ");
            values.add(dto.getRate());
        }
        if (dto.getAmount() != null) {
            sql.append("amount = ?, ");
            values.add(dto.getAmount());
        }

        sql.setLength(sql.length() - 2); // remove last comma
        sql.append(" WHERE id = ?");
        int billLineItemId = dto.getId();
        values.add(billLineItemId);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            return getResultRow(conn, stmt, billLineItemId);
        } catch (SQLException e) {
            System.out.println("BillLineItemDAO : updateBillItem : " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteBillItem(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM bill_line_items WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("BillLineItemDAO : deleteBillItem : " + e.getMessage());
            throw e;
        }
    }


    private BillLineItem getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM bill_line_items WHERE id = ?";

        if (preparedStatement.executeUpdate() > 0) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            } catch (SQLException e) {
                System.out.println("BillLineItemDAO : getResultRow : error in fetching generated key : " + e.getMessage());
                throw e;
            }

            try (PreparedStatement ps = conn.prepareStatement(fetch)) {
                ps.setInt(1, pid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return extractBillLineItem(rs);
                }
            } catch (SQLException e) {
                System.out.println("BillLineItemDAO : getResultRow : error fetching row of generated key : " + e.getMessage());
                throw e;
            }
        }
        return null;
    }

    private BillLineItem extractBillLineItem(ResultSet rs) throws SQLException {
        BillLineItem dto = new BillLineItem();
        dto.setId(rs.getInt("id"));
        dto.setBill_id(rs.getInt("bill_id"));
        dto.setProduct_id(rs.getInt("product_id"));
        dto.setQuantity(rs.getInt("quantity"));
        dto.setRate(rs.getBigDecimal("rate"));
        dto.setAmount(rs.getBigDecimal("amount"));
        return dto;
    }


    public BigDecimal getBillAmount(int billId, Connection conn) throws SQLException {
        BigDecimal amount = BigDecimal.valueOf(0);
        try {
            List<BillLineItem> items = getItemsByBillId(billId, conn);
            for (BillLineItem item : items) amount = amount.add(item.getAmount());
        } catch (SQLException e) {
            System.out.println("BillLineItemDAO : getBillAmount : " + e.getMessage());
            throw e;
        }
        return amount;
    }
}