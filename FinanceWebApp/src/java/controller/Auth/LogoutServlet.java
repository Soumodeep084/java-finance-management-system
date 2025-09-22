package controller.Auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/logoutServlet")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null) {
            s.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
