package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.service.OrderService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/createOrder")
public class CreateOrderServlet extends HttpServlet {
    private final OrderService orderService = new OrderService();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            // 从Session获取用户ID
            Integer userId = (Integer) req.getSession().getAttribute("userId");
            if (userId == null) {
                out.print("{\"success\": false, \"message\": \"请先登录\"}");
                return;
            }

            // 解析请求体中的JSON数据
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject json = new JSONObject(sb.toString());

            Integer productId = json.getInt("productId");
            Integer quantity = json.getInt("quantity");
            String address = json.getString("address");
            String phone = json.getString("phone");

            System.out.println("productId" + productId);
            // 调用服务层创建订单
            orderService.createOrder(userId, productId, quantity, address, phone);

            out.print("{\"success\": true, \"message\": \"订单创建成功\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"订单创建失败：" + e.getMessage() + "\"}");
        }
    }
}