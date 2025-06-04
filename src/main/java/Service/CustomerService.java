package Service;

import DAO.CustomerDAO;
import DTO.CustomerDTO;
import Model.Customer;

import java.sql.SQLException;

public class CustomerService extends PartyService<Customer,CustomerDAO>{

    public CustomerService() {
        super(new CustomerDAO());
    }

    public boolean deleteCustomer(int id) throws SQLException {
        if (new CustomerDAO().isCustomerUsed(id)) {
            throw new IllegalStateException("Cannot delete: Customer (CUS-" + id + ") is associated with sales invoices.");
        }
        return new CustomerDAO().deleteParty(id);
    }

}
