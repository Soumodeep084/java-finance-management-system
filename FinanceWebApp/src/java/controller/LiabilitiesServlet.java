package controller;

import dao.LiabilitiesDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/liabilities")
public class LiabilitiesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int uid = ((model.UserSession) req.getSession().getAttribute("userSession")).getUserId();
        try {
            req.setAttribute("liabilities", LiabilitiesDao.listByUser(uid));
        } catch (SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/liabilities.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int uid = ((model.UserSession) req.getSession().getAttribute("userSession")).getUserId();
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                String title = req.getParameter("title");
                String amountStr = req.getParameter("amount");
                String due = req.getParameter("due_date");
                String status = req.getParameter("status");
                String notes = req.getParameter("notes");
                if (title == null || title.isBlank() || amountStr == null) {
                    req.setAttribute("error", "Title and amount are required.");
                } else {
                    double amount = Double.parseDouble(amountStr);
                    java.sql.Date dueDate = (due == null || due.isBlank()) ? null : java.sql.Date.valueOf(due);
                    if (status == null || status.isBlank()) {
                        status = "ACTIVE";
                    }
                    LiabilitiesDao.create(uid, title.trim(), amount, dueDate, status, notes);
                    req.setAttribute("success", "Liability added.");
                }
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(req.getParameter("liability_id"));
                String title = req.getParameter("title");
                double amount = Double.parseDouble(req.getParameter("amount"));
                String due = req.getParameter("due_date");
                String status = req.getParameter("status");
                String notes = req.getParameter("notes");
                java.sql.Date dueDate = (due == null || due.isBlank()) ? null : java.sql.Date.valueOf(due);
                if (status == null || status.isBlank()) {
                    status = "ACTIVE";
                }
                LiabilitiesDao.update(uid, id, title.trim(), amount, dueDate, status, notes);
                req.setAttribute("success", "Liability updated.");
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("liability_id"));
                LiabilitiesDao.delete(uid, id);
                req.setAttribute("success", "Liability deleted.");
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid numeric value.");
        } catch (SQLException e) {
            req.setAttribute("error", e.getMessage());
        }
        doGet(req, resp);
    }
}
