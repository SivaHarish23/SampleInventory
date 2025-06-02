package Service;

import DAO.CustomerDAO;
import DTO.CustomerDTO;
import Model.Customer;

public class CustomerService extends PartyService<Customer,CustomerDAO>{

    public CustomerService() {
        super(new CustomerDAO());
    }

}
