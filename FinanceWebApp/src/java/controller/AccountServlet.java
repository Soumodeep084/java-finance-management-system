package controller;

import dao.AccountDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import model.UserSession;

@WebServlet("/accounts")
public class AccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int uid = ((UserSession) req.getSession().getAttribute("userSession")).getUserId();
        try {
            req.setAttribute("accounts", AccountDao.listByUser(uid));
        } catch (SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/accounts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int uid = ((UserSession) req.getSession().getAttribute("userSession")).getUserId();
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                String name = req.getParameter("account_type");
                if (name == null || name.isBlank()) {
                    req.setAttribute("error", "Account name required.");
                } else if (AccountDao.existsByName(uid, name)) {
                    req.setAttribute("error", "Account already exists.");
                } else {
                    AccountDao.create(uid, name);
                    req.setAttribute("success", "Account created.");
                }
            } else if ("delete".equals(action)) {
                String name = req.getParameter("account_type");
                AccountDao.deleteByType(uid, name);
                req.setAttribute("success", "Account deleted.");
            }
        } catch (SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        doGet(req, resp);
    }
}
