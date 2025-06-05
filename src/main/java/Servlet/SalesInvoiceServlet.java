package Servlet;


import DTO.InsufficientStockDTO;
import DTO.SalesInvoiceDTO;
import Model.InvoiceLineItem;
import Model.SalesInvoice;
import Service.SalesInvoiceServiceImpl;
import Util.PrefixValidator;
import Validators.SalesInvoiceValidator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebServlet("/salesInvoice/*")
public class SalesInvoiceServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final SalesInvoiceServiceImpl salesInvoiceService = new SalesInvoiceServiceImpl();
    private final SalesInvoiceValidator salesInvoiceValidator = new SalesInvoiceValidator();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleGetAllInvoices(response);
        } else {
            handleGetInvoiceById(pathInfo.substring(1), response);
        }
    }

    private void handleGetAllInvoices(HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            List<SalesInvoice> allInvoices = salesInvoiceService.getAllInvoices();
            for(SalesInvoice p : allInvoices) System.out.println(p.toString());

            System.out.println();

            List<SalesInvoiceDTO> maskedInvoices = new ArrayList<>();
            for(SalesInvoice pb : allInvoices) maskedInvoices.add(new SalesInvoiceDTO(pb));
            for(SalesInvoiceDTO p : maskedInvoices) System.out.println(p.toString());

            json.addProperty("status", "success");
            json.addProperty("message", "Sales invoices retrieved successfully.");
            json.add("sales_invoices", gson.toJsonTree(maskedInvoices));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving purchase invoices.");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        writeResponse(response, json);
    }

    private void handleGetInvoiceById(String idStr, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(idStr , PrefixValidator.EntityType.INVOICE);
            if (error != null){
                throw new NumberFormatException(error);
            }

            Integer id = Integer.parseInt(idStr.substring(4));
            SalesInvoice unMaskedinvoice = salesInvoiceService.getInvoiceById(id);
            SalesInvoiceDTO maskedInvoice = new SalesInvoiceDTO(unMaskedinvoice);
            if (maskedInvoice != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Sales invoice found.");
                json.add("sales_invoice", gson.toJsonTree(maskedInvoice));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Sales invoice not found for ID: " + idStr);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid invoice ID format.");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving purchase invoice.");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
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
            SalesInvoiceDTO dto = gson.fromJson(request.getReader(), SalesInvoiceDTO.class);
            System.out.println(dto.toString());

            Map<String, List<String>> errors = salesInvoiceValidator.validateForCreate(dto);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            SalesInvoice salesInvoiceUnMasked = new SalesInvoice(dto);
            System.out.println(salesInvoiceUnMasked.toString());

            List<InvoiceLineItem> duplicates = salesInvoiceService.findDuplicateLineItems(salesInvoiceUnMasked.getInvoice_line_items());
            if (!duplicates.isEmpty()) {
                json.addProperty("status", "error");
                json.addProperty("message", "Duplicate line items found");

                JsonArray duplicateArray = new JsonArray();
                for (InvoiceLineItem dup : duplicates)
                    duplicateArray.add(gson.toJsonTree(dup));

                json.add("duplicates", duplicateArray);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(response, json);
                return; // Stop further processing
            }

            // Validate stock availability BEFORE creating invoice
            if(salesInvoiceUnMasked.getStatus() == 1){
                List<InsufficientStockDTO> insufficientList = salesInvoiceService.validateStockAvailability(salesInvoiceUnMasked.getInvoice_line_items());

                if (!insufficientList.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    json.addProperty("status", "error");
                    json.addProperty("message", "Insufficient stock for one or more products.");
                    json.add("insufficient_stock", gson.toJsonTree(insufficientList));
                    writeResponse(response, json);
                    return;
                }
            }

            SalesInvoice insertedInvoice = salesInvoiceService.createInvoice(salesInvoiceUnMasked);
            System.out.println(insertedInvoice.toString());
//            for(InvoiceLineItem bdto : insertedInvoice.getInvoice_line_items()) System.out.println(bdto.toString());
//            System.out.println();

            SalesInvoiceDTO maskedInvoice = new SalesInvoiceDTO(insertedInvoice);
            System.out.println(maskedInvoice.toString());
//            for(InvoiceLineItemDTO bdto : maskedInvoice.getInvoiceLineItems()) System.out.println(bdto.toString());
//            System.out.println();

            response.setStatus(HttpServletResponse.SC_CREATED);
            json.addProperty("status", "success");
            json.addProperty("message", "Sales invoice created successfully.");
            json.add("sales_invoice", gson.toJsonTree(maskedInvoice));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.addProperty("status", "error");
            json.addProperty("message", "Database error");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid input");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
        }
        writeResponse(response, json);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        JsonObject json = new JsonObject();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Sales invoice ID is required in URL");
            return;
        }
        String idstr = pathInfo.substring(1);
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(idstr , PrefixValidator.EntityType.INVOICE);
            if (error != null){
                throw new NumberFormatException(error);
            }
            if (salesInvoiceService.isDelivered(Integer.parseInt(idstr.substring(4)))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                json.addProperty("status", "forbidden");
                json.addProperty("message", "Cannot modify a delivered sales invoice : " + idstr);
                writeResponse(response, json);
                return;
            }

            SalesInvoiceDTO dto = gson.fromJson(request.getReader(), SalesInvoiceDTO.class);
            dto.setId(idstr);

            Map<String, List<String>> errors = salesInvoiceValidator.validateForUpdate(dto);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            SalesInvoice unMaskedInvoice = new SalesInvoice(dto);

            List<InvoiceLineItem> duplicates = salesInvoiceService.findDuplicateLineItems(unMaskedInvoice.getInvoice_line_items());
            if (!duplicates.isEmpty()) {
                json.addProperty("status", "error");
                json.addProperty("message", "Duplicate line items found");

                JsonArray duplicateArray = new JsonArray();
                for (InvoiceLineItem dup : duplicates)
                    duplicateArray.add(gson.toJsonTree(dup));

                json.add("duplicates", duplicateArray);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(response, json);
                return; // Stop further processing
            }

            if(unMaskedInvoice.getStatus() == 1){
                List<InsufficientStockDTO> insufficientList = salesInvoiceService.validateStockAvailability(unMaskedInvoice.getInvoice_line_items());

                if (!insufficientList.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    json.addProperty("status", "error");
                    json.addProperty("message", "Insufficient stock for one or more products.");
                    json.add("insufficient_stock", gson.toJsonTree(insufficientList));
                    writeResponse(response, json);
                    return;
                }
            }

            SalesInvoice updatedInvoice = salesInvoiceService.updateInvoice(unMaskedInvoice);

            SalesInvoiceDTO maskedInvoice = new SalesInvoiceDTO(updatedInvoice);

            if (maskedInvoice != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Sales invoice updated successfully.");
                json.add("sales_invoice",gson.toJsonTree(maskedInvoice));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Sales invoice not found or no changes made.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid invoice ID format.");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to update invoice.");
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
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing endpoint or invoice ID");
            return;
        }


        JsonObject json = new JsonObject();
        String idStr = pathInfo.substring(1);
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(idStr , PrefixValidator.EntityType.INVOICE);
            if (error != null){
                throw new NumberFormatException(error);
            }

            Integer id = Integer.parseInt(idStr.substring(4));

            if (salesInvoiceService.deleteInvoice(id)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Sales Invoice deleted successfully");
                json.addProperty("invoice_id", idStr);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Sales Invoice not found for ID: " + idStr);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Invoice ID format.");
            json.addProperty("error", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process Sales Invoice deletion");
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
