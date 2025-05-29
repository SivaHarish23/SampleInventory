package Servlet;

import DTO.CustomerDTO;
import Model.Customer;
import Service.CustomerService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/customers/*")
public class CustomerServlet extends HttpServlet {

    private final CustomerService customerService = new CustomerService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo(); // e.g., / or /{id}

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /customers – Retrieve all customers
            handleGetAllCustomers(response);
        } else {
            // GET /customers/{id} – Retrieve customer by ID
            String idStr = pathInfo.substring(1); // Remove leading '/'
            try {
                int id = Integer.parseInt(idStr);
                handleGetCustomer(id, response);
            } catch (NumberFormatException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer ID format");
            }
        }
    }

    private void handleGetAllCustomers(HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            List<Customer> customers = customerService.getAllCustomers();
            json.addProperty("status", "success");
            json.addProperty("message", "Customers retrieved successfully");
            json.add("customers", gson.toJsonTree(customers));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving customers");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    private void handleGetCustomer(int id, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            Customer customer = customerService.getCustomerById(id);
            if (customer != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Customer found");
                json.add("customer", gson.toJsonTree(customer));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Customer not found for ID: " + id);
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
        // POST /customers – Create new customer
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            // POST should be on /customers only
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            CustomerDTO customerDTO = gson.fromJson(request.getReader(), CustomerDTO.class);
            if (customerService.createCustomer(customerDTO)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                json.addProperty("status", "success");
                json.addProperty("message", "Customer created!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                json.addProperty("status", "error");
                json.addProperty("message", "Failed to create customer!");
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
        // PUT /customers/{id} – Update customer by ID
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Customer ID is missing in URL");
            return;
        }

        String idStr = pathInfo.substring(1);
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer ID format");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            Customer customer = gson.fromJson(request.getReader(), Customer.class);
            if (customer == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
                return;
            }
            // Ensure customer ID matches path ID
            customer.setId(id);

            if (customerService.updateCustomer(customer)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Customer updated successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Customer not found or no fields to update");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process customer update");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        writeResponse(response, json);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // DELETE /customers/{id} – Delete customer by ID
        response.setContentType("application/json");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Customer ID is missing in URL");
            return;
        }

        String idStr = pathInfo.substring(1);
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer ID format");
            return;
        }

        JsonObject json = new JsonObject();
        try {
            if (customerService.deleteCustomer(id)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Customer deleted successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Customer with given ID not found");
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