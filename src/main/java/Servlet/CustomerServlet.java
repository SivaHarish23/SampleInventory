package Servlet;

import DTO.CustomerDTO;
import DTO.PartyDTO;
import Model.Customer;
import Service.CustomerService;
import Service.ProductService;
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
            handleGetCustomer(idStr, response);
        }
    }

    private void handleGetAllCustomers(HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
//            List<Customer> customers = customerService.getAllCustomers();
            List<Customer> customers = customerService.getAll();

            List<CustomerDTO> customersMasked = new ArrayList<>();
            if(customers != null) for(Customer c : customers) customersMasked.add(CustomerDTO.mask(c));

            json.addProperty("status", "success");
            json.addProperty("message", "Customers retrieved successfully");
            json.add("customers", gson.toJsonTree(customersMasked));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Error retrieving customers");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        writeResponse(response, json);
    }

    private void handleGetCustomer(String id, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(id , PrefixValidator.EntityType.CUSTOMER);
            if (error != null){
                throw new NumberFormatException(error);
            }

            CustomerDTO customerDTO = new CustomerDTO(new PartyDTO.Builder().id(id));
            Customer customerUnmasked = Customer.unMask(customerDTO);

            Customer customer = customerService.getPartyById(customerUnmasked.getId());

            CustomerDTO customerDTOMasked = CustomerDTO.mask(customer);
            if (customer != null) {

                json.addProperty("status", "success");
                json.addProperty("message", "Customer found");
                json.add("customer", gson.toJsonTree(customerDTOMasked));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "not_found");
                json.addProperty("message", "Customer not found for ID: " + id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Customer ID");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }   catch (Exception e) {
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

            Customer customerUnMasked = Customer.unMask(customerDTO);

            CustomerService cs = new CustomerService();
            Map<String, String> errors = cs.validate(customerUnMasked);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            Customer customer = customerService.createParty(customerUnMasked);

            CustomerDTO customerDTOMasked = CustomerDTO.mask(customer);
            if (customerDTO != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                json.addProperty("status", "success");
                json.addProperty("message", "Customer created!");
                json.add("customer", gson.toJsonTree(customerDTOMasked));
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

        String id = pathInfo.substring(1);

        JsonObject json = new JsonObject();
        try {
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(id , PrefixValidator.EntityType.CUSTOMER);
            if (error != null){
                throw new NumberFormatException(error);
            }

            CustomerDTO customerDTO = gson.fromJson(request.getReader(), CustomerDTO.class);
            // Ensure customer ID matches path ID
            customerDTO.setId(id);

            Customer customerUnMasked = Customer.unMask(customerDTO);
            if (customerDTO == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
                return;
            }

            CustomerService cs = new CustomerService();
            Map<String, String> errors = cs.validate(customerUnMasked);
            if (!errors.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                gson.toJson(Collections.singletonMap("errors", errors), response.getWriter());
                return;
            }

            Customer customer = customerService.updateParty(customerUnMasked);

            CustomerDTO customerDTOMasked = CustomerDTO.mask(customer);
            if (customerDTOMasked != null) {
                json.addProperty("status", "success");
                json.addProperty("message", "Customer updated successfully");
                json.add("customer",gson.toJsonTree(customerDTOMasked));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Customer not found or no fields to update");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Customer ID");
            json.addProperty("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }  catch (Exception e) {
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

        JsonObject json = new JsonObject();

        try {
            String id = pathInfo.substring(1);
            PrefixValidator validator = new PrefixValidator();
            String error = validator.validatePrefixedId(id , PrefixValidator.EntityType.CUSTOMER);
            if (error != null){
                throw new NumberFormatException(error);
            }

            CustomerDTO customerDTO = new CustomerDTO(new PartyDTO.Builder().id(id));
            Customer customerUnmasked = Customer.unMask(customerDTO);

            if (customerService.deleteCustomer(customerUnmasked.getId())) {
                json.addProperty("status", "success");
                json.addProperty("message", "Customer deleted successfully");
                json.addProperty("customer_id", id);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                json.addProperty("status", "error");
                json.addProperty("message", "Customer with given ID not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }catch (NumberFormatException e) {
            json.addProperty("status", "error");
            json.addProperty("message", "Invalid Customer ID");
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