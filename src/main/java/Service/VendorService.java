package Service;

import DAO.VendorDAO;
import DTO.VendorDTO;
import Model.Vendor;

import java.sql.SQLException;
import java.util.List;

public class VendorService {
    private final VendorDAO vendorDAO = new VendorDAO();
    public boolean createVendor(VendorDTO dto) throws SQLException {
        Vendor vendor = new Vendor(dto.getId(),dto.getName(),dto.getLocation());
        return vendorDAO.insertVendor(vendor);
    }

    public List<Vendor> getAllVendors() throws SQLException {
        return vendorDAO.getAllRows();
    }

    public Vendor getVendorById(int id) throws SQLException {
        return vendorDAO.getVendor(id);
    }

    public boolean updateVendor(Vendor vendor) throws SQLException {
        return vendorDAO.updateVendor(vendor);
    }

    public boolean deleteVendor(int id) throws SQLException {
        return vendorDAO.deleteVendor(id);
    }
}
