package DAO;

import Model.Party;
import Model.Vendor;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendorDAO extends PartyDAO<Vendor> {

    @Override
    protected String getTableName() {
        return "vendors";
    }

    @Override
    protected Vendor createEntityFromResultSet(ResultSet rs) throws SQLException {
        return new Vendor(
                new Party.Builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .location(rs.getString("location"))
                        .phone_number(rs.getString("phone_number"))
                        .created_at(rs.getLong("created_at"))
                        .updated_at(rs.getLong("updated_at"))
        );
    }
    public boolean isVendorUsed(int vendorId) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM purchase_bills WHERE vendor_id = ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }
}