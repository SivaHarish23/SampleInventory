package DTO;

import DTO.PartyDTO;

public class VendorDTO extends PartyDTO {
    public VendorDTO(PartyDTO.Builder builder) {
        super(builder);
        this.setType(PartyDTO.Type.VENDOR);  // override or enforce the type here
    }
}
