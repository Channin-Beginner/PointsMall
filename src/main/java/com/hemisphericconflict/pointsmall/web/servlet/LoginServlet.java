package com.hemisphericconflict.pointsmall.web.servlet;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import com.hemisphericconflict.pointsmall.service.UserService;
// 将导入语句从 javax 改为 jakarta
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;


// LoginServlet.java（改造后 - 完整版本）
@WebServlet("/loginPage")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserService();

    // 处理GET请求（显示登录页）
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/common/LoginInterface.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String identifier = request.getParameter("username");
        String password = request.getParameter("password");
        boolean success = false;
        String message = "登录失败";
        int role = 0; // 默认角色为0（普通用户）

        try {
            UserDB user = userService.login(identifier, password);
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("points", user.getTotalPoints());
                session.setAttribute("role", user.getRole());
                session.setAttribute("user", user); // 存储完整用户对象，供Filter使用

                success = true;
                message = "登录成功";
                role = user.getRole(); // 获取用户角色
                System.out.println("用户 " + user.getUsername() + " 登录成功");
            } else {
                message = "用户名或密码错误";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "登录过程发生错误，请重试";
        }

        // 返回包含角色信息的JSON
        out.println("{\"success\":" + success + ",\"message\":\"" + message + "\",\"role\":" + role + "}");
    }
}