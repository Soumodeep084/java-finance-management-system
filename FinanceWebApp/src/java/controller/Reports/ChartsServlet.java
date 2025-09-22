package controller.Reports;

import dao.DatabaseManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import model.UserSession;

@WebServlet("/charts")
public class ChartsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = resolveUserId(session);

        if (userId == null) {
            request.setAttribute("error", "Please log in to view charts.");
            request.setAttribute("lineData", "{\"labels\":[],\"datasets\":[{\"label\":\"Income\",\"data\":[]},{\"label\":\"Expenses\",\"data\":[]}]}");
            request.setAttribute("incomePie", "{\"labels\":[],\"data\":[]}");
            request.setAttribute("expensePie", "{\"labels\":[],\"data\":[]}");
            request.getRequestDispatcher("/charts.jsp").forward(request, response);
            return;
        }

        YearMonth now = YearMonth.now();
        List<YearMonth> months = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            months.add(now.minusMonths(i));
        }

        Map<YearMonth, Double> incomeByMonth = new LinkedHashMap<>();
        Map<YearMonth, Double> expenseByMonth = new LinkedHashMap<>();
        for (YearMonth ym : months) {
            incomeByMonth.put(ym, 0.0);
            expenseByMonth.put(ym, 0.0);
        }

        Map<String, Double> incomeByCategory = new LinkedHashMap<>();
        Map<String, Double> expenseByCategory = new LinkedHashMap<>();

        LocalDate start = months.get(0).atDay(1);
        LocalDate end = months.get(months.size() - 1).atEndOfMonth();

        try (Connection conn = DatabaseManager.getConnection()) {
            // Monthly income: table income, date column = income_date
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT YEAR(income_date) y, MONTH(income_date) m, SUM(amount) "
                    + "FROM income WHERE user_id=? AND income_date BETWEEN ? AND ? "
                    + "GROUP BY y,m ORDER BY y,m")) {
                ps.setInt(1, userId);
                ps.setDate(2, java.sql.Date.valueOf(start));
                ps.setDate(3, java.sql.Date.valueOf(end));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        YearMonth ym = YearMonth.of(rs.getInt(1), rs.getInt(2));
                        double sum = rs.getBigDecimal(3) == null ? 0.0 : rs.getBigDecimal(3).doubleValue();
                        if (incomeByMonth.containsKey(ym)) {
                            incomeByMonth.put(ym, sum);
                        }
                    }
                }
            }

            // Monthly expenses: table expense (not expenses), date column = expense_date
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT YEAR(expense_date) y, MONTH(expense_date) m, SUM(amount) "
                    + "FROM expense WHERE user_id=? AND expense_date BETWEEN ? AND ? "
                    + "GROUP BY y,m ORDER BY y,m")) {
                ps.setInt(1, userId);
                ps.setDate(2, java.sql.Date.valueOf(start));
                ps.setDate(3, java.sql.Date.valueOf(end));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        YearMonth ym = YearMonth.of(rs.getInt(1), rs.getInt(2));
                        double sum = rs.getBigDecimal(3) == null ? 0.0 : rs.getBigDecimal(3).doubleValue();
                        if (expenseByMonth.containsKey(ym)) {
                            expenseByMonth.put(ym, sum);
                        }
                    }
                }
            }

            // Income by category: use income_source as the category over last 90 days
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(income_source,'Uncategorized') cat, SUM(amount) "
                    + "FROM income WHERE user_id=? AND income_date >= ? "
                    + "GROUP BY cat ORDER BY SUM(amount) DESC")) {
                ps.setInt(1, userId);
                ps.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(90)));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String cat = rs.getString(1);
                        double sum = rs.getBigDecimal(2) == null ? 0.0 : rs.getBigDecimal(2).doubleValue();
                        incomeByCategory.put(cat, sum);
                    }
                }
            }

            // Expense by category: use expense_category over last 90 days
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(expense_category,'Uncategorized') cat, SUM(amount) "
                    + "FROM expense WHERE user_id=? AND expense_date >= ? "
                    + "GROUP BY cat ORDER BY SUM(amount) DESC")) {
                ps.setInt(1, userId);
                ps.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(90)));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String cat = rs.getString(1);
                        double sum = rs.getBigDecimal(2) == null ? 0.0 : rs.getBigDecimal(2).doubleValue();
                        expenseByCategory.put(cat, sum);
                    }
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
        }

        request.setAttribute("lineData", buildLineJson(months, incomeByMonth, expenseByMonth));
        request.setAttribute("incomePie", buildPieJson(incomeByCategory));
        request.setAttribute("expensePie", buildPieJson(expenseByCategory));
        request.getRequestDispatcher("/charts.jsp").forward(request, response);
    }

    private static Integer resolveUserId(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object obj = session.getAttribute("userSession");
        if (obj instanceof UserSession us) {
            return us.getUserId();
        }
        Object[] c = {
            session.getAttribute("user_id"),
            session.getAttribute("userId"),
            session.getAttribute("uid"),
            session.getAttribute("USER_ID")
        };
        for (Object v : c) {
            if (v instanceof Integer i) {
                return i;
            }
            if (v instanceof Long l) {
                return l.intValue();
            }
            if (v instanceof String s) try {
                return Integer.valueOf(s);
            } catch (NumberFormatException ignore) {
            }
        }
        return null;
    }

    private static String buildLineJson(List<YearMonth> months,
            Map<YearMonth, Double> inc,
            Map<YearMonth, Double> exp) {
        StringBuilder labels = new StringBuilder("[");
        StringBuilder iVals = new StringBuilder("[");
        StringBuilder eVals = new StringBuilder("[");
        for (int i = 0; i < months.size(); i++) {
            YearMonth ym = months.get(i);
            labels.append("\"").append(ym.getMonth().toString().substring(0, 3)).append(" ").append(ym.getYear()).append("\"");
            iVals.append(String.format(java.util.Locale.US, "%.2f", inc.get(ym)));
            eVals.append(String.format(java.util.Locale.US, "%.2f", exp.get(ym)));
            if (i < months.size() - 1) {
                labels.append(",");
                iVals.append(",");
                eVals.append(",");
            }
        }
        labels.append("]");
        iVals.append("]");
        eVals.append("]");
        return "{\"labels\":" + labels + ",\"datasets\":[{\"label\":\"Income\",\"data\":" + iVals + "},{\"label\":\"Expenses\",\"data\":" + eVals + "}]}";
    }

    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        // proper escaping for JSON strings (backslash then quote)
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String buildPieJson(Map<String, Double> map) {
        StringBuilder labs = new StringBuilder("[");
        StringBuilder vals = new StringBuilder("[");
        int i = 0, n = map.size();
        for (Map.Entry<String, Double> e : map.entrySet()) {
            labs.append("\"").append(esc(e.getKey())).append("\"");
            vals.append(String.format(java.util.Locale.US, "%.2f", e.getValue()));
            if (++i < n) {
                labs.append(",");
                vals.append(",");
            }
        }
        labs.append("]");
        vals.append("]");
        return "{\"labels\":" + labs + ",\"data\":" + vals + "}";
    }
}
