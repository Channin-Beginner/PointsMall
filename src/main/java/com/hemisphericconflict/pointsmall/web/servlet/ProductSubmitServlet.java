package com.hemisphericconflict.pointsmall.web.servlet;
import com.hemisphericconflict.pointsmall.dao.ProductDao;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/product/submit") // 定义Servlet路径
public class ProductSubmitServlet extends HttpServlet {
    private final ProductDao productDao = new ProductDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. 接收表单参数
        String productName = request.getParameter("productName");
        int productPoints = Integer.parseInt(request.getParameter("productPoints"));
        int productStock = Integer.parseInt(request.getParameter("productStock"));
        String productDescription = request.getParameter("productDescription");
        String imageUrl = request.getParameter("imageUrl"); // 可选参数，根据需求添加

        // 2. 创建ProductDB对象
        ProductDB product = new ProductDB();
        product.setProductName(productName);
        product.setPointsPrice(productPoints);
        product.setStock(productStock);
        product.setDescription(productDescription);
        product.setImageUrl(imageUrl); // 存入图片路径（若有）

        // 3. 调用DAO层新增商品
        boolean success = productDao.addProduct(product);

        // 4. 处理响应（成功则重定向，失败则提示错误）
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard"); // 重定向到管理后台
        } else {
            request.setAttribute("error", "添加商品失败，请检查参数");
            request.getRequestDispatcher("/admin/Admin.jsp").forward(request, response); // 转发回商品管理页面显示错误
        }
    }
}
