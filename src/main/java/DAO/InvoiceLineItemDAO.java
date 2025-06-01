package DAO;

import DTO.InvoiceLineItemDTO;
import DTO.ProductDTO;
import Model.InvoiceLineItem;

import java.sql.*;
import java.util.*;

public class InvoiceLineItemDAO {

    public InvoiceLineItemDTO createBillItem(InvoiceLineItem dto, Connection conn) throws SQLException {
        String sql = "INSERT INTO invoice_line_items (invoice_id, product_id, quantity, rate, amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dto.getInvoice_id());
            stmt.setInt(2, dto.getProduct_id());
            stmt.setInt(3, dto.getQuantity());
            stmt.setBigDecimal(4, dto.getRate());
            stmt.setBigDecimal(5, dto.getAmount());

            return getResultRow(conn,stmt,null);
        }
    }

    public InvoiceLineItemDTO readBillItemById(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM invoice_line_items WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) extractInvoiceLineItem(rs);
        }
        return null;
    }

    public List<InvoiceLineItemDTO> readAllBillItems(Connection conn) throws SQLException {
        List<InvoiceLineItemDTO> items = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM invoice_line_items")) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                items.add(extractInvoiceLineItem(rs));

        }
        return items;
    }

    public List<InvoiceLineItemDTO> getItemsByBillId(int billId, Connection conn) throws SQLException {
        List<InvoiceLineItemDTO> items = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM invoice_line_items WHERE invoice_id = ?")) {
            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                items.add(extractInvoiceLineItem(rs));

        }
        return items;
    }

    public InvoiceLineItemDTO updateBillItem(InvoiceLineItemDTO dto, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE invoice_line_items SET ");
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
        int invoiceLineItemId = Integer.parseInt(dto.getId());
        values.add(invoiceLineItemId);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            return getResultRow(conn,stmt,invoiceLineItemId);
        }
    }

    public boolean deleteBillItem(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM invoice_line_items WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }


    private InvoiceLineItemDTO getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM invoice_line_items WHERE id = ?";

        if(preparedStatement.executeUpdate() > 0){
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return extractInvoiceLineItem(rs);
                }
            }
        }
        return null;
    }


    private InvoiceLineItemDTO extractInvoiceLineItem(ResultSet rs) throws SQLException {
        return new InvoiceLineItemDTO.Builder()
                .id(rs.getString("id"))
                .invoice_id("BIL-" + rs.getInt("invoice_id"))
                .product_id("PRO-" + rs.getInt("product_id"))
                .quantity(rs.getInt("quantity"))
                .rate(rs.getBigDecimal("rate"))
                .amount(rs.getBigDecimal("amount"))
                .build();
    }
}