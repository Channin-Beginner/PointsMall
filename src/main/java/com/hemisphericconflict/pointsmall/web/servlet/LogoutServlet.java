package com.hemisphericconflict.pointsmall.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 销毁会话
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 重定向到登录页
        response.sendRedirect(request.getContextPath() + "/loginPage");
    }
}