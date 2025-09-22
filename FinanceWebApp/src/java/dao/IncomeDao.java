package dao;

import java.sql.*;
import java.util.*;
import java.sql.Date;

public class IncomeDao {

    public static void insert(int userId, int accountId, Date date, String source, double amount) throws SQLException {
        String q = "INSERT INTO income(user_id, account_id, income_date, income_source, amount) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setInt(2, accountId);
            ps.setDate(3, new java.sql.Date(date.getTime()));
            ps.setString(4, source);
            ps.setDouble(5, amount);
            ps.executeUpdate();
        }
    }

    public static List<Map<String, Object>> listByUser(int userId) throws SQLException {
        String q = "SELECT i.income_date, i.income_source, i.amount, a.account_type "
                + "FROM income i INNER JOIN account a ON i.account_id=a.account_id "
                + "WHERE i.user_id=? ORDER BY i.income_date DESC";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> out = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("income_date", rs.getDate("income_date"));
                    m.put("income_source", rs.getString("income_source"));
                    m.put("amount", rs.getDouble("amount"));
                    m.put("account_type", rs.getString("account_type"));
                    out.add(m);
                }
                return out;
            }
        }
    }

    private IncomeDao() {
    }
}
