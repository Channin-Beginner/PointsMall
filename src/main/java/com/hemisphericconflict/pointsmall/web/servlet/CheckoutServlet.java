package com.hemisphericconflict.pointsmall.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hemisphericconflict.pointsmall.dao.CartDao;
import com.hemisphericconflict.pointsmall.dao.OrderDao;
import com.hemisphericconflict.pointsmall.dao.ProductDao;
import com.hemisphericconflict.pointsmall.entity.CartItemDB;
import com.hemisphericconflict.pointsmall.entity.OrderDB;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private final CartDao cartDao = new CartDao();
    private final OrderDao orderDao = new OrderDao();
    private final ProductDao productDao = new ProductDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            sendJsonResponse(response, false, "请先登录", null);
            return;
        }

        List<SelectedItem> selectedItems;
        try {
            // 从请求体中读取JSON参数
            selectedItems = objectMapper.readValue(
                    request.getReader(),
                    new TypeReference<List<SelectedItem>>() {}
            );
        } catch (IOException e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "参数解析失败", null);
            return;
        }

        try (Connection conn = c3p0Tool.getConnection()) {
            conn.setAutoCommit(false);

            List<Integer> removedProductIds = new ArrayList<>();
            for (SelectedItem item : selectedItems) {
                CartItemDB cartItem = cartDao.getCartItem(conn, userId, item.getProductId());
                if (cartItem == null || cartItem.getQuantity() != item.getQuantity()) {
                    conn.rollback();
                    sendJsonResponse(response, false, "商品数量已变更，请刷新购物车", null);
                    return;
                }

                ProductDB product = productDao.getProductById(conn, item.getProductId());
                if (product == null) {
                    conn.rollback();
                    sendJsonResponse(response, false, "商品不存在", null);
                    return;
                }

                OrderDB order = new OrderDB();
                order.setUserId(userId);
                order.setProductId(item.getProductId());
                order.setQuantity(item.getQuantity());
                order.setTotalPoints(product.getPointsPrice() * item.getQuantity());
                order.setStatus(1);
                order.setOrderTime(new java.util.Date());
                order.setAddress("默认地址");

                if (!orderDao.addOrder(conn, order)) {
                    conn.rollback();
                    sendJsonResponse(response, false, "订单创建失败", null);
                    return;
                }

                if (!cartDao.removeCartItem(conn, userId, item.getProductId())) {
                    conn.rollback();
                    sendJsonResponse(response, false, "购物车更新失败", null);
                    return;
                }

                removedProductIds.add(item.getProductId());
            }

            conn.commit();
            sendJsonResponse(response, true, "结算成功", removedProductIds);

        } catch (SQLException e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "系统异常，请重试", null);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, List<Integer> data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        if (data != null && !data.isEmpty()) {
            result.put("data", data);
        }
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    private static class SelectedItem {
        private Integer productId;
        private Integer quantity;

        public Integer getProductId() { return productId; }
        public void setProductId(Integer productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}