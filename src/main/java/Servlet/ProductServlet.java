package Servlet;

import DTO.ProductDTO;
import DTO.ProductUpdateDTO;
import Model.Product;
import Service.ProductService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo(); // /{id}
        response.setContentType("application/json");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Product> products = productService.getAllProducts();
                response.getWriter().write(gson.toJson(products));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Product product = productService.getProduct(id);
                if (product != null) {
                    response.getWriter().write(gson.toJson(product));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Product not found\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ProductDTO dto = gson.fromJson(request.getReader(), ProductDTO.class);
        System.out.println("In Post Product");
        System.out.println(dto.toString());
        try {
            int productId = productService.addProduct(dto);
            if (productId > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                // ✅ Send success JSON
                response.getWriter().write(
                        String.format("{\"status\":\"success\", \"message\":\"Product created successfully\", \"id\":%d}", productId)
                );
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Failed to create product\"}");
            }
        } catch (SQLException e) {
            System.out.println(e+"");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Database error occurred\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo(); // /{id}
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Missing product ID in URL\"}");
            return;
        }

        int id = Integer.parseInt(pathInfo.substring(1));
        ProductDTO dto = gson.fromJson(request.getReader(), ProductDTO.class);

        try {
            boolean updated = productService.updateProduct(id, dto);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Product updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Product not found with id " + id + "\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo(); // /{id}
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Missing product ID in URL\"}");
            return;
        }

        int id = Integer.parseInt(pathInfo.substring(1));
        try {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Product deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Product not found with id " + id + "\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error\"}");
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    // For PATCH requests (partial update)
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        ProductUpdateDTO dto = gson.fromJson(request.getReader(), ProductUpdateDTO.class);

        if (dto.getId() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Missing product ID in JSON body\"}");
            return;
        }

        try {
            boolean updated = productService.partialUpdateProduct(dto.getId(), dto);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\":\"success\", \"message\":\"Product updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Product not found\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error\"}");
        }
    }
}
