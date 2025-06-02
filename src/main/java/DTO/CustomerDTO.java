package DTO;

import DTO.PartyDTO;
import Model.Customer;
import Model.Party;
import Util.TimeUtil;

public class CustomerDTO extends PartyDTO {
    public CustomerDTO(PartyDTO.Builder builder) {
        super(builder);
//        this.setType(Type.CUSTOMER);  // override or enforce the type here
    }


    public static CustomerDTO mask(Customer customer){
        return new CustomerDTO(
                new PartyDTO.Builder()
                        .id((customer.getId() != null) ? "CUS-" + customer.getId() : null)
                        .name(customer.getName())
                        .location(customer.getLocation())
                        .phone_number(customer.getPhoneNumber())
                        .created_at((customer.getCreatedAt()!=null) ? TimeUtil.epochToString(customer.getCreatedAt()) : null)
                        .updated_at((customer.getUpdatedAt()!=null) ? TimeUtil.epochToString(customer.getUpdatedAt()) : null)
        );
    }
}