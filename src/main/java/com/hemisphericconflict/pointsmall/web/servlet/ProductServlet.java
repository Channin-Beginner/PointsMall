package com.hemisphericconflict.pointsmall.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hemisphericconflict.pointsmall.dao.ProductDao;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/product")
public class ProductServlet extends HttpServlet {
    private final ProductDao productDao = new ProductDao();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String action = request.getParameter("action");
        Connection conn = null;
        try {
            conn = c3p0Tool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if ("all".equals(action)) {
            List<ProductDB> productList = productDao.getAllProducts();
            response.getWriter().write(objectMapper.writeValueAsString(productList));
        } else if ("get".equals(action)) { // 新增获取单个商品的 action
            int productId = Integer.parseInt(request.getParameter("productId"));
            ProductDB product = productDao.getProductById(conn,productId);
            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "商品不存在");
                return;
            }
            response.getWriter().write(objectMapper.writeValueAsString(product));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效操作");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Result result = new Result();
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) { // 模仿 UserServlet 的 add 逻辑
                ProductDB product = new ProductDB();
                product.setProductName(request.getParameter("productName"));
                product.setPointsPrice(Integer.parseInt(request.getParameter("pointsPrice")));
                product.setStock(Integer.parseInt(request.getParameter("stock")));
                product.setDescription(request.getParameter("description"));
                product.setImageUrl(request.getParameter("imageUrl"));

                if (productDao.AdminaddProduct(product)) {
                    result.setSuccess(true);
                    result.setMessage("商品添加成功");
                } else {
                    result.setSuccess(false);
                    result.setMessage("商品添加失败（名称或库存无效）");
                }
            } else if ("update".equals(action)) { // 模仿 UserServlet 的 update 逻辑
                ProductDB product = new ProductDB();
                product.setProductId(Integer.parseInt(request.getParameter("productId")));
                product.setProductName(request.getParameter("productName"));
                product.setPointsPrice(Integer.parseInt(request.getParameter("pointsPrice")));
                product.setStock(Integer.parseInt(request.getParameter("stock")));
                product.setDescription(request.getParameter("description"));
                product.setImageUrl(request.getParameter("imageUrl"));

                if (productDao.AdminupdateProduct(product)) {
                    result.setSuccess(true);
                    result.setMessage("商品更新成功");
                } else {
                    result.setSuccess(false);
                    result.setMessage("商品更新失败");
                }
            } else if ("delete".equals(action)) { // 模仿 UserServlet 的 delete 逻辑
                int productId = Integer.parseInt(request.getParameter("productId"));
                if (productDao.AdmindeleteProduct(productId)) {
                    result.setSuccess(true);
                    result.setMessage("商品删除成功");
                } else {
                    result.setSuccess(false);
                    result.setMessage("商品删除失败（可能存在关联数据）");
                }
            } else {
                result.setSuccess(false);
                result.setMessage("无效操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败: " + e.getMessage());
        }

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    private static class Result {
        private boolean success;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}