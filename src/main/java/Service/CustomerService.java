package Service;

import DAO.CustomerDAO;
import DTO.CustomerDTO;

public class CustomerService extends PartyService<CustomerDTO,CustomerDAO>{

    public CustomerService() {
        super(new CustomerDAO());
    }

}
