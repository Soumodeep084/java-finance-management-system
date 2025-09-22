package controller.Auth;

import dao.DatabaseManager;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.Map;
import service.MailService;

@WebServlet("/ForgotPassword")
public class ForgotPasswordServlet extends HttpServlet {

    private MailService mail;
    private String fromEmail = "customerfinance.support@wealthory.com";

    @Override
    public void init() {
        ServletContext ctx = getServletContext();
        String user = ctx.getInitParameter("MAILTRAP_USER");
        String pass = ctx.getInitParameter("MAILTRAP_PASS");
        System.out.println("MAILTRAP_USER=" + user);
        System.out.println("MAILTRAP_PASS set? " + (pass != null && !pass.isBlank()));
        if (user == null) {
            user = "api";
        }
        if (pass == null) {
            pass = "CHANGE_ME";
        }
        mail = MailService.usingMailtrap(user, pass);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("phase", "request");
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = val(request.getParameter("action"));
        HttpSession session = request.getSession(true);

        if ("request".equals(action)) {
            String identity = val(request.getParameter("identity"));
            if (identity == null || identity.isEmpty()) {
                request.setAttribute("error", "Enter registered email or username.");
                request.setAttribute("phase", "request");
                forward(request, response);
                return;
            }

            Integer userId = null;
            String email = null;

            try (Connection conn = DatabaseManager.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT user_id, email FROM users WHERE email=?")) {
                    ps.setString(1, identity);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            userId = rs.getInt("user_id");
                            email = rs.getString("email");
                        }
                    }
                }
                if (userId == null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT user_id, email FROM users WHERE username=?")) {
                        ps.setString(1, identity);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                userId = rs.getInt("user_id");
                                email = rs.getString("email");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                request.setAttribute("error", "Database error: " + e.getMessage());
                request.setAttribute("phase", "request");
                forward(request, response);
                return;
            }

            if (userId == null) {
                request.setAttribute("error", "No account found for the provided email/username.");
                request.setAttribute("phase", "request");
                forward(request, response);
                return;
            }

            String otp = MailService.generateOtp6();
            session.setAttribute("fp_user_id", userId);
            session.setAttribute("fp_otp", otp);
            session.setAttribute("fp_expires", Instant.now().plusSeconds(10 * 60));

            try {
                // Preferred: stable From + user To
                mail.send(MailService.SendType.FORGOT_OTP, fromEmail, email,
                        Map.of("otp", otp, "minutes", "10", "appName", "Wealthory"));
                request.setAttribute("success",
                        "OTP sent to " + (email != null ? email : "your email") + ". Complete the reset password process on the same device and browser.");
                request.setAttribute("phase", "verify");
            } catch (MessagingException ex) { // optional: replace with logger in production
                // optional: replace with logger in production

                request.setAttribute("error", "Failed to send OTP. Try again." + ex.getMessage());
                request.setAttribute("phase", "request");
            }

            forward(request, response);
            return;
        }

        if ("resend".equals(action)) {
            Integer uid = (Integer) session.getAttribute("fp_user_id");
            if (uid == null) {
                request.setAttribute("error", "Start reset again.");
                request.setAttribute("phase", "request");
                forward(request, response);
                return;
            }

            String email = null;
            try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT email FROM users WHERE user_id=?")) {
                ps.setInt(1, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        email = rs.getString(1);
                    }
                }
            } catch (SQLException e) {
                request.setAttribute("error", "Database error: " + e.getMessage());
                request.setAttribute("phase", "verify");
                forward(request, response);
                return;
            }

            String otp = MailService.generateOtp6();
            session.setAttribute("fp_otp", otp);
            session.setAttribute("fp_expires", Instant.now().plusSeconds(10 * 60));

            try {
                mail.send(MailService.SendType.FORGOT_OTP, fromEmail, email,
                        Map.of("otp", otp, "minutes", "10", "appName", "Wealthory"));
                request.setAttribute("success",
                        "OTP resent. Continue on the same device and browser that requested it.");
            } catch (MessagingException ex) { // optional
                // optional
                request.setAttribute("error", "Failed to resend OTP.");
            }
            request.setAttribute("phase", "verify");
            forward(request, response);
            return;
        }

        if ("reset".equals(action)) {
            Integer uid = (Integer) session.getAttribute("fp_user_id");
            String sessionOtp = (String) session.getAttribute("fp_otp");
            Instant exp = (Instant) session.getAttribute("fp_expires");

            String otp = val(request.getParameter("otp"));
            String newPass = val(request.getParameter("newPassword"));
            String confirm = val(request.getParameter("confirmPassword"));

            if (uid == null || sessionOtp == null || exp == null || Instant.now().isAfter(exp)) {
                request.setAttribute("error", "OTP expired or session lost. Request a new OTP from the same device.");
                clearSessionKeys(session);
                request.setAttribute("phase", "request");
                forward(request, response);
                return;
            }
            if (otp == null || !otp.equals(sessionOtp)) {
                request.setAttribute("error", "Invalid OTP. Ensure you are using the same device and browser.");
                request.setAttribute("phase", "verify");
                forward(request, response);
                return;
            }
            if (newPass == null || newPass.length() < 8 || !newPass.equals(confirm)) {
                request.setAttribute("error", "Passwords must match and be at least 8 characters.");
                request.setAttribute("phase", "verify");
                forward(request, response);
                return;
            }

            try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE users SET password=? WHERE user_id=?")) {
                ps.setString(1, newPass); // TODO: replace with your hashing
                ps.setInt(2, uid);
                int n = ps.executeUpdate();
                if (n == 0) {
                    request.setAttribute("error", "User not found.");
                    request.setAttribute("phase", "request");
                } else {
                    clearSessionKeys(session);
                    request.setAttribute("success", "Password reset successful. Please log in.");
                    request.setAttribute("phase", "request");
                }
            } catch (SQLException e) {
                request.setAttribute("error", "Database error: " + e.getMessage());
                request.setAttribute("phase", "verify");
            }
            forward(request, response);
            return;
        }

        request.setAttribute("phase", "request");
        forward(request, response);
    }

    private static String val(String s) {
        return s == null ? null : s.trim();
    }

    private static void clearSessionKeys(HttpSession s) {
        s.removeAttribute("fp_user_id");
        s.removeAttribute("fp_otp");
        s.removeAttribute("fp_expires");
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
    }
}
