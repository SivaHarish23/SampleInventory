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

    public PurchaseBill createBill(PurchaseBill dto, Connection conn) throws SQLException {
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
        } catch (SQLException e) {
            System.out.println("PurchaseBillDAO : createBillItem : " + e.getMessage());
            throw e;
        }
    }

    public PurchaseBill getBillById(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM purchase_bills WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return extractPurchaseBill(rs);
        } catch (SQLException e) {
            System.out.println("PurchaseBillDAO : getBillById : " + e.getMessage());
            throw e;
        }
        return null;
    }


    public List<PurchaseBill> getAllBills(Connection conn) throws SQLException {
        List<PurchaseBill> purchaseBills = new ArrayList<>();
        String sql = "SELECT * from purchase_bills";
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                purchaseBills.add(extractPurchaseBill(resultSet));
            }
        }catch (SQLException e) {
            System.out.println("PurchaseBillDAO : getAllBills : " + e.getMessage());
            throw e;
        }
        return purchaseBills;
    }


    public PurchaseBill updatePurchaseBill(PurchaseBill dto, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE purchase_bills SET ");
        List<Object> values = new ArrayList<>();

        if (dto.getBill_date() != null) {
            sql.append("bill_date = ?, ");
            values.add(dto.getBill_date());
        }
        if (dto.getVendor_id() != null) {
            sql.append("vendor_id = ?, ");
            values.add(dto.getVendor_id());
        }
        if (dto.getAmount() != null) {
            sql.append("amount = ?, ");
            values.add(dto.getAmount());
        }
        if (dto.getStatus() != null) {
            sql.append("status = ?, ");
            values.add(dto.getStatus());
        }
        if (dto.getNotes() != null) {
            sql.append("notes = ?, ");
            values.add(dto.getNotes());
        }

        sql.append("updated_at = ? ");
        values.add(Instant.now().getEpochSecond());

        sql.append("WHERE id = ?");
        int purchaseBillId = dto.getId();
        values.add(purchaseBillId);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            return getResultRow(conn,stmt,purchaseBillId);
        }catch (SQLException e) {
            System.out.println("PurchaseBillDAO : updatePurchaseBill : " + e.getMessage());
            throw e;
        }
    }


    public boolean deletePurchaseBill(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM purchase_bills WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }


    private PurchaseBill getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM purchase_bills WHERE id = ?";

        if(preparedStatement.executeUpdate() > 0){
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            }catch (SQLException e) {
                System.out.println("PurchaseBillDAO : createBillItem : " + e.getMessage());
                throw e;
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return extractPurchaseBill(rs);
                }
            }catch (SQLException e) {
                System.out.println("PurchaseBillDAO : createBillItem : " + e.getMessage());
                throw e;
            }
        }
        return null;
    }

    private PurchaseBill extractPurchaseBill(ResultSet rs) throws SQLException {

        return  new PurchaseBill.Builder()
                .setId(rs.getInt("id"))
                .setBill_date(rs.getLong("bill_date"))
                .setVendor_id(rs.getInt("vendor_id"))
                .setAmount(rs.getBigDecimal("amount"))
                .setStatus(rs.getInt("status"))
                .setNotes(rs.getString("notes"))
                .setCreated_at(rs.getLong("created_at"))
                .setUpdated_at(rs.getLong("updated_at"))
                .build();
    }
}