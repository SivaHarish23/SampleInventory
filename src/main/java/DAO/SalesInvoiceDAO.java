package DAO;

import DTO.PendingQuantityDTO;
import DTO.ProductTranscationDTO;
import Model.SalesInvoice;
import Util.DBConnection;
import Util.TimeUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class SalesInvoiceDAO{

    public SalesInvoice createInvoice(SalesInvoice dto, Connection conn) throws SQLException {
        // Omitted: insert invoice and return created DTO with generated ID
        String sql = "INSERT INTO sales_invoices (invoice_date, customer_id, amount, status, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1,dto.getInvoice_date());
            stmt.setInt(2,dto.getCustomer_id());
            stmt.setBigDecimal(3, dto.getAmount());
            stmt.setInt(4, dto.getStatus());
            stmt.setString(5, dto.getNotes());
            stmt.setLong(6, Instant.now().getEpochSecond());
            stmt.setLong(7,Instant.now().getEpochSecond());

            return getResultRow(conn,stmt,null);
        }catch (SQLException e) {
            System.out.println("SalesInvoiceDAO : createInvoiceItem : " + e.getMessage());
            throw e;
        }
    }

    public SalesInvoice getInvoiceById(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sales_invoices WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return extractSalesInvoice(rs);
        }catch (SQLException e) {
            System.out.println("SalesInvoiceDAO : getInvoiceById : " + e.getMessage());
            throw e;
        }
        return null;
    }


    public List<SalesInvoice> getAllInvoices(Connection conn) throws SQLException {
        List<SalesInvoice> salesInvoices = new ArrayList<>();
        String sql = "SELECT * from sales_invoices";
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                salesInvoices.add(extractSalesInvoice(resultSet));
            }
        }catch (SQLException e) {
            System.out.println("SalesInvoiceDAO : getAllInvoices : " + e.getMessage());
            throw e;
        }
        return salesInvoices;
    }


    public SalesInvoice updateSalesInvoice(SalesInvoice dto, Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE sales_invoices SET ");
        List<Object> values = new ArrayList<>();

        if (dto.getInvoice_date() != null) {
            sql.append("invoice_date = ?, ");
            values.add(dto.getInvoice_date());
        }
        if (dto.getCustomer_id() != null) {
            sql.append("customer_id = ?, ");
            values.add(dto.getCustomer_id());
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
        int salesInvoiceId = dto.getId();
        values.add(salesInvoiceId);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.executeUpdate();
            return getResultRow(conn,stmt,salesInvoiceId);
        }catch (SQLException e) {
            System.out.println("SalesInvoiceDAO : updateSalesInvoice : " + e.getMessage());
            throw e;
        }
    }


    public boolean deleteSalesInvoice(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM sales_invoices WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }



    private SalesInvoice getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM sales_invoices WHERE id = ?";

        if(preparedStatement.executeUpdate() > 0){
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            }catch (SQLException e) {
                System.out.println("SalesInvoiceDAO : getGeneratedKey : " + e.getMessage());
                throw e;
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return extractSalesInvoice(rs);
                }
            }catch (SQLException e) {
                System.out.println("SalesInvoiceDAO : extract invoice : " + e.getMessage());
                throw e;
            }
        }
        return null;
    }

    public List<PendingQuantityDTO> getPending() throws SQLException{
        String query = "SELECT \n" +
                "    c.name AS customer_name, \n" +
                "    p.name AS product_name, \n" +
                "    SUM(ili.quantity) AS quantity_pending\n" +
                "FROM sales_invoices si\n" +
                "JOIN customers c ON si.customer_id = c.id\n" +
                "JOIN invoice_line_items ili ON si.id = ili.invoice_id\n" +
                "JOIN products p ON ili.product_id = p.id\n" +
                "WHERE si.status = 0\n" +
                "GROUP BY c.name, p.name;";
        try(Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);){
            ResultSet rs = stmt.executeQuery();
            List<PendingQuantityDTO> list = new ArrayList<>();
            while (rs.next()){
                list.add(new PendingQuantityDTO(null,rs.getString("customer_name"),
                        rs.getString("product_name"),
                        rs.getInt("quantity_pending")));
            }
            return list;
        }catch (Exception e){
            System.out.println("getPendingProductsByCustomer : " + e);
            throw e;
        }
    }

    public PendingQuantityDTO getPendingByCustomer(int customer_id) throws SQLException {
        String query = "SELECT \n" +
                "    p.name AS product_name, \n" +
                "    SUM(ili.quantity) AS quantity_pending\n" +
                "FROM sales_invoices si\n" +
                "JOIN customers c ON si.customer_id = c.id\n" +
                "JOIN invoice_line_items ili ON si.id = ili.invoice_id\n" +
                "JOIN products p ON ili.product_id = p.id\n" +
                "WHERE si.status = 0 AND c.id = ?\n" +
                "GROUP BY c.name, p.name;";
        try(Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);){
            stmt.setInt(1, customer_id);
            ResultSet rs = stmt.executeQuery();
            PendingQuantityDTO list = new PendingQuantityDTO();
            if (rs.next()){
                list = new PendingQuantityDTO(null,null,
                        rs.getString("product_name"),
                        rs.getInt("quantity_pending"));
            }
            return list;
        }catch (Exception e){
            System.out.println("getPendingProductsByCustomer : " + e);
            throw e;
        }
    }

    public List<ProductTranscationDTO> getProductsDeliveredBetween(String from, String to) throws SQLException {
        Long fromL = TimeUtil.stringToEpoch(from);
        Long toL = TimeUtil.stringToEpoch(to);
        String query = "SELECT \n" +
                "    p.id AS product_id,\n" +
                "    p.name AS product_name,\n" +
                "    ili.quantity AS quantity,\n" +
                "    si.invoice_date AS bill_date\n" +
                "FROM sales_invoices si\n" +
                "JOIN invoice_line_items ili ON si.id = ili.invoice_id\n" +
                "JOIN products p ON p.id = ili.product_id\n" +
                "WHERE si.status = 1  \n" +
                "  AND si.invoice_date BETWEEN ? AND ?\n" +
                "ORDER BY si.invoice_date ASC, p.name ASC;";
        try(Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1,fromL);
            stmt.setLong(2,toL);
            ResultSet rs = stmt.executeQuery();
            List<ProductTranscationDTO> list = new ArrayList<>();
            while(rs.next()){
                ProductTranscationDTO product = new ProductTranscationDTO();
                product.setProduct_id(null);
                product.setProduct_name(rs.getString("product_name"));
                product.setQuantity(rs.getInt("quantity"));
                product.setBill_date(TimeUtil.epochToString(rs.getLong("bill_date")));

                list.add(product);
            }
            return list;
        }catch (Exception e){
            System.out.println("getProductsDeliveredBetween : " + e);
            throw e;
        }
    }

    public int totalQuantitySold(int productId) throws SQLException {
        String query = "SELECT \n" +
                "    SUM(ili.quantity) AS total_sold \n" +
                "FROM invoice_line_items ili\n" +
                "JOIN sales_invoices si ON si.id = ili.invoice_id\n" +
                "WHERE si.status = 1\n" +
                "  AND ili.product_id = ?;";
        try(Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1,productId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt("total_sold");
        }catch (Exception e){
            System.out.println("totalQuantitySold : " + e);
            throw e;
        }
        return 0;
    }

    private SalesInvoice extractSalesInvoice(ResultSet rs) throws SQLException {
        return  new SalesInvoice.Builder()
                .setId(rs.getInt("id"))
                .setInvoice_date(rs.getLong("invoice_date"))
                .setCustomer_id(rs.getInt("customer_id"))
                .setAmount(rs.getBigDecimal("amount"))
                .setStatus(rs.getInt("status"))
                .setNotes(rs.getString("notes"))
                .setCreated_at(rs.getLong("created_at"))
                .setUpdated_at(rs.getLong("updated_at"))
                .build();
    }



}