package controller;

import dao.AccountDao;
import dao.IncomeDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import model.UserSession;

@WebServlet("/income")
public class IncomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserSession us = (session != null) ? (UserSession) session.getAttribute("userSession") : null;
        if (us == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        int uid = us.getUserId();

        try {
            req.setAttribute("accounts", AccountDao.listByUser(uid));
            req.setAttribute("incomes", IncomeDao.listByUser(uid));
        } catch (SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/income.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        UserSession us = (session != null) ? (UserSession) session.getAttribute("userSession") : null;
        if (us == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        int uid = us.getUserId();

        String accountType = req.getParameter("account_type");
        String source = req.getParameter("income_source");
        String amountStr = req.getParameter("amount");
        String dateStr = req.getParameter("income_date");

        try {
            if (isBlank(accountType) || isBlank(source) || isBlank(amountStr) || isBlank(dateStr)) {
                req.setAttribute("error", "All fields required.");
                doGet(req, resp);
                return;
            }

            double amount = Double.parseDouble(amountStr.trim());
            java.sql.Date d = java.sql.Date.valueOf(dateStr.trim());

            // resolve account id and current balance
            Map<String, Object> acc = AccountDao.findByType(uid, accountType.trim());
            if (acc == null) {
                req.setAttribute("error", "Account not found.");
                doGet(req, resp);
                return;
            }

            Integer accountId = asInt(acc.get("account_id"));
            Double balance = asDouble(acc.get("balance"));
            Double liabilities = asDouble(acc.get("liabilities"));

            if (accountId == null) {
                req.setAttribute("error", "Account data missing (account_id).");
                doGet(req, resp);
                return;
            }
            if (balance == null) {
                balance = 0.0;
            }
            if (liabilities == null) {
                liabilities = 0.0;
            }

            // add income row
            IncomeDao.insert(uid, accountId, d, source.trim(), amount);

            req.setAttribute("success", "Income recorded.");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid amount.");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Invalid date.");
        } catch (SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        doGet(req, resp);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static Integer asInt(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Integer i) {
            return i;
        }
        if (v instanceof Long l) {
            return l.intValue();
        }
        if (v instanceof Number n) {
            return n.intValue();
        }
        if (v instanceof String s && !s.isBlank()) try {
            return Integer.valueOf(s.trim());
        } catch (Exception ignore) {
        }
        return null;
    }

    private static Double asDouble(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Double d) {
            return d;
        }
        if (v instanceof Float f) {
            return (double) f;
        }
        if (v instanceof Number n) {
            return n.doubleValue();
        }
        if (v instanceof String s && !s.isBlank()) try {
            return Double.valueOf(s.trim());
        } catch (Exception ignore) {
        }
        return null;
    }
}
