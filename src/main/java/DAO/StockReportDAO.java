package DAO;

import DTO.PurchaseEntryDTO;
import Model.StockAvailablityReport;
import Util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockReportDAO {

    public List<StockAvailablityReport> getOverallStockAvailablity() throws SQLException {
        String query = "SELECT \n" +
                "    p.id AS product_id,\n" +
                "    p.name AS product_name,\n" +
                "\n" +
                "    -- Quantity Ordered (purchase bills with status = 0)\n" +
                "    COALESCE((\n" +
                "        SELECT SUM(bli.quantity)\n" +
                "        FROM bill_line_items bli\n" +
                "        JOIN purchase_bills pb ON pb.id = bli.bill_id\n" +
                "        WHERE pb.status = 0 AND bli.product_id = p.id\n" +
                "    ), 0) AS ordered_stock,\n" +
                "\n" +
                "    -- Quantity In (purchase bills with status = 1)\n" +
                "    COALESCE((\n" +
                "        SELECT SUM(bli.quantity)\n" +
                "        FROM bill_line_items bli\n" +
                "        JOIN purchase_bills pb ON pb.id = bli.bill_id\n" +
                "        WHERE pb.status = 1 AND bli.product_id = p.id\n" +
                "    ), 0) AS quantity_in,\n" +
                "\n" +
                "    -- Committed Stock (sales invoices with status = 0)\n" +
                "    COALESCE((\n" +
                "        SELECT SUM(ili.quantity)\n" +
                "        FROM invoice_line_items ili\n" +
                "        JOIN sales_invoices si ON si.id = ili.invoice_id\n" +
                "        WHERE si.status = 0 AND ili.product_id = p.id\n" +
                "    ), 0) AS committed_stock,\n" +
                "\n" +
                "    -- Quantity Out (sales invoices with status = 1)\n" +
                "    COALESCE((\n" +
                "        SELECT SUM(ili.quantity)\n" +
                "        FROM invoice_line_items ili\n" +
                "        JOIN sales_invoices si ON si.id = ili.invoice_id\n" +
                "        WHERE si.status = 1 AND ili.product_id = p.id\n" +
                "    ), 0) AS quantity_out,\n" +
                "\n" +
                "    -- Stock On Hand = opening_stock + quantity_in - quantity_out\n" +
                "    (p.opening_stock\n" +
                "        + COALESCE((\n" +
                "            SELECT SUM(bli.quantity)\n" +
                "            FROM bill_line_items bli\n" +
                "            JOIN purchase_bills pb ON pb.id = bli.bill_id\n" +
                "            WHERE pb.status = 1 AND bli.product_id = p.id\n" +
                "        ), 0)\n" +
                "        - COALESCE((\n" +
                "            SELECT SUM(ili.quantity)\n" +
                "            FROM invoice_line_items ili\n" +
                "            JOIN sales_invoices si ON si.id = ili.invoice_id\n" +
                "            WHERE si.status = 1 AND ili.product_id = p.id\n" +
                "        ), 0)\n" +
                "    ) AS stock_on_hand\n" +
                "\n" +
                "FROM products p;";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            ResultSet rs = preparedStatement.executeQuery();
            List<StockAvailablityReport> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extract(rs));
            }
            return list;

        } catch (SQLException e) {
            System.out.println("StockReportDAO : overallAvailablity : " + e.getMessage());
            throw e;
        }
    }


    public StockAvailablityReport getStockAvailablity(Integer product_id) throws SQLException {
        String query = "SELECT \n" +
                "    p.id AS product_id,\n" +
                "    p.name AS product_name,\n" +
                "\n" +
                "    -- Quantity Ordered (purchase bills with status = 0)\n" +
                "    COALESCE((\n" +
                "        SELECT SUM(bli.quantity)\n" +
                "        FROM bill_line_items bli\n" +
                "        JOIN purchase_bills pb ON pb.id = bli.bill_id\n" +
                "        WHERE pb.status = 0 AND bli.product_id = p.id\n" +
                "    ), 0) AS ordered_stock,\n" +
                "\n" +
                "    -- Quantity In (purchase bills with status = 1)\n" +
                "    COALESCE((\n" +
                "        SELECT SUM(bli.quantity)\n" +
                "        FROM bill_line_items bli\n" +
                "        JOIN purchase_bills pb ON pb.id = bli.bill_id\n" +
                "        WHERE pb.status = 1 AND bli.product_id = p.id\n" +
                "    ), 0) AS quantity_in,\n" +
                "\n" +
                "    -- Committed Stock (sales invoices with status = 0)\n" +
                "    COALESCE((\n" +
                "        SELECT SUM(ili.quantity)\n" +
                "        FROM invoice_line_items ili\n" +
                "        JOIN sales_invoices si ON si.id = ili.invoice_id\n" +
                "        WHERE si.status = 0 AND ili.product_id = p.id\n" +
                "    ), 0) AS committed_stock,\n" +
                "\n" +
                "    -- Quantity Out (sales invoices with status = 1)\n" +
                "    COALESCE((\n" +
                "        SELECT SUM(ili.quantity)\n" +
                "        FROM invoice_line_items ili\n" +
                "        JOIN sales_invoices si ON si.id = ili.invoice_id\n" +
                "        WHERE si.status = 1 AND ili.product_id = p.id\n" +
                "    ), 0) AS quantity_out,\n" +
                "\n" +
                "    -- Stock On Hand = opening_stock + quantity_in - quantity_out\n" +
                "    (p.opening_stock\n" +
                "        + COALESCE((\n" +
                "            SELECT SUM(bli.quantity)\n" +
                "            FROM bill_line_items bli\n" +
                "            JOIN purchase_bills pb ON pb.id = bli.bill_id\n" +
                "            WHERE pb.status = 1 AND bli.product_id = p.id\n" +
                "        ), 0)\n" +
                "        - COALESCE((\n" +
                "            SELECT SUM(ili.quantity)\n" +
                "            FROM invoice_line_items ili\n" +
                "            JOIN sales_invoices si ON si.id = ili.invoice_id\n" +
                "            WHERE si.status = 1 AND ili.product_id = p.id\n" +
                "        ), 0)\n" +
                "    ) AS stock_on_hand\n" +
                "\n" +
                "FROM products p\n" +
                "WHERE p.id = ?;";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query);
        ) {
            preparedStatement.setInt(1,product_id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return extract(rs);
            }
            return null;
        } catch (SQLException e) {
            System.out.println("StockReportDAO : overallAvailablity : " + e.getMessage());
            throw e;
        }
    }

    private StockAvailablityReport extract(ResultSet rs) throws SQLException {

        return new StockAvailablityReport.Builder()
                .productId(rs.getInt("product_id"))
                .productName(rs.getString("product_name"))
                .orderedStock(rs.getInt("ordered_stock"))
                .quantityIn(rs.getInt("quantity_in"))
                .quantityOut(rs.getInt("quantity_out"))
                .committedStock(rs.getInt("committed_stock"))
                .stockOnHand(rs.getInt("stock_on_hand"))
                .build();
    }




}

