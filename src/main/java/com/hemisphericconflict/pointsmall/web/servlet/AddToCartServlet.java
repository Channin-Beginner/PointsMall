package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.service.CartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/addToCart")
public class AddToCartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 获取当前用户ID
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if(userId == null) {
            sendJsonResponse(response, false, "用户未登录");
            return;
        }

        // 解析请求数据
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        JSONObject json = new JSONObject(sb.toString());

        int productId = json.getInt("productId");
        int quantity = json.getInt("quantity");

        // 调用CartService
        CartService cartService = new CartService();
        try {
            cartService.addToCart(userId, productId, quantity);
            sendJsonResponse(response, true, "添加成功");
        } catch (Exception e) {
            sendJsonResponse(response, false, e.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        jsonResponse.put("message", message);
        response.getWriter().write(jsonResponse.toString());
    }
}
