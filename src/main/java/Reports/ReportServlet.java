package Reports;

import DAO.PurchaseBillDAO;
import DAO.SalesInvoiceDAO;
import DTO.StockAvailablityReportDTO;
import DTO.StockValueDTO;
import Model.StockAvailablityReport;
import Service.StockService;
import Util.PrefixValidator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/reports/*")
public class ReportServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final StockService stockService = new StockService();
    private final PurchaseBillDAO purchaseBillDAO = new PurchaseBillDAO();
    private final SalesInvoiceDAO salesInvoiceDAO = new SalesInvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            String mainPath = pathParts.length > 1 ? pathParts[1] : "";
            String param = pathParts.length > 2 ? pathParts[2] : null;

            switch (mainPath) {
                case "stockAvailability":
                    handleStockAvailability(param, response);
                    break;
                case "stockValue":
                    handleStockValue(param , response);
                    break;
                case "to-receive":
                    handlePendingReceipts(param, response);
                    break;
                case "to-deliver":
                    handlePendingDeliveries(param, response);
                    break;
                case "receivedBetween":
                    handleReceivedBetween(request, response);
                    break;
                case "deliveredBetween":
                    handleDeliveredBetween(request, response);
                    break;
                default:
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint");
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    private void handleStockAvailability(String productIdParam, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            if (productIdParam == null) {
                List<StockAvailablityReport> availabilityList = stockService.getOverallStockAvailability();
                List<StockAvailablityReportDTO> maskedList = new ArrayList<>();
                for (StockAvailablityReport r : availabilityList) maskedList.add(new StockAvailablityReportDTO(r));
                json.addProperty("status", "success");
                json.add("stock_available", gson.toJsonTree(maskedList));
            } else {
                PrefixValidator validator = new PrefixValidator();
                String error = validator.validatePrefixedId(productIdParam , PrefixValidator.EntityType.PRODUCT);
                if (error != null){
                    throw new NumberFormatException(error);
                }
                int productId = Integer.parseInt(productIdParam.substring(4));
                StockAvailablityReport dto = stockService.getStockAvailability(productId);
                StockAvailablityReportDTO masked = new StockAvailablityReportDTO(dto);
                json.addProperty("status", "success");
                json.add("stock_available", gson.toJsonTree(masked));
            }
            writeResponse(response, json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid product id format.");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
//            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
        } catch (SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }

    private void handleStockValue(String param, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            if (param != null) {
                PrefixValidator validator = new PrefixValidator();
                String error = validator.validatePrefixedId(param , PrefixValidator.EntityType.PRODUCT);
                if (error != null){
                    throw new NumberFormatException(error);
                }
                int productId = Integer.parseInt(param.substring(4));
                StockValueDTO stock = stockService.getStockValue(productId);
                json.addProperty("status", "success");
                json.add("stock_value", gson.toJsonTree(stock));
            } else {
                List<StockValueDTO> stocks = stockService.getOverallStockValue();
                BigDecimal overAllStockValue = BigDecimal.valueOf(0);
                if(stocks != null)
                    for(StockValueDTO s : stocks){
                        BigDecimal amt = s.getStock_value();
                        if(amt!=null && amt.compareTo(BigDecimal.ZERO) > 0) overAllStockValue = overAllStockValue.add(amt);
                    }
                json.addProperty("status", "success");
                json.addProperty("overall_stock_value", overAllStockValue);
                json.add("details", gson.toJsonTree(stocks));
            }
            writeResponse(response, json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid product ID format.");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
//            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
        } catch (SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }

    private void handlePendingReceipts(String vendorId, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();

        try {
            if (vendorId == null) {
                json.addProperty("status", "success");
                json.add("to_be_received", gson.toJsonTree(purchaseBillDAO.getPending()));
            } else {
                PrefixValidator validator = new PrefixValidator();
                String error = validator.validatePrefixedId(vendorId , PrefixValidator.EntityType.VENDOR);
                if (error != null){
                    throw new NumberFormatException(error);
                }
                int vid = Integer.parseInt(vendorId.substring(4)); // assumes "ven_" prefix or similar
                json.addProperty("status", "success");
                json.add("to_be_received", gson.toJsonTree(purchaseBillDAO.getPendingByVendor(vid)));
            }
            writeResponse(response, json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid vendor ID format.");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
//            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid vendor_id format");
        } catch (SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    private void handlePendingDeliveries(String customerIdParam, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            if (customerIdParam == null) {
                json.addProperty("status", "success");
                json.add("to_be_delivered", gson.toJsonTree(salesInvoiceDAO.getPending()));
            } else {
                PrefixValidator validator = new PrefixValidator();
                String error = validator.validatePrefixedId(customerIdParam , PrefixValidator.EntityType.CUSTOMER);
                if (error != null){
                    throw new NumberFormatException(error);
                }
                int cid = Integer.parseInt(customerIdParam.substring(4));
                json.addProperty("status", "success");
                json.add("to_be_delivered", gson.toJsonTree(salesInvoiceDAO.getPendingByCustomer(cid)));
            }
            writeResponse(response, json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid customer ID format.");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
//            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer_id format");
        } catch (SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

//    private void handleReceivedBetween(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String from = request.getParameter("from");
//        String to = request.getParameter("to");
//        JsonObject json = new JsonObject();
//
//        try {
//            json.addProperty("status", "success");
//            json.add("data", gson.toJsonTree(purchaseBillDAO.getProductsReceivedBetween(from, to)));
//            writeResponse(response, json);
//        } catch (SQLException e) {
//            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
//        } catch (Exception e) {
//            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
//        }
//    }
//
//    private void handleDeliveredBetween(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String from = request.getParameter("from");
//        String to = request.getParameter("to");
//        JsonObject json = new JsonObject();
//
//        try {
//            json.addProperty("status", "success");
//            json.add("data", gson.toJsonTree(salesInvoiceDAO.getProductsDeliveredBetween(from, to)));
//            writeResponse(response, json);
//        } catch (SQLException e) {
//            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
//        } catch (Exception e) {
//            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
//        }
//    }
    private void handleReceivedBetween(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        JsonObject json = new JsonObject();

        try {
            LocalDate fromDate = parseAndValidateDate(from, "from");
            LocalDate toDate = parseAndValidateDate(to, "to");

            if (fromDate.isAfter(toDate)) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "'from' date must be before 'to' date.");
                return;
            }

            json.addProperty("status", "success");
            json.add("data", gson.toJsonTree(purchaseBillDAO.getProductsReceivedBetween(from, to)));
            writeResponse(response, json);
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    private void handleDeliveredBetween(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        JsonObject json = new JsonObject();

        try {
            LocalDate fromDate = parseAndValidateDate(from, "from");
            LocalDate toDate = parseAndValidateDate(to, "to");

            if (fromDate.isAfter(toDate)) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "'from' date must be before 'to' date.");
                return;
            }

            json.addProperty("status", "success");
            json.add("data", gson.toJsonTree(salesInvoiceDAO.getProductsDeliveredBetween(from, to)));
            writeResponse(response, json);
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    private LocalDate parseAndValidateDate(String dateStr, String paramName) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required query parameter: " + paramName);
        }
        try {
            return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format for '" + paramName + "'. Expected yyyy-MM-dd.");
        }
    }

    private void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("status", "error");
        json.addProperty("message", message);
        response.setStatus(statusCode);
        writeResponse(response, json);
    }

    private void writeResponse(HttpServletResponse response, JsonObject json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(json));
            out.flush();
        }
    }
}
