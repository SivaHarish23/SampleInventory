package Service;

import DAO.VendorDAO;
import DTO.VendorDTO;

public class VendorService extends PartyService<VendorDTO,VendorDAO>{

    public VendorService() {
        super(new VendorDAO());
    }

}
