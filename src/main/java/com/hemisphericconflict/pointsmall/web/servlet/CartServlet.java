package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.service.CartService;
import com.hemisphericconflict.pointsmall.entity.CartItemDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    // 直接实例化合并后的CartService
    private final CartService cartService = new CartService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("CartServlet.doGet 被调用"); // 新增日志
        HttpSession session = request.getSession(false);

        // 详细会话调试信息
        if(session == null) {
            System.err.println("[错误] 会话不存在");
        } else {
            System.out.println("会话ID: " + session.getId());
            System.out.println("会话中所有属性: ");
            session.getAttributeNames().asIterator()
                    .forEachRemaining(name ->
                            System.out.println(" - " + name + ": " + session.getAttribute(name))
                    );
        }

        Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;

        if (userId == null) {
            System.err.println("[错误] 用户未登录，重定向到登录页");
            response.sendRedirect("/PointsMall/loginPage");
            return;
        }


        // 直接调用CartService获取购物车数据
        List<CartItemDB> cartItems = cartService.getUserCart(userId);
        request.setAttribute("cartItems", cartItems);
        request.getRequestDispatcher("/user/Shoppingcart.jsp").forward(request, response);

        System.out.println("用户ID: " + userId + " 的购物车项数: " + cartItems.size());
        // 添加空购物车提示属性
        request.setAttribute("isEmptyCart", cartItems.isEmpty());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            sendJsonError(response, "请先登录");
            return;
        }

        String action = request.getParameter("action");
        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");

        if (productIdStr == null || !productIdStr.matches("\\d+")) {
            sendJsonError(response, "无效的商品ID");
            return;
        }

        int productId = Integer.parseInt(productIdStr);

        try {
            switch (action) {
                case "add":
                    int quantity = quantityStr != null ? Integer.parseInt(quantityStr) : 1;
                    // 直接调用CartService的添加方法
                    cartService.addToCart(userId, productId, quantity);
                    break;

                case "remove":
                    // 直接调用CartService的移除方法
                    cartService.removeFromCart(userId, productId);
                    break;

                case "update":
                    if (quantityStr == null) {
                        sendJsonError(response, "缺少数量参数");
                        return;
                    }
                    // 直接调用CartService的更新方法
                    cartService.updateQuantity(userId, productId, Integer.parseInt(quantityStr));
                    break;

                default:
                    sendJsonError(response, "无效的操作类型");
                    return;
            }

            response.setContentType("application/json");
            response.getWriter().write("{\"success\": true}");

        } catch (Exception e) {
            sendJsonError(response, "操作失败: " + e.getMessage());
        }
    }

    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
    }
}