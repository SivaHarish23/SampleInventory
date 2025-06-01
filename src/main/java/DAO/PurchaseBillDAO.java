package DAO;

import DTO.BillLineItemDTO;
import DTO.ProductDTO;
import DTO.PurchaseBillDTO;
import Model.PurchaseBill;
import Util.DBConnection;
import Util.TimeUtil;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class PurchaseBillDAO{

    public PurchaseBillDTO createBill(PurchaseBill dto, Connection conn) throws SQLException {
        // Omitted: insert bill and return created DTO with generated ID
        String sql = "INSERT INTO purchase_bills (bill_date, vendor_id, amount, status, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1,dto.getBill_date());
            stmt.setInt(2,dto.getVendor_id());
            stmt.setBigDecimal(3, dto.getAmount());
            stmt.setInt(4, dto.getStatus());
            stmt.setString(5, dto.getNotes());
            stmt.setLong(6, Instant.now().getEpochSecond());
            stmt.setLong(7,Instant.now().getEpochSecond());

            return getResultRow(conn,stmt,null);
        }
    }

    public PurchaseBillDTO getBillById(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM purchase_bills WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return extractPurchaseBill(rs);
        }
        return null;
    }


    public List<PurchaseBillDTO> getAllBills(Connection conn) throws SQLException {
        List<PurchaseBillDTO> purchaseBills = new ArrayList<>();
        String sql = "SELECT * from purchase_bills";
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                purchaseBills.add(extractPurchaseBill(resultSet));
            }
        }
        return purchaseBills;
    }


    public PurchaseBillDTO updatePurchaseBill(PurchaseBillDTO dto, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE purchase_bills SET ");
        List<Object> values = new ArrayList<>();

        if (dto.getBill_date() != null) {
            sql.append("bill_date = ?, ");
            values.add(Long.parseLong(dto.getBill_date()));
        }
        if (dto.getVendor_id() != null) {
            sql.append("vendor_id = ?, ");
            values.add(Integer.parseInt(dto.getVendor_id()));
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
        values.add(Instant.now().getEpochSecond());

        sql.append("WHERE id = ?");
        int purchaseBillId = Integer.parseInt(dto.getId());
        values.add(purchaseBillId);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            return getResultRow(conn,stmt,purchaseBillId);
        }
    }


    public boolean deletePurchaseBill(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM purchase_bills WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }



    private PurchaseBillDTO getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM purchase_bills WHERE id = ?";

        if(preparedStatement.executeUpdate() > 0){
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return extractPurchaseBill(rs);
                }
            }
        }
        return null;
    }

    private PurchaseBillDTO extractPurchaseBill(ResultSet rs) throws SQLException {

        return  new PurchaseBillDTO.Builder()
                .id("BIL-" + rs.getInt("id"))
                .bill_date(TimeUtil.epochToString(rs.getLong("bill_date")))
                .vendor_id("VEN-" + rs.getInt("vendor_id"))
                .amount(rs.getBigDecimal("amount"))
                .status(rs.getInt("status"))
                .notes(rs.getString("notes"))
                .created_at(TimeUtil.epochToString(rs.getLong("created_at")))
                .updated_at(TimeUtil.epochToString(rs.getLong("updated_at")))
                .build();
    }
}