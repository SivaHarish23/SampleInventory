package DAO;

import DTO.BillLineItemDTO;
import DTO.ProductDTO;
import Model.BillLineItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class BillLineItemDAO {


    public BillLineItemDTO createBillItem(BillLineItem dto, Connection conn) throws SQLException {
        String sql = "INSERT INTO bill_line_items (bill_id, product_id, quantity, rate, amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dto.getBill_id());
            stmt.setInt(2, dto.getProduct_id());
            stmt.setInt(3, dto.getQuantity());
            stmt.setBigDecimal(4, dto.getRate());
            stmt.setBigDecimal(5, dto.getAmount());

            return getResultRow(conn,stmt,null);
        }
    }

    public BillLineItemDTO readBillItemById(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bill_line_items WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractBillLineItem(rs);
        }
        return null;
    }

    public List<BillLineItemDTO> readAllBillItems(Connection conn) throws SQLException {
        List<BillLineItemDTO> items = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bill_line_items")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(extractBillLineItem(rs));
            }
        }
        return items;
    }

    public List<BillLineItemDTO> getItemsByBillId(int billId, Connection conn) throws SQLException {
        List<BillLineItemDTO> items = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM bill_line_items WHERE bill_id = ?")) {
            pstmt.setInt(1, billId);
            ResultSet rss = pstmt.executeQuery();
            while (rss.next()) {
                items.add(extractBillLineItem(rss));
            }
        }
        return items;
    }

    public BillLineItemDTO updateBillItem(BillLineItemDTO dto, Connection conn) throws SQLException {
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
        int billLineItemId = Integer.parseInt(dto.getId());
        values.add(billLineItemId);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            return getResultRow(conn,stmt,billLineItemId);
        }
    }

    public boolean deleteBillItem(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM bill_line_items WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }


    private BillLineItemDTO getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM bill_line_items WHERE id = ?";

        if(preparedStatement.executeUpdate() > 0){
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return extractBillLineItem(rs);
                }
            }
        }
        return null;
    }

    private BillLineItemDTO extractBillLineItem(ResultSet rs) throws SQLException {
        BillLineItemDTO dto = new BillLineItemDTO();
        dto.setId("ITM-" + rs.getString("id"));
        dto.setBill_id("BIL-" + rs.getInt("bill_id"));
        dto.setProduct_id("PRO-" + rs.getInt("product_id"));
        dto.setQuantity(rs.getInt("quantity"));
        dto.setRate(rs.getBigDecimal("rate"));
        dto.setAmount(rs.getBigDecimal("amount"));
        return dto;
    }



    public BigDecimal getBillAmount(int billId, Connection conn) throws SQLException {
        List<BillLineItemDTO> items = getItemsByBillId(billId, conn);
        BigDecimal amount = BigDecimal.valueOf(0);
        for(BillLineItemDTO item : items){
            amount = amount.add(item.getAmount());
        }
        return amount;
    }
}