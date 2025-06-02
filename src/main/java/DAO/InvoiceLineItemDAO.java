package DAO;

import Model.InvoiceLineItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class InvoiceLineItemDAO {

    public InvoiceLineItem createInvoiceItem(InvoiceLineItem item, Connection conn) throws SQLException {
        String sql = "INSERT INTO invoice_line_items (invoice_id, product_id, quantity, rate, amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getInvoice_id());
            stmt.setInt(2, item.getProduct_id());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getRate());
            stmt.setBigDecimal(5, item.getAmount());
            try {
                return getResultRow(conn, stmt, null);
            } catch (SQLException e) {
                System.out.println("InvoiceLineItemDAO : createInvoiceItem : " + e.getMessage());
                throw e;
            }
        }
    }

    public InvoiceLineItem readInvoiceItemById(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM invoice_line_items WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) return extractInvoiceLineItem(rs);
            } catch (SQLException e) {
                System.out.println("InvoiceLineItemDAO : readInvoiceItemById : " + e.getMessage());
                throw e;
            }
        }
        return null;
    }

    public List<InvoiceLineItem> readAllInvoiceItems(Connection conn) throws SQLException {
        List<InvoiceLineItem> items = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM invoice_line_items")) {
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    items.add(extractInvoiceLineItem(rs));
                }
            } catch (SQLException e) {
                System.out.println("InvoiceLineItemDAO : readAllInvoiceItems : " + e.getMessage());
                throw e;
            }
        }
        return items;
    }

    public List<InvoiceLineItem> getItemsByInvoiceId(int invoiceId, Connection conn) throws SQLException {
        List<InvoiceLineItem> items = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM invoice_line_items WHERE invoice_id = ?")) {
            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                items.add(extractInvoiceLineItem(rs));
        }catch (SQLException e) {
            System.out.println("InvoiceLineItemDAO : getItemsByInvoiceId : " + e.getMessage());
            throw e;
        }
        return items;
    }

    public InvoiceLineItem updateInvoiceItem(InvoiceLineItem dto, Connection conn) throws SQLException {
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
        int invoiceLineItemId = dto.getId();
        values.add(invoiceLineItemId);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            return getResultRow(conn,stmt,invoiceLineItemId);
        }catch (SQLException e) {
            System.out.println("InvoiceLineItemDAO : updateInvoiceItem : " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteInvoiceItem(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM invoice_line_items WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }


    private InvoiceLineItem getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
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


    private InvoiceLineItem extractInvoiceLineItem(ResultSet rs) throws SQLException {
        return new InvoiceLineItem.Builder()
                .setId(rs.getInt("id"))
                .setInvoice_id(rs.getInt("invoice_id"))
                .setProduct_id(rs.getInt("product_id"))
                .setQuantity(rs.getInt("quantity"))
                .setRate(rs.getBigDecimal("rate"))
                .setAmount(rs.getBigDecimal("amount"))
                .build();
    }

    public BigDecimal getInvoiceAmount(int invoiceId, Connection conn) throws SQLException {
        BigDecimal amount = BigDecimal.valueOf(0);
        try {
            List<InvoiceLineItem> items = getItemsByInvoiceId(invoiceId, conn);
            for (InvoiceLineItem item : items) amount = amount.add(item.getAmount());
        } catch (SQLException e) {
            System.out.println("InvoiceLineItemDAO : getInvoiceAmount : " + e.getMessage());
            throw e;
        }
        return amount;
    }
}