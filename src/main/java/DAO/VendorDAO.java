package DAO;

import DTO.PartyDTO;
import DTO.VendorDTO;
import Model.Party;
import Model.Vendor;
import Util.TimeUtil;

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
}