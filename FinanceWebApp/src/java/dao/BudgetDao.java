package dao;

import java.sql.*;
import java.util.*;

public class BudgetDao {

    public static void upsertTarget(int userId, double amount) throws SQLException {
        String check = "SELECT COUNT(*) FROM target_amount WHERE user_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(check)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int count = rs.getInt(1);
                if (count > 0) {
                    try (PreparedStatement up = c.prepareStatement("UPDATE target_amount SET amount=? WHERE user_id=?")) {
                        up.setDouble(1, amount);
                        up.setInt(2, userId);
                        up.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ins = c.prepareStatement("INSERT INTO target_amount(user_id, amount) VALUES(?,?)")) {
                        ins.setInt(1, userId);
                        ins.setDouble(2, amount);
                        ins.executeUpdate();
                    }
                }
            }
        }
    }

    public static void addBudget(int userId, String category, double amount) throws SQLException {
        String q = "INSERT INTO budget(user_id, expense_category, amount) VALUES(?,?,?)";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setString(2, category);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        }
    }

    public static void deleteBudget(int userId, String category) throws SQLException {
        String q = "DELETE FROM budget WHERE user_id=? AND expense_category=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setString(2, category);
            ps.executeUpdate();
        }
    }

    public static List<Map<String, Object>> listBudgets(int userId) throws SQLException {
        String q = "SELECT expense_category, amount FROM budget WHERE user_id=? ORDER BY expense_category";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> out = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("expense_category", rs.getString("expense_category"));
                    m.put("amount", rs.getDouble("amount"));
                    out.add(m);
                }
                return out;
            }
        }
    }

    public static double monthIncome(int userId) throws SQLException {
        String q = "SELECT COALESCE(SUM(amount),0) FROM income WHERE user_id=? AND MONTH(income_date)=MONTH(CURRENT_DATE()) AND YEAR(income_date)=YEAR(CURRENT_DATE())";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        }
    }

    public static double monthExpense(int userId) throws SQLException {
        String q = "SELECT COALESCE(SUM(amount),0) FROM expense WHERE user_id=? AND MONTH(expense_date)=MONTH(CURRENT_DATE()) AND YEAR(expense_date)=YEAR(CURRENT_DATE())";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        }
    }

    public static Double targetAmount(int userId) throws SQLException {
        String q = "SELECT amount FROM target_amount WHERE user_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    private BudgetDao() {
    }
}
