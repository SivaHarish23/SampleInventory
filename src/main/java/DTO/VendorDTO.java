package DTO;

import Model.Vendor;
import Util.TimeUtil;

public class VendorDTO extends PartyDTO {
    public VendorDTO(PartyDTO.Builder builder) {
        super(builder);
//        this.setType(PartyDTO.Type.VENDOR);  // not here, but in MODEL.
    }
    public static VendorDTO mask(Vendor vendor){
        return new VendorDTO(
                new PartyDTO.Builder()
                        .id((vendor.getId() != null) ? "VEN-" + vendor.getId() : null)
                        .name(vendor.getName())
                        .location(vendor.getLocation())
                        .phone_number(vendor.getPhoneNumber())
                        .created_at((vendor.getCreatedAt()!=null) ? TimeUtil.epochToString(vendor.getCreatedAt()) : null)
                        .updated_at((vendor.getUpdatedAt()!=null) ? TimeUtil.epochToString(vendor.getUpdatedAt()) : null)
        );
    }

    @Override
    public String toString() {
        return "VendorDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", phoneNumber='" + phone_number + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", type=" + type +
                '}';
    }
}
