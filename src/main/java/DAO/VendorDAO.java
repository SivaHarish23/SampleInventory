package DAO;

import DTO.PartyDTO;
import DTO.VendorDTO;
import Util.TimeUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VendorDAO extends PartyDAO<VendorDTO> {

    @Override
    protected String getTableName() {
        return "vendors";
    }

    @Override
    protected VendorDTO createEntityFromResultSet(ResultSet rs) throws SQLException {
        return new VendorDTO(
                new PartyDTO.Builder()
                        .id("VEN-" + rs.getInt("id"))
                        .name(rs.getString("name"))
                        .location(rs.getString("location"))
                        .phoneNumber(rs.getString("phone_number"))
                        .created_at(TimeUtil.epochToString(rs.getLong("created_at")))
                        .updated_at(TimeUtil.epochToString(rs.getLong("updated_at")))
        );
    }
}