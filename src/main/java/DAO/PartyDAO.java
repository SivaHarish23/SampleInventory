package DAO;

import DTO.PartyDTO;
import Model.Party;
import Util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class PartyDAO<T extends Party> {

    protected abstract String getTableName();
    protected abstract T createEntityFromResultSet(ResultSet rs) throws SQLException;

    public T insertParty(T party) throws SQLException {
        String sql = "INSERT INTO " + getTableName() + " (name, location, phone_number, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, party.getName());
            ps.setString(2, party.getLocation());
            ps.setString(3,party.getPhoneNumber());
            ps.setLong(4, Instant.now().getEpochSecond());
            ps.setLong(5,Instant.now().getEpochSecond());
//            ResultSet rs = ps.executeQuery();
            return getResultRow(conn,ps,null);
        }
    }

    public List<T> getAllRows() throws SQLException {
        String sql = "SELECT * FROM " + getTableName();
        List<T> parties = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                parties.add(createEntityFromResultSet(rs));
            }
        }
        return parties;
    }

    public T getPartyById(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createEntityFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public T updateParty(T party) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE " + getTableName() + " SET ");
        List<Object> values = new ArrayList<>();

        if (party.getName() != null) {
            sql.append("name = ?, ");
            values.add(party.getName());
        }
        if (party.getLocation() != null) {
            sql.append("location = ?, ");
            values.add(party.getLocation());
        }
        if (party.getPhoneNumber() != null) {
            sql.append("phone_number = ?, ");
            values.add(party.getPhoneNumber());
        }

        if (values.isEmpty()) return null;

        sql.append("updated_at = ?, ");
        values.add(Instant.now().getEpochSecond());

        sql.setLength(sql.length() - 2); // trim last comma
        sql.append(" WHERE id = ?");
        int partyId = party.getId();
        values.add(partyId);

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++)
                ps.setObject(i + 1, values.get(i));

            return getResultRow(conn,ps,partyId);
        }
    }

    public boolean deleteParty(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private T getResultRow(Connection conn, PreparedStatement preparedStatement, Integer pid) throws SQLException {
        String fetch = "SELECT * FROM "+ getTableName() +" WHERE id = ?";

        if(preparedStatement.executeUpdate() > 0){
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next() && pid == null)
                    pid = generatedKeys.getInt(1);
            }

            try(PreparedStatement ps = conn.prepareStatement(fetch)){
                ps.setInt(1,pid);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()) return createEntityFromResultSet(rs);
                }
            }
        }
        return null;
    }


}