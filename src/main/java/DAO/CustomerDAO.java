package DAO;

import DTO.CustomerDTO;
import DTO.PartyDTO;
import Model.Customer;
import Util.TimeUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO extends PartyDAO<CustomerDTO> {

    @Override
    protected String getTableName() {
        return "customers";
    }

    @Override
    protected CustomerDTO createEntityFromResultSet(ResultSet rs) throws SQLException {
        return new CustomerDTO(
                new PartyDTO.Builder()
                        .id("CUS-" + rs.getInt("id"))
                        .name(rs.getString("name"))
                        .location(rs.getString("location"))
                        .phoneNumber(rs.getString("phone_number"))
                        .created_at(TimeUtil.epochToString(rs.getLong("created_at")))
                        .updated_at(TimeUtil.epochToString(rs.getLong("updated_at")))
        );
    }
}