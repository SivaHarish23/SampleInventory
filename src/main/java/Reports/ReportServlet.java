package Reports;

import DAO.PurchaseBillDAO;
import DAO.SalesInvoiceDAO;
import DTO.StockAvailablityReportDTO;
import DTO.StockValueDTO;
import Model.StockAvailablityReport;
import Service.StockService;
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

    private void handleStockAvailability(String productIdParam, HttpServletResponse response) throws IOException, SQLException {
        JsonObject json = new JsonObject();
        if (productIdParam == null) {
            List<StockAvailablityReport> availabilityList = stockService.getOverallStockAvailability();
            List<StockAvailablityReportDTO> maskedList = new ArrayList<>();

            if(availabilityList != null) for(StockAvailablityReport r : availabilityList) maskedList.add(new StockAvailablityReportDTO(r));

            json.add("stock_available", gson.toJsonTree(maskedList));
        } else {
            try {
                int productId = Integer.parseInt(productIdParam.substring(4));
                StockAvailablityReport dto = stockService.getStockAvailability(productId);
                StockAvailablityReportDTO masked = new StockAvailablityReportDTO(dto);
                json.add("stock_available", gson.toJsonTree(masked));
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid product_id");
                return;
            }
        }
        json.addProperty("status", "success");
        writeResponse(response, json);
    }

    private void handleStockValue(String param, HttpServletResponse response) throws IOException, SQLException {
        JsonObject json = new JsonObject();
        if(param != null){
            try {
                int productId = Integer.parseInt(param.substring(4));
                StockValueDTO stock = stockService.getStockValue(productId);
                if(stock!=null){
                    json.addProperty("status", "success");
//                    json.addProperty("stock_value",stock.getStock_value());
                    json.add("stock_value", gson.toJsonTree(stock));
                    writeResponse(response, json);
                }
            }catch (Exception e){
                System.out.println(e);
                e.printStackTrace();
            }
        }else{
            try{

                List<StockValueDTO> stocks = stockService.getOverallStockValue();
                BigDecimal overAllStockValue = BigDecimal.valueOf(0);
                if(stocks != null){
                    for(StockValueDTO s : stocks){
                        BigDecimal amt = s.getStock_value();
                        if(amt!=null && amt.compareTo(BigDecimal.ZERO) > 0)
                            overAllStockValue = overAllStockValue.add(amt);
                    }
                }
                json.addProperty("status", "success");
                json.addProperty("overall_stock_value",overAllStockValue);
                json.add("details", gson.toJsonTree(stocks));
                writeResponse(response, json);
            }catch (Exception e){
                System.out.println(e);
                e.printStackTrace();
            }
        }

    }

    private void handlePendingReceipts(String vendorId, HttpServletResponse response) throws IOException, SQLException {
        JsonObject json = new JsonObject();
        if (vendorId == null) {
            json.add("to_be_received", gson.toJsonTree(purchaseBillDAO.getPending()));
        } else {
            try {
                int vid = Integer.parseInt(vendorId.substring(4));
                json.add("to_be_received", gson.toJsonTree(purchaseBillDAO.getPendingByVendor(vid)));
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid product_id");
                return;
            }
        }
        json.addProperty("status", "success");
        writeResponse(response, json);
    }

    private void handlePendingDeliveries(String customerIdParam, HttpServletResponse response) throws IOException, SQLException {
        JsonObject json = new JsonObject();
        if (customerIdParam == null) {
            json.add("to_be_delivered", gson.toJsonTree(salesInvoiceDAO.getPending()));
        } else {
            try {
                int cid = Integer.parseInt(customerIdParam.substring(4));
                json.add("to_be_delivered", gson.toJsonTree(salesInvoiceDAO.getPendingByCustomer(cid)));
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid product_id");
                return;
            }
        }
        json.addProperty("status", "success");
        writeResponse(response, json);
    }

    private void handleReceivedBetween(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.add("data", gson.toJsonTree(purchaseBillDAO.getProductsReceivedBetween(from, to)));
        writeResponse(response, json);
    }

    private void handleDeliveredBetween(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.add("data", gson.toJsonTree(salesInvoiceDAO.getProductsDeliveredBetween(from, to)));
        writeResponse(response, json);
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
