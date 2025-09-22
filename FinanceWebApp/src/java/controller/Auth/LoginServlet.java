package controller.Auth;

import dao.DatabaseManager;
import model.UserSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password"); // ideally compare hashed

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT user_id, username, password FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getString("password").equals(password)) {
                        HttpSession session = req.getSession();
                        session.setAttribute("userSession",
                                new model.UserSession(rs.getInt("user_id"), rs.getString("username")));
                        resp.sendRedirect(req.getContextPath() + "/dashboard");
                        return;
                    }
                }
            }
            req.setAttribute("error", "Invalid credentials.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Login error: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
