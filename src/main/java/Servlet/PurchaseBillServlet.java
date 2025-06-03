package Servlet;

import DTO.BillLineItemDTO;
import DTO.PurchaseBillDTO;
import Model.BillLineItem;
import Model.InvoiceLineItem;
import Model.PurchaseBill;
import Service.PurchaseBillService;
import Service.PurchaseBillServiceImpl;
import Util.PrefixValidator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            List<PurchaseBill> allBills = purchaseBillService.getAllPurchaseBills();
            for(PurchaseBill p : allBills) System.out.println(p.toString());

            System.out.println();

            List<PurchaseBillDTO> maskedBills = new ArrayList<>();
            for(PurchaseBill pb : allBills) maskedBills.add(new PurchaseBillDTO(pb));
            for(PurchaseBillDTO p : maskedBills) System.out.println(p.toString());

            json.addProperty("status", "success");
            json.addProperty("message", "Purchase bills retrieved successfully.");
            json.add("purchase_bills", gson.toJsonTree(maskedBills));
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
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(idStr , PrefixValidator.EntityType.BILL);
            if (error != null){
                throw new NumberFormatException(error);
            }

            Integer id = Integer.parseInt(idStr.substring(4));
            PurchaseBill unMaskedbill = purchaseBillService.getPurchaseBillById(id);
            PurchaseBillDTO maskedBill = new PurchaseBillDTO(unMaskedbill);
            if (maskedBill != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Purchase bill found.");
                json.add("purchase_bill", gson.toJsonTree(maskedBill));
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

            Map<String, List<String>> errors = purchaseBillService.validate(dto, false);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            PurchaseBill purchaseBillUnMasked = new PurchaseBill(dto);
            System.out.println(purchaseBillUnMasked.toString());

            List<BillLineItem> duplicates = purchaseBillService.findDuplicateLineItems(purchaseBillUnMasked.getBill_line_items());
            if (!duplicates.isEmpty()) {
                json.addProperty("status", "error");
                json.addProperty("message", "Duplicate line items found");

                JsonArray duplicateArray = new JsonArray();
                for (BillLineItem dup : duplicates)
                    duplicateArray.add(gson.toJsonTree(dup));

                json.add("duplicates", duplicateArray);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(response, json);
                return; // Stop further processing
            }

            PurchaseBill insertedBill = purchaseBillService.createPurchaseBill(purchaseBillUnMasked);
            System.out.println(insertedBill.toString());
//            for(BillLineItem bdto : insertedBill.getBill_line_items()) System.out.println(bdto.toString());
//            System.out.println();

            PurchaseBillDTO maskedBill = new PurchaseBillDTO(insertedBill);
            System.out.println(maskedBill.toString());
//            for(BillLineItemDTO bdto : maskedBill.getBillLineItems()) System.out.println(bdto.toString());
//            System.out.println();

            response.setStatus(HttpServletResponse.SC_CREATED);
            json.addProperty("status", "success");
            json.addProperty("message", "Purchase bill created successfully.");
            json.add("purchase_bill", gson.toJsonTree(maskedBill));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.addProperty("status", "error");
            json.addProperty("message", "Database error");
            json.addProperty("error", e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
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
        String idstr = pathInfo.substring(1);
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(idstr , PrefixValidator.EntityType.BILL);
            if (error != null){
                throw new NumberFormatException(error);
            }
            if (purchaseBillService.isReceived(Integer.parseInt(idstr))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                json.addProperty("status", "forbidden");
                json.addProperty("message", "Cannot modify a received purchase bill.");
                writeResponse(response, json);
                return;
            }

            PurchaseBillDTO dto = gson.fromJson(request.getReader(), PurchaseBillDTO.class);
            dto.setId(idstr);

            Map<String, List<String>> errors = purchaseBillService.validate(dto, true);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            PurchaseBill unMaskedBill = new PurchaseBill(dto);

            List<BillLineItem> duplicates = purchaseBillService.findDuplicateLineItems(unMaskedBill.getBill_line_items());
            if (!duplicates.isEmpty()) {
                json.addProperty("status", "error");
                json.addProperty("message", "Duplicate line items found");

                JsonArray duplicateArray = new JsonArray();
                for (BillLineItem dup : duplicates)
                    duplicateArray.add(gson.toJsonTree(dup));

                json.add("duplicates", duplicateArray);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(response, json);
                return; // Stop further processing
            }

            PurchaseBill updatedBill = purchaseBillService.updatePurchaseBill(unMaskedBill);

            PurchaseBillDTO maskedBill = new PurchaseBillDTO(updatedBill);

            if (maskedBill != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Purchase bill updated successfully.");
                json.add("purchase_bill",gson.toJsonTree(maskedBill));
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
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to update bill.");
            json.addProperty("error", e+"");
            e.printStackTrace();
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
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(idStr , PrefixValidator.EntityType.BILL);
            if (error != null){
                throw new NumberFormatException(error);
            }
            Integer id = Integer.parseInt(idStr.substring(4));

            if (purchaseBillService.deletePurchaseBill(id)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Purchase Bill deleted successfully");
                json.addProperty("bill_id", idStr);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Purchase Bill not found for ID: " + idStr);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid bill ID format.");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
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