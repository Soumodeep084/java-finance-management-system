package controller.Auth;

import dao.DatabaseManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import model.UserSession;
import service.MailService;

@WebServlet("/DeleteAccountServlet")
public class DeleteAccountServlet extends HttpServlet {

    private MailService mailer;
    private String fromEmail = "customerfinance.support@wealthory.com";

    @Override
    public void init() throws ServletException {
        String user = System.getenv("MAILTRAP_USER");
        String pass = System.getenv("MAILTRAP_PASS");
        if (user == null) {
            user = getServletContext().getInitParameter("MAILTRAP_USER");
        }
        if (pass == null) {
            pass = getServletContext().getInitParameter("MAILTRAP_PASS");
        }

        if (user != null && pass != null) {
            mailer = MailService.usingMailtrap(user, pass); // or new MailService("smtp.gmail.com",587,user,pass)
        } else {
            getServletContext().log("DeleteAccountServlet: mail credentials not configured; OTP email disabled");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserSession us = (session != null) ? (UserSession) session.getAttribute("userSession") : null;
        if (us == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        session.setAttribute("otp", otp);
        session.setAttribute("otpExpiresAt", Instant.now().plusSeconds(10 * 60));

        String email = null;
        String name = null;
        String selectEmail = "SELECT name, email FROM users WHERE user_id=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(selectEmail)) {
            ps.setInt(1, us.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString(1);
                    email = rs.getString(2);
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Could not prepare deletion: " + e.getMessage());
            request.getRequestDispatcher("/delete-account.jsp").forward(request, response);
            return;
        }

        // Send email with OTP (best effort)
        if (mailer != null && email != null) {
            try {
                Map<String, String> data = new HashMap<>();
                data.put("name", name != null ? name : "there");
                data.put("otp", otp);
                data.put("minutes", "10");
                data.put("appName", "Wealthory");
                mailer.send(MailService.SendType.DELETE_CONFIRM, fromEmail, email, data);
                request.setAttribute("success", "OTP has been sent to " + email + ".");
            } catch (Exception ex) {
                getServletContext().log("Delete OTP email failed: " + ex.getMessage(), ex);
                request.setAttribute("error", "Failed to send OTP email. Try again.");
            }
        } else {
            request.setAttribute("error", "Email service not configured.");
        }

        request.getRequestDispatcher("/delete-account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserSession us = (session != null) ? (UserSession) session.getAttribute("userSession") : null;
        if (us == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        int userId = us.getUserId();

        String confirmText = val(request.getParameter("confirmText"));
        String otp = val(request.getParameter("otp"));
        if (!"Delete Account".equals(confirmText)) {
            request.setAttribute("error", "Type 'Delete Account' exactly to confirm.");
            request.getRequestDispatcher("/delete-account.jsp").forward(request, response);
            return;
        }

        String sessionOtp = (String) session.getAttribute("otp");
        Instant otpExpiresAt = (Instant) session.getAttribute("otpExpiresAt");
        boolean expired = (otpExpiresAt == null) || Instant.now().isAfter(otpExpiresAt);

        if (sessionOtp == null || otp == null || !otp.equals(sessionOtp) || expired) {
            session.removeAttribute("otp");
            session.removeAttribute("otpExpiresAt");
            request.setAttribute("error", "Invalid or expired OTP.");
            request.getRequestDispatcher("/delete-account.jsp").forward(request, response);
            return;
        }

        // Clear OTP before destructive action
        session.removeAttribute("otp");
        session.removeAttribute("otpExpiresAt");

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id=?")) {
                ps.setInt(1, userId);
                int count = ps.executeUpdate();
                if (count == 0) {
                    conn.rollback();
                    request.setAttribute("error", "User not found or already deleted.");
                    request.getRequestDispatcher("/delete-account.jsp").forward(request, response);
                    return;
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/delete-account.jsp").forward(request, response);
            return;
        }

        session.invalidate();
        response.sendRedirect(request.getContextPath() + "/signup.jsp?msg=account_deleted");
    }

    private static String val(String s) {
        return s == null ? null : s.trim();
    }
}
