package controller.Auth;

import dao.DatabaseManager;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import service.MailService;

@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {

    private MailService mailer;
    private String fromEmail = "customerfinance.support@wealthory.com";

    @Override
    public void init() throws ServletException {
        // Read from env or context params
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String reenter = request.getParameter("reenter-password");

        if (isBlank(name) || isBlank(username) || isBlank(email) || isBlank(password) || isBlank(reenter)) {
            request.setAttribute("error", "All fields are required!");
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
            return;
        }
        if (!password.equals(reenter)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
            return;
        }
        if (password.length() < 8 || !Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            request.setAttribute("error", "Password must be at least 8 characters and include a special character.");
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement chk = conn.prepareStatement("SELECT 1 FROM users WHERE username=?")) {
                chk.setString(1, username);
                try (ResultSet rs = chk.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("error", "Username already exists.");
                        request.getRequestDispatcher("/signup.jsp").forward(request, response);
                        return;
                    }
                }
            }

            String insert = "INSERT INTO users(name, email, username, password, created_at) VALUES(?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, username);
                ps.setString(4, password); // hash in production
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            }

            // Send welcome email (best effort; non-blocking on failure)
            if (mailer != null) {
                try {
                    Map<String, String> data = new HashMap<>();
                    data.put("name", name);
                    data.put("appName", "Wealthory");
                    mailer.send(MailService.SendType.REGISTRATION, fromEmail, email, data);
                } catch (MessagingException mailEx) {
                    // Log and continue; do not block signup if email fails
                    getServletContext().log("Signup email failed: " + mailEx.getMessage(), mailEx);
                }
            }

            request.setAttribute("success", "Account created! Please check email, then log in.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/signup.jsp").forward(request, response);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
