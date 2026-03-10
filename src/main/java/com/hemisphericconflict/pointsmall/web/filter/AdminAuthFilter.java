package com.hemisphericconflict.pointsmall.web.filter;

import com.hemisphericconflict.pointsmall.entity.UserDB;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/admin/*") // 拦截所有/admin路径下的请求
public class AdminAuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // 1. 检查用户是否登录
        if (session == null || session.getAttribute("userId") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/loginPage"); // 未登录，重定向到登录页
            return;
        }

        // 2. 检查用户角色是否为管理员（role=1）
        UserDB user = (UserDB) session.getAttribute("user"); // 从Session获取用户对象（需在登录时存储）
        if (user == null || user.getRole() != 1) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/home"); // 非管理员，重定向到首页
            return;
        }

        // 3. 权限通过，继续访问
        chain.doFilter(request, response);
    }
}
