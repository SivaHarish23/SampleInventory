package Servlet;

import DTO.PurchaseBillDTO;
import Service.PurchaseBillService;
import Service.PurchaseBillServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/purchaseBill/*")
public class PurchaseBillServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final PurchaseBillServiceImpl purchaseBillService = new PurchaseBillServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleGetAllBills(response);
        } else {
            handleGetBillById(pathInfo.substring(1), response);
        }
    }

    private void handleGetAllBills(HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            List<PurchaseBillDTO> allBills = purchaseBillService.getAllPurchaseBills();
            json.addProperty("status", "success");
            json.addProperty("message", "Purchase bills retrieved successfully.");
            json.add("bills", gson.toJsonTree(allBills));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving purchase bills.");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    private void handleGetBillById(String idStr, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            PurchaseBillDTO bill = purchaseBillService.getPurchaseBillById(idStr);
            if (bill != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Purchase bill found.");
                json.add("bill", gson.toJsonTree(bill));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Purchase bill not found for ID: " + idStr);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid bill ID format.");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving purchase bill.");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            PurchaseBillDTO dto = gson.fromJson(request.getReader(), PurchaseBillDTO.class);
            System.out.println(dto.toString());
            dto = purchaseBillService.createPurchaseBill(dto);
            response.setStatus(HttpServletResponse.SC_CREATED);
            json.addProperty("status", "success");
            json.addProperty("message", "Purchase bill created successfully.");
            json.add("bill", gson.toJsonTree(dto));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.addProperty("status", "error");
            json.addProperty("message", "Database error");
            json.addProperty("error", e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid input");
            json.addProperty("error", e.getMessage());
        }
        writeResponse(response, json);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        JsonObject json = new JsonObject();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Purchase bill ID is required in URL");
            return;
        }
        String id = pathInfo.substring(1);
        try {
            PurchaseBillDTO dto = gson.fromJson(request.getReader(), PurchaseBillDTO.class);
            System.out.println(dto.toString());
            dto.setId(id);
            dto = purchaseBillService.updatePurchaseBill(dto);

            if (dto != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Purchase bill updated successfully.");
                json.add("bill",gson.toJsonTree(dto));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Purchase bill not found or no changes made.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid bill ID format.");
            json.addProperty("error", e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to update bill.");
            json.addProperty("error", e+"");
        }
        writeResponse(response, json);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing endpoint or bill ID");
            return;
        }


        JsonObject json = new JsonObject();
        String idStr = pathInfo.substring(1);

        try {
            if (purchaseBillService.deletePurchaseBill(idStr)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Purchase Bill deleted successfully");
                json.addProperty("bill_id", idStr);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Purchase Bill not found for ID: " + idStr);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process Purchase Bill deletion");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

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