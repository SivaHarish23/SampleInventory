package DAO;

import DTO.ProductTranscationDTO;
import DTO.StockReportDTO;
import DTO.pendingProductsReportDTO;
import Util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<StockReportDTO> getStockAvailability() throws SQLException {
        String query =
                "SELECT p.id, p.name, " +
                        "(p.opening_stock " +
                        "+ COALESCE(SUM(CASE WHEN bi.bill_type='PURCHASE' AND pb.status='RECEIVED' THEN bi.quantity ELSE 0 END), 0) " +
                        "- COALESCE(SUM(CASE WHEN bi.bill_type='SALES' AND sb.status='DELIVERED' THEN bi.quantity ELSE 0 END), 0) " +
                        ") AS current_stock, " +
                        "p.cost_price * ( " +
                        "(p.opening_stock " +
                        "+ COALESCE(SUM(CASE WHEN bi.bill_type='PURCHASE' AND pb.status='RECEIVED' THEN bi.quantity ELSE 0 END), 0) " +
                        "- COALESCE(SUM(CASE WHEN bi.bill_type='SALES' AND sb.status='DELIVERED' THEN bi.quantity ELSE 0 END), 0) " +
                        ") " +
                        ") AS stock_value " +
                        "FROM products p " +
                        "LEFT JOIN bill_items bi ON p.id = bi.product_id " +
                        "LEFT JOIN purchase_bills pb ON bi.bill_id = pb.id AND bi.bill_type='PURCHASE' " +
                        "LEFT JOIN sales_bills sb ON bi.bill_id = sb.id AND bi.bill_type='SALES' " +
                        "GROUP BY p.id;";

        List<StockReportDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StockReportDTO dto = new StockReportDTO();
                dto.setProductId(rs.getInt("id"));
                dto.setProductName(rs.getString("name"));
                dto.setCurrentStock(rs.getInt("current_stock"));
                dto.setStockValue(rs.getBigDecimal("stock_value"));
                list.add(dto);
            }
        }
        return list;
    }

    public BigDecimal getOverallStockValue() throws SQLException {
        String query = "SELECT \n" +
                "    SUM(stock_value) AS OverallStockValue\n" +
                "       FROM (\n" +
                "    SELECT \n" +
                "        ((p.opening_stock + \n" +
                "          IFNULL((\n" +
                "              SELECT SUM(bi.quantity)\n" +
                "              FROM bill_items bi\n" +
                "              JOIN purchase_bills pb ON pb.id = bi.bill_id\n" +
                "              WHERE bi.product_id = p.id AND bi.bill_type = 'PURCHASE' AND pb.status = 'RECEIVED'\n" +
                "          ), 0)\n" +
                "          - IFNULL((\n" +
                "              SELECT SUM(bi.quantity)\n" +
                "              FROM bill_items bi\n" +
                "              JOIN sales_bills sb ON sb.id = bi.bill_id\n" +
                "              WHERE bi.product_id = p.id AND bi.bill_type = 'SALES' AND sb.status = 'DELIVERED'\n" +
                "          ), 0)) * p.cost_price) AS stock_value\n" +
                "    FROM products p\n" +
                ") AS derived\n" +
                "WHERE stock_value > 0;";

        BigDecimal sum = null;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                sum = rs.getBigDecimal("OverallStockValue");
        }
        return sum;
    }

    public List<pendingProductsReportDTO> getProductsToReceive() throws SQLException {
        String query = "SELECT \n" +
                "p.id as product_id,\n" +
                "    p.name as product_name,\n" +
                "    SUM(bi.quantity) AS pending_quantity\n" +
                "FROM bill_items bi\n" +
                "JOIN purchase_bills pb ON pb.id = bi.bill_id\n" +
                "JOIN products p ON p.id = bi.product_id\n" +
                "WHERE bi.bill_type = 'PURCHASE' AND pb.status = 'PAID'\n" +
                "GROUP BY p.id, p.name;";

        List<pendingProductsReportDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new pendingProductsReportDTO(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("pending_quantity")));
            }
        }
        return list;

    }

    public List<pendingProductsReportDTO> getProductsToDeliver() throws SQLException{
        String query ="SELECT \n" +
                "p.id as product_id,\n" +
                "    p.name as product_name,\n" +
                "    SUM(bi.quantity) AS pending_quantity\n" +
                "FROM bill_items bi\n" +
                "JOIN sales_bills sb ON sb.id = bi.bill_id\n" +
                "JOIN products p ON p.id = bi.product_id\n" +
                "WHERE bi.bill_type = 'SALES' AND sb.status = 'PAID'\n" +
                "GROUP BY p.id, p.name;";

        List<pendingProductsReportDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new pendingProductsReportDTO(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("pending_quantity")));
            }
        }
        return list;
    }

    public List<ProductTranscationDTO> getProductsReceivedBetweenDates(Date from , Date to) throws SQLException{
        String query = "SELECT \n" +
                "                p.id AS product_id,\n" +
                "                p.name AS product_name,\n" +
                "                SUM(bi.quantity) AS quantity,\n" +
                "                pb.bill_date AS bill_date\n" +
                "            FROM bill_items bi\n" +
                "            JOIN purchase_bills pb ON pb.id = bi.bill_id\n" +
                "            JOIN products p ON p.id = bi.product_id\n" +
                "            WHERE bi.bill_type = 'PURCHASE' \n" +
                "              AND pb.status = 'RECEIVED'\n" +
                "              AND pb.bill_date BETWEEN ? AND ?\n" +
                "            GROUP BY p.id, p.name, pb.bill_date;";

        List<ProductTranscationDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection()){
             PreparedStatement ps = conn.prepareStatement(query);
             ps.setDate(1,from);
             ps.setDate(2,to);
             ResultSet rs = ps.executeQuery() ;
            while (rs.next()) {
                list.add(new ProductTranscationDTO(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("quantity"),rs.getDate("bill_date")));
            }
        }
        return list;

    }

    public List<ProductTranscationDTO> getProductsDeliveredBetweenDates(Date from , Date to) throws SQLException{
        String query = "SELECT \n" +
                "                p.id AS product_id,\n" +
                "                p.name AS product_name,\n" +
                "                SUM(bi.quantity) AS quantity,\n" +
                "                sb.bill_date AS bill_date\n" +
                "            FROM bill_items bi\n" +
                "            JOIN sales_bills sb ON sb.id = bi.bill_id\n" +
                "            JOIN products p ON p.id = bi.product_id\n" +
                "            WHERE bi.bill_type = 'SALES' \n" +
                "              AND sb.status = 'DELIVERED'\n" +
                "              AND sb.bill_date BETWEEN ? AND ?\n" +
                "            GROUP BY p.id, p.name, sb.bill_date;";

        List<ProductTranscationDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDate(1,from);
            ps.setDate(2,to);
            ResultSet rs = ps.executeQuery() ;
            while (rs.next()) {
                list.add(new ProductTranscationDTO(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("quantity"),rs.getDate("bill_date")));
            }
        }
        return list;

    }
}

