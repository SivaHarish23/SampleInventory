package Servlet;

import DTO.VendorDTO;
import DTO.PartyDTO;
import Model.Vendor;
import Service.CustomerService;
import Service.VendorService;
import Util.PrefixValidator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebServlet("/vendors/*")
public class VendorServlet extends HttpServlet {

    private final VendorService vendorService = new VendorService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo(); // e.g., / or /{id}

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /vendors – Retrieve all vendors
            handleGetAllVendors(response);
        } else {
            // GET /vendors/{id} – Retrieve vendor by ID
            String idStr = pathInfo.substring(1); // Remove leading '/'
            handleGetVendor(idStr, response);
        }
    }

    private void handleGetAllVendors(HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
//            List<Vendor> vendors = vendorService.getAllVendors();
            List<Vendor> vendors = vendorService.getAll();

            List<VendorDTO> vendorsMasked = new ArrayList<>();
            if(vendors != null) for(Vendor c : vendors) vendorsMasked.add(VendorDTO.mask(c));

            json.addProperty("status", "success");
            json.addProperty("message", "Vendors retrieved successfully");
            json.add("vendors", gson.toJsonTree(vendorsMasked));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving vendors");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    private void handleGetVendor(String id, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(id , PrefixValidator.EntityType.VENDOR);
            if (error != null){
                throw new NumberFormatException(error);
            }

            VendorDTO vendorDTO = new VendorDTO(new PartyDTO.Builder().id(id));
            Vendor vendorUnmasked = Vendor.unMask(vendorDTO);

            Vendor vendor = vendorService.getPartyById(vendorUnmasked.getId());

            VendorDTO vendorDTOMasked = VendorDTO.mask(vendor);
            if (vendor != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor found");
                json.add("vendor", gson.toJsonTree(vendorDTOMasked));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Vendor not found for ID: " + id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Vendor ID");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }  catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error processing request");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // POST /vendors – Create new vendor
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
            System.out.println(vendorDTO.toString());
            Vendor vendorUnMasked = Vendor.unMask(vendorDTO);

            VendorService vs = new VendorService();
            Map<String, String> errors = vs.validate(vendorUnMasked);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            Vendor vendor = vendorService.createParty(vendorUnMasked);

            VendorDTO vendorDTOMasked = VendorDTO.mask(vendor);
            if (vendorDTO != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor created!");
                json.add("vendor", gson.toJsonTree(vendorDTOMasked));
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
        // PUT /vendors/{id} – Update vendor by ID
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Vendor ID is missing in URL");
            return;
        }

        String id = pathInfo.substring(1);

        JsonObject json = new JsonObject();
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(id , PrefixValidator.EntityType.VENDOR);
            if (error != null){
                throw new NumberFormatException(error);
            }

            VendorDTO vendorDTO = gson.fromJson(request.getReader(), VendorDTO.class);
            // Ensure vendor ID matches path ID
            vendorDTO.setId(id);

            Vendor vendorUnMasked = Vendor.unMask(vendorDTO);
            if (vendorDTO == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
                return;
            }

            VendorService vs = new VendorService();
            Map<String, String> errors = vs.validate(vendorUnMasked);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            Vendor vendor = vendorService.updateParty(vendorUnMasked);

            VendorDTO vendorDTOMasked = VendorDTO.mask(vendor);
            if (vendorDTOMasked != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor updated successfully");
                json.add("vendor",gson.toJsonTree(vendorDTOMasked));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Vendor not found or no fields to update");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Vendor ID.");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process vendor update");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        writeResponse(response, json);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // DELETE /vendors/{id} – Delete vendor by ID
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Vendor ID is missing in URL");
            return;
        }

        String id = pathInfo.substring(1);
        JsonObject json = new JsonObject();

        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(id , PrefixValidator.EntityType.VENDOR);
            if (error != null){
                throw new NumberFormatException(error);
            }

            VendorDTO vendorDTO = new VendorDTO(new PartyDTO.Builder().id(id));
            Vendor vendorUnmasked = Vendor.unMask(vendorDTO);

            if (vendorService.deleteVendor(vendorUnmasked.getId())) {
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor deleted successfully");
                json.addProperty("vendor_id", id);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Vendor with given ID not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Vendor ID.");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }  catch (Exception e) {
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