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

    protected void doGet(HttpServletRequest request , HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if(path == null) path = "";

        switch (path){
            case "/getAllVendors":
                handleGetAllVendors(request,response);
                break;
            case "/getVendor":
                handleGetVendor(request,response);
                break;
            default:
                invalidEndPoint(response, path);
        }
    }

    private void handleGetVendor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            JsonObject requestBody = gson.fromJson(request.getReader(), JsonObject.class);
            if (!requestBody.has("id")) {
                json.addProperty("status", "error");
                json.addProperty("message", "Missing 'id' in request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                int id = requestBody.get("id").getAsInt();
                Vendor vendor = vendorService.getVendorById(id);
                if (vendor != null) {
                    json.addProperty("status", "success");
                    json.addProperty("message", "Vendor found");
                    json.add("vendor", gson.toJsonTree(vendor));
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    json.addProperty("status", "not_found");
                    json.addProperty("message", "Product not found for ID: " + id);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error processing request");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.getWriter().println(gson.toJson(json));
    }

    private void handleGetAllVendors(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        response.getWriter().println(gson.toJson(json));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if(path == null) path = "";

        if(!path.equals("/createVendor")){
            invalidEndPoint(response, path);
        }
        JsonObject json = new JsonObject();
        try{
            VendorDTO dto = gson.fromJson(request.getReader(),VendorDTO.class);
            System.out.println("Cutomer Insertion : " + dto.toString());
            if(vendorService.createVendor(dto)){
                response.setStatus(HttpServletResponse.SC_CREATED);
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor created!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                json.addProperty("status", "error");
                json.addProperty("message", "Failed to create Vendor!");
            }
        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid input: " + e.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(json));
            out.flush();
        }
    }

    protected void doPut(HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) path = "";

        if (!path.equals("/updateVendor")) {
            invalidEndPoint(response, path);
            return;
        }

        JsonObject json = new JsonObject();
        try {
            Vendor vendor = gson.fromJson(request.getReader(), Vendor.class);
            if (vendor.getId() == null) {
                json.addProperty("status", "error");
                json.addProperty("message", "Missing 'id' in request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {

                if (vendorService.updateVendor(vendor)) {
                    json.addProperty("status", "success");
                    json.addProperty("message", "Vendor updated successfully");
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    json.addProperty("status", "error");
                    json.addProperty("message", "Vendor not found or no fields to update");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process vendor update");
            json.addProperty("error", e.getMessage());
        }

        // Send final JSON response
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(json));
            out.flush();
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) path = "";

        JsonObject json = new JsonObject();
        if (!path.equals("/deleteVendor")) {
            invalidEndPoint(response, path);
            return;
        }
        Vendor vendor = gson.fromJson(request.getReader(),Vendor.class);
        if (vendor.getId() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", "Missing 'id' in request");
            response.getWriter().println(errorJson);
            return;
        }

        int id = vendor.getId();
        try {
            if (vendorService.deleteVendor(id)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Vendor deleted successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                json.addProperty("status", "error");
                json.addProperty("message", "Vendor with given id not found");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process deletion");
            json.addProperty("error", e.getMessage());
        }

        // Send final JSON response
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(json));
            out.flush();
        }
    }

    private void invalidEndPoint(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("status", "error");
        errorJson.addProperty("message", "Invalid endpoint : " + path);
        response.getWriter().println(errorJson);
    }

}
