package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class AuthGuard implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpSession session = r.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("userSession") != null;
        if (!loggedIn) {
            ((HttpServletResponse) res).sendRedirect(r.getContextPath() + "/login.jsp");
            return;
        }
        chain.doFilter(req, res);
    }
}
