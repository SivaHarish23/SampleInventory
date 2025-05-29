package Servlet;

import DAO.BillItemDAO;
import DAO.SalesBillDAO;
import DTO.ProductUpdateDTO;
import DTO.SalesBillDTO;
import Service.BillingService;
import Service.BillingServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/salesBill/*")
public class SalesBillServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final BillingService billingService = new BillingServiceImpl(null,new SalesBillDAO(), new BillItemDAO());


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            return;
        }

        try {
            SalesBillDTO salesBillDTO = gson.fromJson(request.getReader(), SalesBillDTO.class);
            int salesBillId = billingService.createSalesBill(salesBillDTO);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Sales bill created successfully.");
            jsonResponse.addProperty("bill_id", salesBillId);
            response.setStatus(HttpServletResponse.SC_CREATED);
            writeResponse(response, jsonResponse);
        } catch (SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid input: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                List<SalesBillDTO> allBills = billingService.getAllSalesBills();
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", "success");
                jsonResponse.add("bills", gson.toJsonTree(allBills));
                response.setStatus(HttpServletResponse.SC_OK);
                writeResponse(response, jsonResponse);
            } catch (Exception e) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving sales bills: " + e.getMessage());
            }
        } else {
            String idStr = pathInfo.substring(1);
            try {
                int id = Integer.parseInt(idStr);
                SalesBillDTO bill = billingService.getSalesBill(id);
                if (bill != null) {
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.add("bill", gson.toJsonTree(bill));
                    response.setStatus(HttpServletResponse.SC_OK);
                    writeResponse(response, jsonResponse);
                } else {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Sales bill not found for ID: " + id);
                }
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid sales bill ID format");
            } catch (Exception e) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving sales bill: " + e.getMessage());
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Sales bill ID is missing in URL");
            return;
        }

        String idStr = pathInfo.substring(1);
        try {
            int id = Integer.parseInt(idStr);
            SalesBillDTO dto = gson.fromJson(request.getReader(), SalesBillDTO.class);
            dto.setId(id);

            if (billingService.updateSalesBill(dto)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Sales bill updated successfully");
                response.setStatus(HttpServletResponse.SC_OK);
                writeResponse(response, jsonResponse);
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Sales bill not found or no fields to update");
            }
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid sales bill ID format");
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to process update: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Endpoint is missing in URL");
            return;
        }

        String[] pathParts = pathInfo.substring(1).split("/");
        if (pathParts.length == 1) {
            try {
                int id = Integer.parseInt(pathParts[0]);
                if (billingService.deleteSalesBill(id)) {
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Sales bill deleted successfully");
                    response.setStatus(HttpServletResponse.SC_OK);
                    writeResponse(response, jsonResponse);
                } else {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Sales bill with given ID not found");
                }
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid sales bill ID format");
            } catch (Exception e) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to process deletion: " + e.getMessage());
            }
        } else if (pathParts.length == 2 && pathParts[0].equals("item")) {
            try {
                int billItemId = Integer.parseInt(pathParts[1]);
                if (billingService.deleteBillItemForBill(billItemId)) {
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Sales bill item deleted successfully");
                    response.setStatus(HttpServletResponse.SC_OK);
                    writeResponse(response, jsonResponse);
                } else {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Sales bill item with given ID not found");
                }
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid bill item ID format");
            } catch (Exception e) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to process deletion: " + e.getMessage());
            }
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
        }
    }

    private void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("status", "error");
        errorJson.addProperty("message", message);
        writeResponse(response, errorJson);
    }

    private void writeResponse(HttpServletResponse response, JsonObject json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(json));
            out.flush();
        }
    }
}
