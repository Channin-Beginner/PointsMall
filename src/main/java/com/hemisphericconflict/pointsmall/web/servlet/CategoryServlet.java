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

@WebServlet("/home")
public class CategoryServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



        // 获取所有分类
        List<CategoryDB> categories = categoryService.getAllCategories();
        System.out.println("Servlet获取分类数量：" + categories.size());
        request.setAttribute("categories", categories);

        // 获取分类参数（默认获取全部商品）
        String categoryParam = request.getParameter("category");
        Integer categoryId = null;
        if (categoryParam != null && !categoryParam.equals("all")) {
            try {
                categoryId = Integer.parseInt(categoryParam);
            } catch (NumberFormatException e) {
                // 处理无效分类ID
            }
        }

        // 获取商品列表
        List<ProductDB> products = productService.getProductsByCategory(categoryId);
        System.out.println("商品数量：" + products.size()); // 调试输出
        request.setAttribute("products", products);

        // 转发到首页JSP
        request.getRequestDispatcher("/common/Home.jsp").forward(request, response);
    }
}