// src/main/java/com/hemisphericconflict/pointsmall/web/servlet/SearchServlet.java
package com.hemisphericconflict.pointsmall.web.servlet;

import com.hemisphericconflict.pointsmall.entity.CategoryDB;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.service.CategoryService;
import com.hemisphericconflict.pointsmall.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 处理中文乱码

        // 获取搜索关键词
        String keyword = request.getParameter("keyword");
        List<ProductDB> products = null;

        if (keyword != null && !keyword.isEmpty()) {
            // 调用新增的搜索方法
            products = productService.searchProducts(keyword);
        } else {
            // 关键词为空时返回全部商品
            products = productService.getProductsByCategory(null);
        }

        // 获取分类数据（用于页面导航）
        List<CategoryDB> categories = categoryService.getAllCategories();
        request.setAttribute("categories", categories);
        request.setAttribute("products", products);
        request.setAttribute("currentKeyword", keyword); // 保存当前关键词用于页面显示

        // 转发到首页JSP
        request.getRequestDispatcher("/common/SearchResult.jsp").forward(request, response);
    }
}