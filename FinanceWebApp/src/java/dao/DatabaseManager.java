package dao;

import java.sql.*;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/sem5_se_fms";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // or org.postgresql.Driver for Supabase
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
