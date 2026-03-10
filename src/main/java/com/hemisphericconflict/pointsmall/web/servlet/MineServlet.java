package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.entity.OrderDB;
import com.hemisphericconflict.pointsmall.entity.PointsLogDB;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import com.hemisphericconflict.pointsmall.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/mine")
public class MineServlet extends HttpServlet {
    private final UserService userService = new UserService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); // 从Session获取用户ID

        if (userId == null) {
            response.sendRedirect("/PointsMall/loginPage"); // 未登录重定向
            return;
        }

        // 获取用户信息（含总积分）
        UserDB user = userService.getUserById(userId);
        request.setAttribute("user", user);

        // 获取订单列表
        List<OrderDB> orders = userService.getOrdersByUserId(userId);
        // 过滤掉状态为5的订单（假设状态5为已取消）
        orders.removeIf(order -> order.getStatus() == 5); // Java 8+ 流式过滤
        request.setAttribute("orders", orders);

        // 获取积分变动记录
        List<PointsLogDB> pointsLogs = userService.getPointsLogByUserId(userId);
        request.setAttribute("pointsLogs", pointsLogs);

        // 转发到Mine.jsp
        request.getRequestDispatcher("/user/Mine.jsp").forward(request, response);
    }
}