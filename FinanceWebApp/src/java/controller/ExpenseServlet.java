package controller;

import dao.AccountDao;
import dao.ExpenseDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import model.UserSession;

@WebServlet("/expense")
public class ExpenseServlet extends HttpServlet {

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
            req.setAttribute("expenses", ExpenseDao.listByUser(uid));
        } catch (SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/expense.jsp").forward(req, resp);
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
        String category = req.getParameter("expense_category");
        String amountStr = req.getParameter("amount");
        String dateStr = req.getParameter("expense_date");
        String remark = req.getParameter("remark");

        try {
            if (isBlank(accountType) || isBlank(category) || isBlank(amountStr) || isBlank(dateStr)) {
                req.setAttribute("error", "All fields required.");
                doGet(req, resp);
                return;
            }

            double amount = Double.parseDouble(amountStr.trim());
            java.sql.Date d = java.sql.Date.valueOf(dateStr.trim());

            Map<String, Object> acc = AccountDao.findByType(uid, accountType.trim());
            if (acc == null) {
                req.setAttribute("error", "Account not found.");
                doGet(req, resp);
                return;
            }
            Integer accountId = asInt(acc.get("account_id"));
            if (accountId == null) {
                req.setAttribute("error", "Account data missing.");
                doGet(req, resp);
                return;
            }

            // Compute available balance as of the chosen date (income up to date - expenses up to date)
            double available = AccountDao.balanceAsOf(accountId, d);

            if (available < amount) {
                req.setAttribute("error", "Insufficient funds as of the selected date.");
                doGet(req, resp);
                return;
            }

            ExpenseDao.insert(uid, accountId, d, category.trim(), remark == null ? null : remark.trim(), amount);
            req.setAttribute("success", "Expense recorded.");
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
        if (v instanceof String s && !s.isBlank()) {
            try {
                return Integer.valueOf(s.trim());
            } catch (Exception ignore) {
            }
        }
        return null;
    }
}
