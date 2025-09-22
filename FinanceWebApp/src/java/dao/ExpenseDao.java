package dao;

import java.sql.*;
import java.util.*;
import java.sql.Date;

public class ExpenseDao {

    public static void insert(int userId, int accountId, Date date, String category, String remark, double amount)
            throws SQLException {
        String q = "INSERT INTO expense(user_id, account_id, expense_date, expense_category, remark, amount) VALUES (?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setInt(2, accountId);
            ps.setDate(3, new java.sql.Date(date.getTime()));
            ps.setString(4, category);
            ps.setString(5, remark);
            ps.setDouble(6, amount);
            ps.executeUpdate();
        }
    }

    public static List<Map<String, Object>> listByUser(int userId) throws SQLException {
        String q = "SELECT e.expense_date, e.expense_category, e.amount, e.remark, a.account_type "
                + "FROM expense e INNER JOIN account a ON e.account_id=a.account_id "
                + "WHERE e.user_id=? ORDER BY e.expense_date DESC";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> out = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("expense_date", rs.getDate("expense_date"));
                    m.put("expense_category", rs.getString("expense_category"));
                    m.put("amount", rs.getDouble("amount"));
                    m.put("remark", rs.getString("remark"));
                    m.put("account_type", rs.getString("account_type"));
                    out.add(m);
                }
                return out;
            }
        }
    }

    private ExpenseDao() {
    }
}
