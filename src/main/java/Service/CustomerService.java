package Service;

import DAO.CustomerDAO;
import DTO.CustomerDTO;
import Model.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerService {
    private final CustomerDAO customerDAO = new CustomerDAO();
    public boolean createCustomer(CustomerDTO dto) throws SQLException {
        Customer customer = new Customer(dto.getId(),dto.getName(),dto.getLocation());
        return customerDAO.insertCustomer(customer);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.getAllRows();
    }

    public Customer getCustomerById(int id) throws SQLException {
        return customerDAO.getCustomer(id);
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        return customerDAO.updateCustomer(customer);
    }

    public boolean deleteCustomer(int id) throws SQLException {
        return customerDAO.deleteCustomer(id);
    }
}
