package com.shopsphere.filter;

import com.shopsphere.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter that protects customer-only endpoints, redirecting unauthenticated
 * visitors to the login page while preserving the return target URL.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
        "/profile", "/profile.jsp",
        "/checkout", "/checkout.jsp",
        "/orders", "/orders.jsp",
        "/order-details", "/order-details.jsp",
        "/wishlist", "/wishlist.jsp",
        "/add-review"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            // Save attempted request URL
            String uri = req.getRequestURI();
            String query = req.getQueryString();
            String returnUrl = (query != null) ? (uri + "?" + query) : uri;

            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("returnUrl", returnUrl);

            res.sendRedirect(req.getContextPath() + "/login.jsp?error=auth_required");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
