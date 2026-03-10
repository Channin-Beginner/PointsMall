package com.hemisphericconflict.pointsmall.dao;

import com.hemisphericconflict.pointsmall.entity.CartItemDB;
import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDao {
    // 获取用户购物车所有项
    public List<CartItemDB> getUserCartItems(Integer userId) {
        List<CartItemDB> cartItems = new ArrayList<>();
        String sql = "SELECT c.*, p.product_name, p.points_price, p.image_url " +
                "FROM cart c JOIN product p ON c.product_id = p.product_id " +
                "WHERE c.user_id = ?";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cartItems.add(mapCartItemFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    // 添加购物车项
    public void addCartItem(CartItemDB cartItem) {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartItem.getUserId());
            pstmt.setInt(2, cartItem.getProductId());
            pstmt.setInt(3, cartItem.getQuantity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 其他DAO方法实现...
    private CartItemDB mapCartItemFromResultSet(ResultSet rs) throws SQLException {
        CartItemDB cartItem = new CartItemDB();
        cartItem.setCartId(rs.getInt("cart_id"));
        cartItem.setUserId(rs.getInt("user_id"));
        cartItem.setProductId(rs.getInt("product_id"));
        cartItem.setQuantity(rs.getInt("quantity"));

        // 设置关联商品信息
        ProductDB product = new ProductDB();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setPointsPrice(rs.getInt("points_price"));
        product.setImageUrl(rs.getString("image_url"));
        cartItem.setProduct(product);

        return cartItem;
    }

    public boolean removeCartItem(Connection conn, int userId, int productId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // 根据用户ID和商品ID获取单个购物车项
    public CartItemDB getCartItem(Connection conn, int userId, int productId) {
        String sql = "SELECT cart_id, user_id, product_id, quantity FROM cart " +
                "WHERE user_id = ? AND product_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CartItemDB cartItem = new CartItemDB();
                    cartItem.setCartId(rs.getInt("cart_id"));
                    cartItem.setUserId(rs.getInt("user_id"));
                    cartItem.setProductId(rs.getInt("product_id"));
                    cartItem.setQuantity(rs.getInt("quantity"));
                    return cartItem;
                }
            }
        } catch (SQLException e) {
            System.err.println("获取购物车项失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}