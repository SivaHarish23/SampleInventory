package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private final String URL = "jdbc:mysql://localhost:3306/SampleInventory";
    private final String USER = "root";
    private final String PASSWORD = "root1234";

    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8 driver class
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
