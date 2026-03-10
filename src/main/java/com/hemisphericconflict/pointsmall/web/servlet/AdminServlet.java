package com.hemisphericconflict.pointsmall.web.servlet;


import com.hemisphericconflict.pointsmall.dao.ProductDao;
import com.hemisphericconflict.pointsmall.dao.TaskDAO;
import com.hemisphericconflict.pointsmall.dao.UserDao;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.entity.TaskDB;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final ProductDao productDao = new ProductDao();
    private final TaskDAO taskDAO = new TaskDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. 获取用户列表（带角色、手机号、密码）
        List<UserDB> userList = userDao.getAllUsers(); // 需在UserDao中新增getAllUsers方法
        System.out.println("获取到的用户数量：" + userList.size()); // 调试日志

        // 2. 获取商品列表
        List<ProductDB> productList = productDao.getAllProducts();


        // 3. 获取任务列表
        List<TaskDB> taskList = null; // 假设TaskDAO有获取所有任务的方法
        try {
            taskList = taskDAO.getTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 4. 将数据存入请求域
        request.setAttribute("userList", userList);
        request.setAttribute("productList", productList);
        request.setAttribute("taskList", taskList);

        // 5. 转发到管理员页面
        request.getRequestDispatcher("/admin/Admin.jsp").forward(request, response);
    }
}
