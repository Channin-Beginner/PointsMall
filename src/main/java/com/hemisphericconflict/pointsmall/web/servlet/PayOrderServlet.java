package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.dao.OrderDao;
import com.hemisphericconflict.pointsmall.dao.PointsLogDao;
import com.hemisphericconflict.pointsmall.dao.UserDao;
import com.hemisphericconflict.pointsmall.entity.OrderDB;
import com.hemisphericconflict.pointsmall.entity.PointsLogDB;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;

// PayOrderServlet.java
@WebServlet("/payOrder")
public class PayOrderServlet extends HttpServlet {
    private final OrderDao orderDao = new OrderDao();
    private final UserDao userDao = new UserDao();
    private final PointsLogDao pointsLogDao = new PointsLogDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || !orderIdStr.matches("\\d+")) {
            response.getWriter().write("付款失败：无效的订单ID");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            OrderDB order = orderDao.getOrderById(orderId);

            UserDB user = userDao.findById(order.getUserId());
            int totalPoints = order.getTotalPoints();

            // 检查积分是否足够
            if (user.getTotalPoints() < totalPoints) {
                response.getWriter().write("付款失败：积分不足");
                return;
            }

            if (order == null || order.getStatus() != 1) {
                response.getWriter().write("付款失败：订单状态异常");
                return;
            }

            // 计算新积分并更新
            int newPoints = user.getTotalPoints() - totalPoints;
            boolean isPointsUpdated = userDao.updateUserPoints(user.getUserId(), newPoints);
            if (!isPointsUpdated) {
                response.getWriter().write("付款失败：积分更新失败");
                return;
            }

            // 更新订单状态
            boolean isOrderUpdated = orderDao.updateOrderStatus(orderId, 2);
            if (!isOrderUpdated) {
                // 回滚积分操作（实际生产环境建议使用事务）
                userDao.updateUserPoints(user.getUserId(), user.getTotalPoints());
                response.getWriter().write("付款失败：订单状态更新失败");
                return;
            }

            // 记录积分明细
            PointsLogDB log = new PointsLogDB();
            log.setUserId(user.getUserId());
            log.setChangePoints(-totalPoints);
            log.setCreateTime(new Date());
            pointsLogDao.addPointsLog(log);

            response.getWriter().write("付款成功");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("付款失败：系统异常");
        }
    }
}
