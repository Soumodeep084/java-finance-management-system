package dao;

import java.sql.*;
import java.util.*;

public class AccountDao {

    public static List<Map<String, Object>> listByUser(int userId) throws SQLException {
        String q = "SELECT account_id, account_type, balance FROM account WHERE user_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> res = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("account_id", rs.getInt("account_id"));
                    m.put("account_type", rs.getString("account_type"));
                    m.put("balance", rs.getDouble("balance"));
                    res.add(m);
                }
                return res;
            }
        }
    }

    public static boolean existsByName(int userId, String name) throws SQLException {
        String q = "SELECT 1 FROM account WHERE account_type=? AND user_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static void create(int userId, String name) throws SQLException {
        String q = "INSERT INTO account(account_type, balance, user_id) VALUES(?,0,?)";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public static void deleteByType(int userId, String name) throws SQLException {
        String q = "DELETE FROM account WHERE account_type=? AND user_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public static Map<String, Object> findByType(int userId, String name) throws SQLException {
        String q = "SELECT account_id, balance FROM account WHERE account_type=? AND user_id=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("account_id", rs.getInt("account_id"));
                    m.put("balance", rs.getDouble("balance"));
                    return m;
                }
                return null;
            }
        }
    }

    // Compute balance up to a date from ledger tables
    public static double balanceAsOf(int accountId, java.sql.Date asOf) throws SQLException {
        String incSql = "SELECT COALESCE(SUM(amount),0) FROM income WHERE account_id=? AND income_date<=?";
        String expSql = "SELECT COALESCE(SUM(amount),0) FROM expense WHERE account_id=? AND expense_date<=?";
        try (Connection c = DatabaseManager.getConnection()) {
            double inc = 0, exp = 0;
            try (PreparedStatement ps = c.prepareStatement(incSql)) {
                ps.setInt(1, accountId);
                ps.setDate(2, asOf);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getBigDecimal(1) != null) {
                        inc = rs.getBigDecimal(1).doubleValue();
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(expSql)) {
                ps.setInt(1, accountId);
                ps.setDate(2, asOf);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getBigDecimal(1) != null) {
                        exp = rs.getBigDecimal(1).doubleValue();
                    }
                }
            }
            return inc - exp;
        }
    }

//    public static void updateBalanceAndLiabilities(int accountId, double balance, double liabilities) throws SQLException {
//        String q = "UPDATE account SET balance=?, WHERE account_id=?";
//        try (Connection c = DatabaseManager.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
//            ps.setDouble(1, balance);
//            ps.setInt(2, accountId);
//            ps.executeUpdate();
//        }
//    }
    private AccountDao() {
    }
}
