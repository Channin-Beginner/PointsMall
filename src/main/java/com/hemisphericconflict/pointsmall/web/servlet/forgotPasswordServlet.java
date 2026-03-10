package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/password")
public class forgotPasswordServlet extends HttpServlet {
    private final UserService userService = new UserService();

    // 添加doGet方法处理GET请求
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // 转发到忘记密码页面
        req.getRequestDispatcher("/common/ForgotPassword.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();
        String action = req.getParameter("action");

        try {
            if ("sendCode".equals(action)) {
                handleSendCode(req, out);
            } else if ("reset".equals(action)) {
                handleResetPassword(req, out);
            } else {
                out.println("{\"success\":false,\"message\":\"无效的操作\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"success\":false,\"message\":\"服务器内部错误\"}");
        }
    }

    private void handleSendCode(HttpServletRequest req, PrintWriter out) {
        String identifier = req.getParameter("identifier");
        String type = req.getParameter("type");
        boolean isPhone = "phone".equals(type);

        String result = userService.sendVerificationCode(identifier, isPhone);
        out.println("{\"success\":" + (result.startsWith("验证码") ? "true" : "false") +
                ",\"message\":\"" + result + "\"}");
    }

    private void handleResetPassword(HttpServletRequest req, PrintWriter out) {
        String identifier = req.getParameter("identifier");
        String type = req.getParameter("type");
        String code = req.getParameter("code");
        String newPassword = req.getParameter("newPassword");
        boolean isPhone = "phone".equals(type);

        String result = userService.resetPassword(identifier, code, newPassword, isPhone);
        out.println("{\"success\":" + (result.equals("密码重置成功") ? "true" : "false") +
                ",\"message\":\"" + result + "\"}");
    }
}