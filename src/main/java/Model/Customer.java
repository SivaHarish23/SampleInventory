package Model;


import DTO.CustomerDTO;
import Util.TimeUtil;

public class Customer extends Party {

    public Customer(Builder builder) {
        super(builder);
        this.setType(Type.CUSTOMER);  // override or enforce the type here
    }

    public static Customer unMask(CustomerDTO dto){
        return new Customer(
                new Party.Builder()
                        .id((dto.getId() != null) ? Integer.parseInt(dto.getId().substring(4)) : null)
                        .name(dto.getName())
                        .location(dto.getLocation())
                        .phone_number(dto.getPhoneNumber())
                        .created_at((dto.getCreatedAt()!=null) ? TimeUtil.stringToEpoch(dto.getCreatedAt()) : null)
                        .updated_at((dto.getUpdatedAt()!=null) ? TimeUtil.stringToEpoch(dto.getUpdatedAt()) : null)
        );
    }
}