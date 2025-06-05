package DAO;

import DTO.*;
import Model.PurchaseBill;
import Util.DBConnection;
import Util.TimeUtil;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class PurchaseBillDAO{

    public PurchaseBill createBill(PurchaseBill dto, Connection conn) throws SQLException {

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
                System.out.println("PurchaseBillDAO : getGeneratedKey : " + e.getMessage());
                throw e;
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return extractPurchaseBill(rs);
                }
            }catch (SQLException e) {
                System.out.println("PurchaseBillDAO : extractBill : " + e.getMessage());
                throw e;
            }
        }
        return null;
    }

    public List<PendingQuantityDTO> getPending() throws SQLException{
        String query = "SELECT \n" +
                "    v.id AS vendor_id, \n" +
                "    v.name AS vendor_name, \n" +
                "    p.id AS product_id,\n" +
                "    p.name AS product_name, \n" +
                "    SUM(bli.quantity) AS quantity_pending\n" +
                "FROM purchase_bills pb\n" +
                "JOIN vendors v ON pb.vendor_id = v.id\n" +
                "JOIN bill_line_items bli ON pb.id = bli.bill_id\n" +
                "JOIN products p ON bli.product_id = p.id\n" +
                "WHERE pb.status = 0 \n" +
                "GROUP BY v.id, p.id;";
        try(Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);){
            ResultSet rs = stmt.executeQuery();
            List<PendingQuantityDTO> list = new ArrayList<>();
            while (rs.next()){
                PendingQuantityDTO p = new PendingQuantityDTO(rs.getString("vendor_name"),  null,
                        rs.getString("product_name"),
                        rs.getInt("quantity_pending"));
                p.setVendor_id(rs.getInt("vendor_id")+"");
                p.setProduct_id(rs.getInt("product_id")+"");
                list.add(p);
            }
            return list;
        }catch (Exception e){
            System.out.println("getPendingProductsByCustomer : " + e);
            throw e;
        }
    }

    public PendingQuantityDTO getPendingByVendor(int vendor_id) throws SQLException {
        String query = "SELECT \n" +
                "    p.id AS product_id,\n" +
                "    p.name AS product_name, \n" +
                "    SUM(bli.quantity) AS quantity_pending\n" +
                "FROM purchase_bills pb\n" +
                "JOIN vendors v ON pb.vendor_id = v.id\n" +
                "JOIN bill_line_items bli ON pb.id = bli.bill_id\n" +
                "JOIN products p ON bli.product_id = p.id\n" +
                "WHERE pb.status = 0 AND v.id = ?\n" +
                "GROUP BY v.id, p.id;";
        try(Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);){
            stmt.setInt(1, vendor_id);
            ResultSet rs = stmt.executeQuery();
            PendingQuantityDTO list = new PendingQuantityDTO();
            if (rs.next()){
                PendingQuantityDTO p = new PendingQuantityDTO(null, null,
                        rs.getString("product_name"),
                        rs.getInt("quantity_pending"));
                p.setProduct_id(rs.getInt("product_id")+"");
                return p;
            }
            return list;
        }catch (Exception e){
            System.out.println("getPendingProductsByCustomer : " + e);
            throw e;
        }
    }


    public List<ProductTranscationDTO> getProductsReceivedBetween(String from, String to) throws SQLException {
        Long fromL = TimeUtil.stringToEpoch(from);
        Long toL = TimeUtil.stringToEpoch(to);
        String query = "SELECT \n" +
                "    p.id AS product_id,\n" +
                "    p.name AS product_name,\n" +
                "    bli.quantity AS quantity,\n" +
                "    pb.bill_date AS bill_date\n" +
                "FROM purchase_bills pb\n" +
                "JOIN bill_line_items bli ON pb.id = bli.bill_id\n" +
                "JOIN products p ON p.id = bli.product_id\n" +
                "WHERE pb.status = 1  \n" +
                "  AND pb.bill_date BETWEEN ? AND ?\n" +
                "ORDER BY pb.bill_date ASC;";
        try(Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1,fromL);
            stmt.setLong(2,toL);
            ResultSet rs = stmt.executeQuery();
            List<ProductTranscationDTO> list = new ArrayList<>();
            while(rs.next()){
                ProductTranscationDTO product = new ProductTranscationDTO();
                product.setProduct_id(rs.getInt("product_id")+"");
                product.setProduct_name(rs.getString("product_name"));
                product.setQuantity(rs.getInt("quantity"));
                product.setBill_date(TimeUtil.epochToString(rs.getLong("bill_date")));

                list.add(product);
            }
            return list;
        }catch (Exception e){
            System.out.println("getProductsReceivedBetween : " + e);
            throw e;
        }
    }

    public List<PurchaseEntryDTO> getPurchaseEntryById(Integer pid) throws SQLException {
        String query = "SELECT \n" +
                "    bli.quantity, \n" +
                "    bli.rate \n" +
                "FROM bill_line_items bli\n" +
                "JOIN purchase_bills pb ON pb.id = bli.bill_id\n" +
                "WHERE pb.status = 1\n" +
                "  AND bli.product_id = ?\n" +
                "ORDER BY pb.bill_date ASC;";
        try(Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(query)){
            List<PurchaseEntryDTO> list = new ArrayList<>();
            ps.setInt(1,pid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                list.add(new PurchaseEntryDTO(rs.getInt("quantity"),rs.getBigDecimal("rate")));
            }
            return list;
        }catch (Exception e){
            System.out.println("getPurchaseEntryById : " + e);
            throw e;
        }
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