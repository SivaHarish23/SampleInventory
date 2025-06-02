package Model;

import DTO.CustomerDTO;
import DTO.VendorDTO;
import Util.TimeUtil;

public class Vendor extends Party {

    public Vendor(Builder builder) {
        super(builder);
        this.setType(Type.VENDOR);  // override or enforce the type here
    }

    public static Vendor unMask(VendorDTO dto){
        return new Vendor(
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