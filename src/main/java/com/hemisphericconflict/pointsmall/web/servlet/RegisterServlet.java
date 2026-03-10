package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserService();

    // 添加doGet方法处理GET请求
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // 转发到注册页面
        req.getRequestDispatcher("/common/Register.jsp").forward(req, res);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 获取参数
        String username = request.getParameter("username");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String verifyCode = request.getParameter("verifyCode");

        // 调用Service完成注册
        String result = userService.register(username, phone, password);

        // 返回结果
        String json = "{\"success\":" + result.startsWith("注册成功") + ",\"message\":\"" + result + "\"}";
        out.println(json);
        out.close();
    }
}