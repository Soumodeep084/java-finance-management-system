package controller;

import dao.DatabaseManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import model.UserSession;

@WebServlet("/ChangeUsernameServlet")
public class ChangeUsernameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Object usObj = session.getAttribute("userSession");
        if (!(usObj instanceof UserSession)) {
            response.sendRedirect("login.jsp");
            return;
        }
        UserSession us = (UserSession) usObj;

        String newUsername = request.getParameter("newUsername");
        newUsername = newUsername == null ? "" : newUsername.trim();

        if (newUsername.isEmpty()) {
            request.setAttribute("error", "Username cannot be empty.");
            request.getRequestDispatcher("change-username.jsp").forward(request, response);
            return;
        }

        // Optional: short-circuit if unchanged
        if (newUsername.equals(us.getUsername())) {
            request.setAttribute("success", "Username updated successfully.");
            request.getRequestDispatcher("change-username.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET username=? WHERE id=?"; // use user id for reliability
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newUsername);
                ps.setInt(2, us.getUserId());
                int updated = ps.executeUpdate();

                if (updated > 0) {
                    // Refresh session with updated username
                    session.setAttribute("userSession", new UserSession(us.getUserId(), newUsername));
                    request.setAttribute("success", "Username updated successfully.");
                } else {
                    request.setAttribute("error", "Failed to update username.");
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error.");
        }

        request.getRequestDispatcher("change-username.jsp").forward(request, response);
    }
}
