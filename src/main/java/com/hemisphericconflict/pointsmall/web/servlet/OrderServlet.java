package com.hemisphericconflict.pointsmall.web.servlet;
import jakarta.servlet.http.HttpServletResponse;
import com.hemisphericconflict.pointsmall.dao.OrderDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

// OrderServlet.java
@WebServlet("/updateOrderStatus")
public class OrderServlet extends HttpServlet {
    private final OrderDao orderDao = new OrderDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderId = request.getParameter("orderId");
        String statusStr = request.getParameter("status");

        System.out.println("接收到的 orderId: " + orderId); // 调试时打印参数值

        // 校验orderId非空且为有效数字
        if (orderId == null || orderId.isEmpty()) {
            response.getWriter().write("订单ID无效");
            return;
        }

        // 校验status非空且为有效数字
        if (statusStr == null || statusStr.isEmpty() || !statusStr.matches("\\d+")) {
            response.getWriter().write("状态参数无效");
            return;
        }

        try {
            int status = Integer.parseInt(statusStr);
            if (orderDao.updateOrderStatus(Integer.parseInt(orderId), status)) {
                response.getWriter().write("订单状态更新成功");
            } else {
                response.getWriter().write("订单状态更新失败");
            }
        } catch (Exception e) {
            response.getWriter().write("操作失败：" + e.getMessage());
        }
    }
}