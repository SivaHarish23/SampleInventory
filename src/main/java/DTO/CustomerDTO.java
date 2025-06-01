package DTO;

import DTO.PartyDTO;

public class CustomerDTO extends PartyDTO {
    public CustomerDTO(PartyDTO.Builder builder) {
        super(builder);
        this.setType(Type.CUSTOMER);  // override or enforce the type here
    }
}