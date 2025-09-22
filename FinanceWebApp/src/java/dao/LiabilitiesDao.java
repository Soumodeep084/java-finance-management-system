package dao;

import java.sql.*;
import java.util.*;

public class LiabilitiesDao {

    public static List<Map<String, Object>> listByUser(int userId) throws SQLException {
        String q = "SELECT liability_id, title, amount, due_date, status, notes "
                + "FROM liabilities WHERE user_id=? ORDER BY COALESCE(due_date, '9999-12-31') ASC, created_at DESC";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> out = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("liability_id", rs.getInt("liability_id"));
                    m.put("title", rs.getString("title"));
                    m.put("amount", rs.getDouble("amount"));
                    m.put("due_date", rs.getDate("due_date"));
                    m.put("status", rs.getString("status"));
                    m.put("notes", rs.getString("notes"));
                    out.add(m);
                }
                return out;
            }
        }
    }

    public static Map<String, Object> findById(int userId, int id) throws SQLException {
        String q = "SELECT liability_id, title, amount, due_date, status, notes FROM liabilities WHERE user_id=? AND liability_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("liability_id", rs.getInt("liability_id"));
                    m.put("title", rs.getString("title"));
                    m.put("amount", rs.getDouble("amount"));
                    m.put("due_date", rs.getDate("due_date"));
                    m.put("status", rs.getString("status"));
                    m.put("notes", rs.getString("notes"));
                    return m;
                }
                return null;
            }
        }
    }

    public static void create(int userId, String title, double amount, java.sql.Date dueDate, String status, String notes) throws SQLException {
        String q = "INSERT INTO liabilities(user_id, title, amount, due_date, status, notes) VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setDouble(3, amount);
            if (dueDate != null) {
                ps.setDate(4, dueDate);
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setString(5, status);
            if (notes != null && !notes.isBlank()) {
                ps.setString(6, notes);
            } else {
                ps.setNull(6, Types.VARCHAR);
            }
            ps.executeUpdate();
        }
    }

    public static void update(int userId, int id, String title, double amount, java.sql.Date dueDate, String status, String notes) throws SQLException {
        String q = "UPDATE liabilities SET title=?, amount=?, due_date=?, status=?, notes=? WHERE user_id=? AND liability_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, title);
            ps.setDouble(2, amount);
            if (dueDate != null) {
                ps.setDate(3, dueDate);
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setString(4, status);
            if (notes != null && !notes.isBlank()) {
                ps.setString(5, notes);
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            ps.setInt(6, userId);
            ps.setInt(7, id);
            ps.executeUpdate();
        }
    }

    public static void delete(int userId, int id) throws SQLException {
        String q = "DELETE FROM liabilities WHERE user_id=? AND liability_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static double totalSum(int userId) throws SQLException {
        String q = "SELECT COALESCE(SUM(amount), 0) FROM liabilities WHERE user_id = ?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
                return 0.0;
            }
        }
    }

    private LiabilitiesDao() {
    }
}
