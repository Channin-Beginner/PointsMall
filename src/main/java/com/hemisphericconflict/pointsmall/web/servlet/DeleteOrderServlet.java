package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.dao.OrderDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/deleteOrder")
public class DeleteOrderServlet extends HttpServlet {
    private final OrderDao orderDao = new OrderDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || !orderIdStr.matches("\\d+")) {
            response.getWriter().write("删除失败：订单ID必须为有效数字");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            boolean result = orderDao.deleteOrder(orderId);
            if (result) {
                response.getWriter().write("操作成功"); // 必须与前端匹配
            } else {
                response.getWriter().write("删除失败：订单不存在或已被删除");
            }
        } catch (Exception e) {
            response.getWriter().write("删除失败：系统异常，请重试");
            e.printStackTrace(); // 开发环境打印堆栈，生产环境可移除
        }
    }
}