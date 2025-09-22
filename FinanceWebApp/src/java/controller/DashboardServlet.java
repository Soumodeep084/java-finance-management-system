package controller;

import dao.AccountDao;
import dao.BudgetDao;
import dao.LiabilitiesDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import model.UserSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserSession us = (UserSession) req.getSession().getAttribute("userSession");
        int uid = us.getUserId();

        try {
            var accounts = AccountDao.listByUser(uid);
            double totalBalance = accounts.stream().mapToDouble(a -> (double) a.get("balance")).sum();
            double totalLiabilities = LiabilitiesDao.totalSum(uid);
            double monthIncome = BudgetDao.monthIncome(uid);
            double monthExpense = BudgetDao.monthExpense(uid);
            double target = BudgetDao.targetAmount(uid);
            double saved = monthIncome - monthExpense;
            int progress = (target > 0) ? (int) Math.round((saved / target) * 100.0) : 0;

            req.setAttribute("totalBalance", totalBalance);
            req.setAttribute("totalLiabilities", totalLiabilities);
            req.setAttribute("monthIncome", monthIncome);
            req.setAttribute("monthExpense", monthExpense);
            req.setAttribute("progress", progress);
            req.setAttribute("savedVsTarget", saved + "/" + target);

            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Failed to load dashboard: " + e.getMessage());
            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
        }
    }
}
