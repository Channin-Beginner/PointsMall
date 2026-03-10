package com.hemisphericconflict.pointsmall.service;

import com.hemisphericconflict.pointsmall.dao.OrderDao;
import com.hemisphericconflict.pointsmall.dao.ProductDao;
import com.hemisphericconflict.pointsmall.dao.UserDao;
import com.hemisphericconflict.pointsmall.entity.OrderDB;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class OrderService {
    private final OrderDao orderDao = new OrderDao();
    private final ProductDao productDao = new ProductDao();
    private final UserDao userDao = new UserDao();

    // 创建单个商品订单
    public void createOrder(Integer userId, Integer productId, Integer quantity, String address, String phone)
            throws SQLException {
        // 检查商品存在性和库存
        Connection conn = null;
        conn = c3p0Tool.getConnection();
        ProductDB product = productDao.getProductById(conn,productId);
        System.out.println(product.getProductId());
        System.out.println(product.getProductName());
        System.out.println(product.getStock());
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        if (productDao.getStock(productId) < quantity) {
            throw new IllegalArgumentException("库存不足");
        }

        // 检查用户积分（假设积分足够，实际需添加积分校验）
        UserDB user = userDao.findById(userId);
        int totalPoints = product.getPointsPrice() * quantity;
        if (user.getTotalPoints() < totalPoints) {
            throw new IllegalArgumentException("积分不足");
        }
        // 拼接地址和手机号
//        String combinedAddress = address + "#" + phone;
        // 创建订单
        OrderDB order = new OrderDB();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPoints(totalPoints);
        order.setAddress(address);
        order.setStatus(1); // 待付款状态
        order.setOrderTime(new Date());

        if (!orderDao.addOrder(order)) {
            throw new IllegalStateException("订单创建失败");
        }

        // 扣除积分（实际应使用事务保证一致性）
        userDao.updateUserPoints(userId, user.getTotalPoints() - totalPoints);

        // 扣减库存（实际应使用事务保证一致性）
        productDao.updateStock(productId, productDao.getStock(productId) - quantity);
    }
}
