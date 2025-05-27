package DAO;

import Model.Vendor;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VendorDAO {
    public boolean insertVendor(Vendor vendor) throws SQLException {
        String sql = "INSERT into vendors (name, location) VALUES (?, ?)";
        try(Connection conn = DBConnection.getInstance().getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,vendor.getName());
            preparedStatement.setString(2,vendor.getLocation());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Vendor> getAllRows() throws SQLException {
        String sql = "SELECT * FROM vendors";
        List<Vendor> vendors= new ArrayList<>();
        try(Connection conn = DBConnection.getInstance().getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                vendors.add(new Vendor(rs.getInt("id"),rs.getString("name"),rs.getString("location")));
        }
        return vendors;
    }

    public Vendor getVendor(int id) throws SQLException{
        String sql = "SELECT * FROM vendors WHERE id = ?";
        try(Connection conn = DBConnection.getInstance().getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Vendor(rs.getInt("id"),rs.getString("name"),rs.getString("location"));
        }
        return null;
    }

    public boolean updateVendor(Vendor vendor) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE vendors SET ");
        List<Object> values = new ArrayList<>();

        if (vendor.getName() != null) {
            sql.append("name = ?, ");
            values.add(vendor.getName());
        }
        if (vendor.getLocation() != null) {
            sql.append("location = ?, ");
            values.add(vendor.getLocation());
        }

        if (values.isEmpty()) return false; // nothing to update

        sql.setLength(sql.length() - 2); // remove last comma and space
        sql.append(" WHERE id = ?");
        values.add(vendor.getId());

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                preparedStatement.setObject(i + 1, values.get(i));
            }
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteVendor(int id) throws SQLException {
        String sql = "DELETE FROM vendors WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }
}
