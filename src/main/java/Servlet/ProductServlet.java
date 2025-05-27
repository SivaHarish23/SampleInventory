package Servlet;

import DTO.ProductDTO;
import DTO.ProductUpdateDTO;
import Model.Product;
import Service.ProductService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) path = "";

        switch (path) {
            case "/getALlProducts":
                handleGetAllProducts(request, response);
                break;
            case "/getProduct":
                handleGetProduct(request, response);
                break;
            default:
                invalidEndPoint(response, path);
        }
    }

    private void handleGetAllProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            List<Product> products = productService.getAllProducts();
            json.addProperty("status", "success");
            json.addProperty("message", "Products retrieved successfully");
            json.add("products", gson.toJsonTree(products));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving products");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.getWriter().println(gson.toJson(json));
    }

    private void handleGetProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            JsonObject requestBody = gson.fromJson(request.getReader(), JsonObject.class);
            if (!requestBody.has("id")) {
                json.addProperty("status", "error");
                json.addProperty("message", "Missing 'id' in request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                int id = requestBody.get("id").getAsInt();
                Product product = productService.getProductById(id);
                if (product != null) {
                    json.addProperty("status", "success");
                    json.addProperty("message", "Product found");
                    json.add("product", gson.toJsonTree(product));
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) path = "";

        if (!path.equals("/createProduct")) {
            invalidEndPoint(response, path);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            ProductDTO productDTO = gson.fromJson(request.getReader(), ProductDTO.class);
            if (productService.addProduct(productDTO)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                jsonObject.put("status", "success");
                jsonObject.put("message", "Product created!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonObject.put("status", "error");
                jsonObject.put("message", "Failed to create product!");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            jsonObject.put("status", "error");
            jsonObject.put("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            jsonObject.put("status", "error");
            jsonObject.put("message", "Invalid input: " + e.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonObject.toString());
            out.flush();
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if (path == null) path = "";

        if (!path.equals("/updateProduct")) {
            invalidEndPoint(response, path);
            return;
        }

        JsonObject json = new JsonObject();
        try {
            ProductUpdateDTO dto = gson.fromJson(request.getReader(), ProductUpdateDTO.class);
            if (dto.getId() == null) {
                json.addProperty("status", "error");
                json.addProperty("message", "Missing 'id' in request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                if (productService.updateProduct(dto)) {
                    json.addProperty("status", "success");
                    json.addProperty("message", "Product updated successfully");
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    json.addProperty("status", "error");
                    json.addProperty("message", "Product not found or no fields to update");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process update");
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
        if (!path.equals("/deleteProduct")) {
            invalidEndPoint(response, path);
            return;
        }
        JsonObject requestBody = gson.fromJson(request.getReader(), JsonObject.class);

        if (!requestBody.has("id") || requestBody.get("id").isJsonNull()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", "Missing 'id' in request");
            response.getWriter().println(errorJson);
            return;
        }

        int id = requestBody.get("id").getAsInt();
        try {
            if (productService.deleteProduct(id)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Product deleted successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                json.addProperty("status", "error");
                json.addProperty("message", "Product with given id not found");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process update");
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