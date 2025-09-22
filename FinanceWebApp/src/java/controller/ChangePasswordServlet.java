package controller;

import dao.DatabaseManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import model.UserSession;

@WebServlet("/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        Object obj = session.getAttribute("userSession");
        if (!(obj instanceof UserSession)) {
            response.sendRedirect("login.jsp");
            return;
        }
        UserSession us = (UserSession) obj;
        int userId = us.getUserId();

        String currentPassword = safe(request.getParameter("currentPassword"));
        String newPassword = safe(request.getParameter("newPassword"));
        String confirmPassword = safe(request.getParameter("confirmPassword"));

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }
        if (newPassword.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters.");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Fetch existing password (hash or plain, depending on your schema)
            String checkSql = "SELECT password FROM users WHERE user_id=?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        request.setAttribute("error", "User not found.");
                        request.getRequestDispatcher("change-password.jsp").forward(request, response);
                        return;
                    }
                    String dbPassword = rs.getString(1);

                    // TODO: If using hashing, verify with your password verifier.
                    // For now, plain comparison fallback if legacy:
                    if (!dbPassword.equals(currentPassword)) {
                        request.setAttribute("error", "Current password is incorrect.");
                        request.getRequestDispatcher("change-password.jsp").forward(request, response);
                        return;
                    }
                }
            }

            // Update to new password (replace with hashed value if using hashing)
            String updateSql = "UPDATE users SET password=? WHERE user_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newPassword);
                ps.setInt(2, userId);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    request.setAttribute("success", "Password updated successfully.");
                } else {
                    request.setAttribute("error", "Failed to update password.");
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error.");
        }

        request.getRequestDispatcher("change-password.jsp").forward(request, response);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
