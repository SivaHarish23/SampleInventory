package DAO;

import DTO.BillLineItemDTO;
import DTO.ProductDTO;
import DTO.SalesInvoiceDTO;
import Model.SalesInvoice;
import Util.DBConnection;
import Util.TimeUtil;

import java.sql.*;
import java.util.*;

public class SalesInvoiceDAO{

    public SalesInvoiceDTO createBill(SalesInvoice dto, Connection conn) throws SQLException {
        // Omitted: insert bill and return created DTO with generated ID
        String sql = "INSERT INTO sales_invoices (invoice_date, customer_id, amount, status, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1,dto.getInvoice_date());
            stmt.setInt(2,dto.getCustomer_id());
            stmt.setBigDecimal(3, dto.getAmount());
            stmt.setInt(4, dto.getStatus());
            stmt.setString(5, dto.getNotes());
            stmt.setLong(6,dto.getCreated_at());
            stmt.setLong(7,dto.getUpdated_at());

            return getResultRow(conn,stmt,null);
        }
    }

    public SalesInvoiceDTO getBillById(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sales_invoices WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return extractSalesInvoice(rs);
        }
        return null;
    }


    public List<SalesInvoiceDTO> getAllBills(Connection conn) throws SQLException {
        List<SalesInvoiceDTO> purchaseBills = new ArrayList<>();
        String sql = "SELECT * from products";
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                purchaseBills.add(extractSalesInvoice(resultSet));
            }
        }
        return purchaseBills;
    }


    public SalesInvoiceDTO updateSalesInvoice(SalesInvoiceDTO dto, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE sales_invoices SET ");
        List<Object> values = new ArrayList<>();

        if (dto.getInvoice_date() != null) {
            sql.append("invoice_date = ?, ");
            values.add(Long.parseLong(dto.getInvoice_date()));
        }
        if (dto.getCustomer_id() != null) {
            sql.append("customer_id = ?, ");
            values.add(Integer.parseInt(dto.getCustomer_id()));
        }
        if (dto.getAmount() != null) {
            sql.append("amount = ?, ");
            values.add(dto.getAmount());
        }
        if (dto.getStatus() != null) {
            sql.append("status = ?, ");
            values.add(Integer.parseInt(dto.getStatus()));
        }
        if (dto.getNotes() != null) {
            sql.append("notes = ?, ");
            values.add(dto.getNotes());
        }

        sql.append("updated_at = ? ");
        values.add(System.currentTimeMillis());

        sql.append("WHERE id = ?");
        int salesInvoiceId = Integer.parseInt(dto.getId());
        values.add(salesInvoiceId);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.executeUpdate();
            return getResultRow(conn,stmt,salesInvoiceId);
        }
    }


    public boolean deleteSalesInvoice(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM sales_invoices WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }



    private SalesInvoiceDTO getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM sales_invoices WHERE id = ?";

        if(preparedStatement.executeUpdate() > 0){
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return extractSalesInvoice(rs);
                }
            }
        }
        return null;
    }

    private SalesInvoiceDTO extractSalesInvoice(ResultSet rs) throws SQLException {
        return  new SalesInvoiceDTO.Builder()
                .id("INV-" + rs.getInt("id"))
                .invoice_date(TimeUtil.epochToString(rs.getLong("bill_date")))
                .customer_id("CUS-" + rs.getInt("vendor_id"))
                .amount(rs.getBigDecimal("amount"))
                .status(rs.getInt("status")+"")
                .notes(rs.getString("notes"))
                .created_at(TimeUtil.epochToString(rs.getLong("created_at")))
                .updated_at(TimeUtil.epochToString(rs.getLong("updated_at")))
                .build();
    }
}