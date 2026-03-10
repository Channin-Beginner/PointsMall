package com.hemisphericconflict.pointsmall.service;

import com.hemisphericconflict.pointsmall.dao.CartDao;
import com.hemisphericconflict.pointsmall.dao.ProductDao;
import com.hemisphericconflict.pointsmall.entity.CartItemDB;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    private final CartDao cartDao = new CartDao();

    // 核心业务方法：添加商品到购物车
    public void addToCart(Integer userId, Integer productId, Integer quantity) {
        // 验证商品可用性
        ProductDB product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        if (product.getIsActive() != 1) {
            throw new IllegalArgumentException("商品已下架");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("库存不足，当前库存: " + product.getStock());
        }

        // 检查是否已存在购物车
        CartItemDB existingItem = getCartItem(userId, productId);
        if (existingItem != null) {
            // 更新数量
            updateQuantity(userId, productId, existingItem.getQuantity() + quantity);
        } else {
            // 添加新项
            try (Connection conn = c3p0Tool.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)")) {

                pstmt.setInt(1, userId);
                pstmt.setInt(2, productId);
                pstmt.setInt(3, quantity);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("添加购物车失败: " + e.getMessage(), e);
            }
        }
    }

    // 获取用户购物车所有项
    public List<CartItemDB> getUserCart(Integer userId) {
        List<CartItemDB> cartItems = new ArrayList<>();
        System.out.println("==== 开始查询购物车 ====");
        System.out.println("执行 getUserCart，userId: " + userId); // 新增日志

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT c.cart_id, c.user_id, c.product_id, c.quantity, " +
                             "p.product_name, p.points_price, p.image_url " +
                             "FROM cart c JOIN product p ON c.product_id = p.product_id " +
                             "WHERE c.user_id = ?")) {

            pstmt.setInt(1, userId);
            System.out.println("执行SQL: " + pstmt);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CartItemDB item = new CartItemDB();
                    item.setCartId(rs.getInt("cart_id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));

                    // 关联商品信息
                    ProductDB product = new ProductDB();
                    product.setProductId(rs.getInt("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setPointsPrice(rs.getInt("points_price"));
                    product.setImageUrl(rs.getString("image_url"));

                    item.setProduct(product);
                    cartItems.add(item);

                    // 调试日志
                    System.out.printf("映射商品: ID=%d, 名称=%s, 数量=%d%n",
                            item.getProductId(),
                            item.getProduct().getProductName(),
                            item.getQuantity());
                }
            }
            return cartItems;
        } catch (SQLException e) {
            System.err.println("数据库错误: " + e.getMessage());
            throw new RuntimeException("获取购物车失败", e);
        }
    }

    // 更新购物车商品数量
    public void updateQuantity(Integer userId, Integer productId, Integer newQuantity) {
        // 验证商品可用性
        ProductDB product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        if (product.getIsActive() != 1) {
            throw new IllegalArgumentException("商品已下架");
        }
        if (product.getStock() < newQuantity) {
            throw new IllegalArgumentException("库存不足，最大数量: " + product.getStock());
        }

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?")) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, productId);
            int updated = pstmt.executeUpdate();

            if (updated == 0) {
                throw new IllegalArgumentException("购物车项不存在");
            }

        } catch (SQLException e) {
            throw new RuntimeException("更新购物车失败: " + e.getMessage(), e);
        }
    }

    // 从购物车移除商品
    public void removeFromCart(Integer userId, Integer productId) {
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM cart WHERE user_id = ? AND product_id = ?")) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("移除购物车失败: " + e.getMessage(), e);
        }
    }

    // 计算购物车总积分
    public int calculateTotalPoints(List<CartItemDB> cartItems) {
        int totalPoints = 0;
        for (CartItemDB item : cartItems) {
            totalPoints += item.getProduct().getPointsPrice() * item.getQuantity();
        }
        return totalPoints;
    }

    // 清空用户购物车
    public void clearUserCart(Integer userId) {
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM cart WHERE user_id = ?")) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("清空购物车失败: " + e.getMessage(), e);
        }
    }

    // 核心业务方法：结算购物车
    public void checkoutCart(Integer userId) {
        // 1. 获取购物车项
        List<CartItemDB> cartItems = getUserCart(userId);

        // 2. 计算总积分和验证库存
        int totalPoints = 0;
        for (CartItemDB item : cartItems) {
            ProductDB product = getProductById(item.getProductId());
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("商品 '" + product.getProductName() +
                        "' 库存不足，需要: " + item.getQuantity() +
                        " 剩余: " + product.getStock());
            }
            totalPoints += product.getPointsPrice() * item.getQuantity();
        }

        // 3. 验证用户积分（伪代码）
        // if (user.getTotalPoints() < totalPoints) {
        //     throw new IllegalStateException("积分不足");
        // }

        // 4. 扣减库存（事务处理）
        try (Connection conn = c3p0Tool.getConnection()) {
            conn.setAutoCommit(false); // 开始事务

            // 扣减库存
            for (CartItemDB item : cartItems) {
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE product SET stock = stock - ? WHERE product_id = ?")) {

                    pstmt.setInt(1, item.getQuantity());
                    pstmt.setInt(2, item.getProductId());
                    pstmt.executeUpdate();
                }
            }

            // 清空购物车
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM cart WHERE user_id = ?")) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            conn.commit(); // 提交事务
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            throw new RuntimeException("结算失败: " + e.getMessage(), e);
        }
    }

    // 辅助方法：获取单个商品信息
    private ProductDB getProductById(Integer productId) {
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM product WHERE product_id = ?")) {

            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapProductFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取商品信息失败: " + e.getMessage(), e);
        }
        return null;
    }

    // 获取单个购物车项
    private CartItemDB getCartItem(Integer userId, Integer productId) {
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM cart WHERE user_id = ? AND product_id = ?")) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapCartItemWithoutProduct(rs);
                }
            }
        } catch (SQLException e) {
            // 忽略错误，返回null
        }
        return null;
    }

    // 结果集映射为购物车项（含商品信息）
    private CartItemDB mapCartItemFromResultSet(ResultSet rs) throws SQLException {
        CartItemDB item = new CartItemDB();
        item.setCartId(rs.getInt("cart_id"));
        item.setUserId(rs.getInt("user_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));

        // 创建并填充关联商品
        ProductDB product = new ProductDB();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setPointsPrice(rs.getInt("points_price"));
        product.setImageUrl(rs.getString("image_url"));

        item.setProduct(product);  // 关联商品对象
        return item;
    }

    // 结果集映射为购物车项（不含商品信息）
    private CartItemDB mapCartItemWithoutProduct(ResultSet rs) throws SQLException {
        CartItemDB cartItem = new CartItemDB();
        cartItem.setCartId(rs.getInt("cart_id"));
        cartItem.setUserId(rs.getInt("user_id"));
        cartItem.setProductId(rs.getInt("product_id"));
        cartItem.setQuantity(rs.getInt("quantity"));
        cartItem.setAddedTime(rs.getTimestamp("added_time"));
        return cartItem;
    }

    // 结果集映射为商品信息
    private ProductDB mapProductFromResultSet(ResultSet rs) throws SQLException {
        ProductDB product = new ProductDB();
        product.setProductId(rs.getInt("product_id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setProductName(rs.getString("product_name"));
        product.setPointsPrice(rs.getInt("points_price"));
        product.setStock(rs.getInt("stock"));
        product.setDescription(rs.getString("description"));
        product.setImageUrl(rs.getString("image_url"));
        product.setIsActive(rs.getInt("is_active"));
        product.setCreateTime(rs.getTimestamp("create_time"));
        return product;
    }
}