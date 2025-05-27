package DAO;

import Model.Customer;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public boolean insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT into customers (name, location) VALUES (?, ?)";
        try(Connection conn = DBConnection.getInstance().getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,customer.getName());
            preparedStatement.setString(2,customer.getLocation());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Customer> getAllRows() throws SQLException {
        String sql = "SELECT * FROM customers";
        List<Customer> customers= new ArrayList<>();
        try(Connection conn = DBConnection.getInstance().getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                customers.add(new Customer(rs.getInt("id"),rs.getString("name"),rs.getString("location")));
        }
        return customers;
    }

    public Customer getCustomer(int id) throws SQLException{
        String sql = "SELECT * FROM customers WHERE id = ?";
        try(Connection conn = DBConnection.getInstance().getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Customer(rs.getInt("id"),rs.getString("name"),rs.getString("location"));
        }
        return null;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE customers SET ");
        List<Object> values = new ArrayList<>();

        if (customer.getName() != null) {
            sql.append("name = ?, ");
            values.add(customer.getName());
        }
        if (customer.getLocation() != null) {
            sql.append("location = ?, ");
            values.add(customer.getLocation());
        }

        if (values.isEmpty()) return false; // nothing to update

        sql.setLength(sql.length() - 2); // remove last comma and space
        sql.append(" WHERE id = ?");
        values.add(customer.getId());

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                preparedStatement.setObject(i + 1, values.get(i));
            }
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }
}
