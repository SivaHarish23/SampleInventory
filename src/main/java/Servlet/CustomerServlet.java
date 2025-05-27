package Servlet;

import DTO.CustomerDTO;
import DTO.ProductDTO;
import DTO.ProductUpdateDTO;
import Model.Customer;
import Model.Product;
import Service.CustomerService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/customers/*")
public class CustomerServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final CustomerService customerService = new CustomerService();

    protected void doGet(HttpServletRequest request , HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if(path == null) path = "";

        switch (path){
            case "/getAllCustomers":
                handleGetAllCustomers(request,response);
                break;
            case "/getCustomer":
                handleGetCustomer(request,response);
                break;
            default:
                invalidEndPoint(response, path);
        }
    }

    private void handleGetCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            JsonObject requestBody = gson.fromJson(request.getReader(), JsonObject.class);
            if (!requestBody.has("id")) {
                json.addProperty("status", "error");
                json.addProperty("message", "Missing 'id' in request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                int id = requestBody.get("id").getAsInt();
                Customer customer = customerService.getCustomerById(id);
                if (customer != null) {
                    json.addProperty("status", "success");
                    json.addProperty("message", "Customer found");
                    json.add("customer", gson.toJsonTree(customer));
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

    private void handleGetAllCustomers(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        response.getWriter().println(gson.toJson(json));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();
        if(path == null) path = "";

        if(!path.equals("/createCustomer")){
            invalidEndPoint(response, path);
        }
        JsonObject json = new JsonObject();
        try{
            CustomerDTO dto = gson.fromJson(request.getReader(),CustomerDTO.class);
            System.out.println("Cutomer Insertion : " + dto.toString());
            if(customerService.createCustomer(dto)){
                response.setStatus(HttpServletResponse.SC_CREATED);
                json.addProperty("status", "success");
                json.addProperty("message", "Customer created!");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                json.addProperty("status", "error");
                json.addProperty("message", "Failed to create Customer!");
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

        if (!path.equals("/updateCustomer")) {
            invalidEndPoint(response, path);
            return;
        }

        JsonObject json = new JsonObject();
        try {
            Customer customer = gson.fromJson(request.getReader(), Customer.class);
            if (customer.getId() == null) {
                json.addProperty("status", "error");
                json.addProperty("message", "Missing 'id' in request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {

                if (customerService.updateCustomer(customer)) {
                    json.addProperty("status", "success");
                    json.addProperty("message", "Customer updated successfully");
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    json.addProperty("status", "error");
                    json.addProperty("message", "Customer not found or no fields to update");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            json.addProperty("status", "error");
            json.addProperty("message", "Failed to process customer update");
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
        if (!path.equals("/deleteCustomer")) {
            invalidEndPoint(response, path);
            return;
        }
        Customer customer = gson.fromJson(request.getReader(),Customer.class);
        if (customer.getId() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("status", "error");
            errorJson.addProperty("message", "Missing 'id' in request");
            response.getWriter().println(errorJson);
            return;
        }

        int id = customer.getId();
        try {
            if (customerService.deleteCustomer(id)) {
                json.addProperty("status", "success");
                json.addProperty("message", "Customer deleted successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                json.addProperty("status", "error");
                json.addProperty("message", "Customer with given id not found");
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
