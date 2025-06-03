package DAO;

import DTO.CustomerDTO;
import DTO.PartyDTO;
import Model.Customer;
import Model.Party;
import Util.DBConnection;
import Util.TimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO extends PartyDAO<Customer> {

    @Override
    protected String getTableName() {
        return "customers";
    }

    @Override
    protected Customer createEntityFromResultSet(ResultSet rs) throws SQLException {
        return new Customer(
                new Party.Builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .location(rs.getString("location"))
                        .phone_number(rs.getString("phone_number"))
                        .created_at(rs.getLong("created_at"))
                        .updated_at(rs.getLong("updated_at"))
        );
    }
    public boolean isCustomerUsed(int customerId) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM sales_invoices WHERE customer_id = ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }
}