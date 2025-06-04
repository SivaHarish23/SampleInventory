package Servlet;

import DTO.ProductDTO;
import Model.Product;
import Service.ProductService;
import Util.PrefixValidator;
import Validators.ProductValidator;
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

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final ProductValidator productValidator = new ProductValidator();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo(); // e.g. / or /{id}

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /products – Retrieve all products
            handleGetAllProducts(response);
        } else {
            // GET /products/{id}
            String id = pathInfo.substring(1); // remove leading '/'
            handleGetProduct(id, response);
        }
    }

    private void handleGetAllProducts(HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            List<Product> products = productService.getAllProducts();

            List<ProductDTO> maskedProducts = new ArrayList<>();

            if(products != null) for(Product p : products) maskedProducts.add(new ProductDTO(p));

            json.addProperty("status", "success");
            json.addProperty("message", "Products retrieved successfully");
            json.add("products", gson.toJsonTree(maskedProducts));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving products");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    private void handleGetProduct(String id, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(id , PrefixValidator.EntityType.PRODUCT);
            if (error != null){
                System.out.println("ProductId Validation failed: " + error);
                throw new NumberFormatException(error);
            }

            ProductDTO dto = new ProductDTO.Builder().id(id).build();
            Product productModel = new Product(dto);

            Product product = productService.getProductById(productModel.getId());

            ProductDTO maskedProduct = new ProductDTO(product);
            if (product != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Product found");
                json.add("product", gson.toJsonTree(maskedProduct));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Product not found for ID: " + id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Product ID");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
        // POST /products – Create new product
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            ProductDTO productDTO = gson.fromJson(request.getReader(), ProductDTO.class);

            Map<String, String> errors = productValidator.validate(productDTO);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            Product unMaskedProduct = new Product(productDTO);
            Product savedProduct = productService.addProduct(unMaskedProduct);

            ProductDTO maskedProductDTO = new ProductDTO(savedProduct);
            if (maskedProductDTO != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Product created!");
                json.add("product", gson.toJsonTree(maskedProductDTO));
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Failed to create product");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid input: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

        JsonObject json = new JsonObject();
        try {
            String idStr = pathInfo.substring(1);
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(idStr , PrefixValidator.EntityType.PRODUCT);
            if (error != null){
                System.out.println("ProductId Validation failed: " + error);
                throw new NumberFormatException(error);
            }

            ProductDTO dto = gson.fromJson(request.getReader(), ProductDTO.class);

            ProductService ps = new ProductService();
            Map<String, String> errors = productValidator.validateForUpdate(dto);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            // Ensure DTO id matches path id
            dto.setId(idStr);
            Product unMaskedProduct = new Product(dto);
            if (unMaskedProduct == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
                return;
            }

            Product updatedProductDTO = productService.updateProduct(unMaskedProduct);

            ProductDTO maskedProductDTO = new ProductDTO(updatedProductDTO);
            if (maskedProductDTO != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Product updated successfully");
                json.add("product" , gson.toJsonTree(maskedProductDTO));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Product not found or no fields to update");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Product ID");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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



        JsonObject json = new JsonObject();
        try {
            String idStr = pathInfo.substring(1);
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(idStr , PrefixValidator.EntityType.PRODUCT);
            if (error != null){
                throw new NumberFormatException(error);
            }

            ProductDTO dto = new ProductDTO.Builder().id(idStr).build();
            Product productModel = new Product(dto);
            if (productService.deleteProduct(productModel.getId())) {
                json.addProperty("status", "success");
                json.addProperty("message", "Product deleted successfully");
                json.addProperty("product_id", idStr);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Product not found for ID: " + idStr);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Product ID");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }catch (IllegalStateException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.setContentType("application/json");
            gson.toJson(Collections.singletonMap("error", e.getMessage()), response.getWriter());
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