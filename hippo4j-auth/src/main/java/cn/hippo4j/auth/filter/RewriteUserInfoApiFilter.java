package cn.hippo4j.auth.filter;

import cn.hippo4j.auth.toolkit.AuthUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * When anonymous login is enabled, an error will be reported when viewing the current user information.
 * Modify the URI to query the default administrator information.
 *
 * before:hippo4j/v1/cs/auth/users/info or hippo4j/v1/cs/auth/users/info/xxx
 * after:hippo4j/v1/cs/auth/users/info/admin
 */
public class RewriteUserInfoApiFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        boolean enableAuthentication = AuthUtil.enableAuthentication;
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String path = httpRequest.getRequestURI();
        if (!enableAuthentication && path.contains("users/info")) {
            httpRequest.getRequestDispatcher("/hippo4j/v1/cs/auth/users/info/admin").forward(servletRequest, servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}