package Service;

import DAO.VendorDAO;
import DTO.VendorDTO;
import Model.Vendor;

public class VendorService extends PartyService<Vendor,VendorDAO>{

    public VendorService() {
        super(new VendorDAO());
    }

}
