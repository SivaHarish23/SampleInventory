package Servlet;

import DTO.ProductDTO;
import DTO.ProductUpdateDTO;
import Model.Product;
import Service.ProductService;
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


@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo(); // e.g. / or /{id}

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /products -> get all products
            handleGetAllProducts(response);
        } else {
            // GET /products/{id}
            String idStr = pathInfo.substring(1); // remove leading '/'
            try {
                int id = Integer.parseInt(idStr);
                handleGetProduct(id, response);
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
            }
        }
    }

    private void handleGetAllProducts(HttpServletResponse response) throws IOException {
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
        writeResponse(response, json);
    }

    private void handleGetProduct(int id, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
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
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error processing request");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // POST /products - create new product
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            // POST should be on /products only
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            ProductDTO productDTO = gson.fromJson(request.getReader(), ProductDTO.class);
            if (productService.addProduct(productDTO)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                json.addProperty("status", "success");
                json.addProperty("message", "Product created!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                json.addProperty("status", "error");
                json.addProperty("message", "Failed to create product!");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.addProperty("status", "error");
            json.addProperty("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid input: " + e.getMessage());
        }

        writeResponse(response, json);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // PUT /products/{id} - update product by id
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Product ID is missing in URL");
            return;
        }

        String idStr = pathInfo.substring(1);
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            ProductUpdateDTO dto = gson.fromJson(request.getReader(), ProductUpdateDTO.class);
            if (dto == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
                return;
            }
            // Ensure DTO id matches path id
            dto.setId(id);

            if (productService.updateProduct(dto)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Product updated successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Product not found or no fields to update");
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

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // DELETE /products/{id} - delete product by id
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Product ID is missing in URL");
            return;
        }

        String idStr = pathInfo.substring(1);
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            if (productService.deleteProduct(id)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Product deleted successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Product with given id not found");
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