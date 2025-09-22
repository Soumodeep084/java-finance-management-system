package controller;

import dao.BudgetDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import model.UserSession;

@WebServlet("/budgets")
public class BudgetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int uid = ((UserSession) req.getSession().getAttribute("userSession")).getUserId();
        try {
            req.setAttribute("budgets", BudgetDao.listBudgets(uid));
        } catch (SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/budgets.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int uid = ((UserSession) req.getSession().getAttribute("userSession")).getUserId();
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                String category = req.getParameter("expense_category");
                double amount = Double.parseDouble(req.getParameter("amount"));
                BudgetDao.addBudget(uid, category, amount);
                req.setAttribute("success", "Budget added.");
            } else if ("delete".equals(action)) {
                String category = req.getParameter("expense_category");
                BudgetDao.deleteBudget(uid, category);
                req.setAttribute("success", "Budget deleted.");
            } else if ("target".equals(action)) {
                double amount = Double.parseDouble(req.getParameter("target_amount"));
                BudgetDao.upsertTarget(uid, amount);
                req.setAttribute("success", "Target saved.");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        doGet(req, resp);
    }
}
