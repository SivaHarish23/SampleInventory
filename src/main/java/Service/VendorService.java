package Service;

import DAO.VendorDAO;
import Model.Vendor;

import java.sql.SQLException;

public class VendorService extends PartyService<Vendor,VendorDAO>{

    public VendorService() {
        super(new VendorDAO());
    }
    public boolean deleteVendor(int id) throws SQLException {
        if (new VendorDAO().isVendorUsed(id)) {
            throw new IllegalStateException("Cannot delete: Vendor (VEN-" + id + ") is associated with purchase bills.");
        }
        return new VendorDAO().deleteParty(id);
    }

}
