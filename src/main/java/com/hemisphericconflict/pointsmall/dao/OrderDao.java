package com.hemisphericconflict.pointsmall.dao;

import com.hemisphericconflict.pointsmall.entity.OrderDB;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    // 添加订单
    public boolean addOrder(OrderDB order) {
        String sql = "INSERT INTO `order` (user_id, product_id, quantity, address, status, total_points, order_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getProductId());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setString(4, order.getAddress());
            pstmt.setInt(5, order.getStatus());
            pstmt.setInt(6, order.getTotalPoints());
            pstmt.setTimestamp(7, new Timestamp(order.getOrderTime().getTime()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("添加订单失败: " + e.getMessage());
            return false;
        }
    }

    // 获取用户的订单历史（带地址）
// 获取用户订单（包含关联的Product信息）
    public List<OrderDB> getOrdersByUserId(int userId) {
        List<OrderDB> orders = new ArrayList<>();
        String sql = "SELECT " +
                "o.*, p.product_name, p.image_url " + // 选择订单和商品字段
                "FROM `order` o " +
                "LEFT JOIN product p ON o.product_id = p.product_id " + // 左连接商品表
                "WHERE o.user_id = ? " +
                "ORDER BY o.order_time DESC";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDB order = new OrderDB();
                    // 填充OrderDB自身字段
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setProductId(rs.getInt("product_id"));
                    order.setQuantity(rs.getInt("quantity"));
                    order.setAddress(rs.getString("address"));
                    order.setStatus(rs.getInt("status"));
                    order.setTotalPoints(rs.getInt("total_points"));
                    order.setOrderTime(rs.getTimestamp("order_time"));

                    // 实例化ProductDB并填充商品信息
                    ProductDB product = new ProductDB();
                    product.setProductName(rs.getString("product_name"));
                    product.setImageUrl(rs.getString("image_url"));
                    order.setProduct(product); // 在OrderDB中设置关联的Product对象

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("查询订单失败: " + e.getMessage());
        }
        return orders;
    }

    // 在OrderDao类中添加以下方法
    public boolean updateOrderStatus(int orderId, int status) {
        String sql = "UPDATE `order` SET status = ? WHERE order_id = ?";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, status);
            pstmt.setInt(2, orderId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新订单状态失败: " + e.getMessage());
            return false;
        }
    }

    // 新增：删除订单
    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM `order` WHERE order_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除订单失败: " + e.getMessage());
            return false;
        }
    }

    // 新增：根据订单ID查询订单详情
    public OrderDB getOrderById(int orderId) {
        String sql = "SELECT " +
                "o.*, p.product_name, p.image_url " +
                "FROM `order` o " +
                "LEFT JOIN product p ON o.product_id = p.product_id " +
                "WHERE o.order_id = ?";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    OrderDB order = new OrderDB();
                    // 填充订单基本信息
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setProductId(rs.getInt("product_id"));
                    order.setQuantity(rs.getInt("quantity"));
                    order.setAddress(rs.getString("address"));
                    order.setStatus(rs.getInt("status"));
                    order.setTotalPoints(rs.getInt("total_points"));
                    order.setOrderTime(rs.getTimestamp("order_time"));

                    // 填充关联的商品信息
                    ProductDB product = new ProductDB();
                    product.setProductName(rs.getString("product_name"));
                    product.setImageUrl(rs.getString("image_url"));
                    order.setProduct(product);

                    return order;
                }
            }
        } catch (SQLException e) {
            System.err.println("查询订单详情失败: " + e.getMessage());
        }
        return null; // 订单不存在或查询失败时返回null
    }

    public boolean addOrder(Connection conn, OrderDB order) throws SQLException {
        String sql = "INSERT INTO `order` (user_id, product_id, quantity, total_points, status, order_time, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getProductId());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setInt(4, order.getTotalPoints());
            pstmt.setInt(5, order.getStatus());
            pstmt.setTimestamp(6, new Timestamp(order.getOrderTime().getTime()));
            pstmt.setString(7, order.getAddress()); // 必须传入 address
            return pstmt.executeUpdate() > 0;
        }
    }
}
