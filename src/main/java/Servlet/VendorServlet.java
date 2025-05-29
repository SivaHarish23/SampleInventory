package Servlet;

import DTO.VendorDTO;
import Model.Vendor;
import Service.VendorService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/vendors/*")
public class VendorServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final VendorService vendorService = new VendorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo(); // e.g., / or /{id}

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /vendors -> get all vendors
            handleGetAllVendors(response);
        } else {
            // GET /vendors/{id}
            String idStr = pathInfo.substring(1); // remove leading '/'
            try {
                int id = Integer.parseInt(idStr);
                handleGetVendor(id, response);
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid vendor ID format");
            }
        }
    }

    private void handleGetAllVendors(HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            List<Vendor> vendors = vendorService.getAllVendors();
            json.addProperty("status", "success");
            json.addProperty("message", "Vendors retrieved successfully");
            json.add("vendors", gson.toJsonTree(vendors));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving vendors");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    private void handleGetVendor(int id, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            Vendor vendor = vendorService.getVendorById(id);
            if (vendor != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor found");
                json.add("vendor", gson.toJsonTree(vendor));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Vendor not found for ID: " + id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error processing request");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // POST /vendors - create new vendor
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            // POST should be on /vendors only
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            VendorDTO vendorDTO = gson.fromJson(request.getReader(), VendorDTO.class);
            if (vendorService.createVendor(vendorDTO)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor created!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                json.addProperty("status", "error");
                json.addProperty("message", "Failed to create vendor!");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid input: " + e.getMessage());
        }

        writeResponse(response, json);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // PUT /vendors/{id} - update vendor by id
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Vendor ID is missing in URL");
            return;
        }

        String idStr = pathInfo.substring(1);
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid vendor ID format");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            Vendor vendor = gson.fromJson(request.getReader(), Vendor.class);
            if (vendor == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
                return;
            }
            // Ensure vendor id matches path id
            vendor.setId(id);

            if (vendorService.updateVendor(vendor)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor updated successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Vendor not found or no fields to update");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process update");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        writeResponse(response, json);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // DELETE /vendors/{id} - delete vendor by id
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Vendor ID is missing in URL");
            return;
        }

        String idStr = pathInfo.substring(1);
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid vendor ID format");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            if (vendorService.deleteVendor(id)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor deleted successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Vendor with given ID not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process deletion");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        writeResponse(response, json);
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