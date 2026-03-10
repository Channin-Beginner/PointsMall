package com.hemisphericconflict.pointsmall.dao;

import com.hemisphericconflict.pointsmall.entity.ProductDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductDao {

    // 根据分类获取商品
    public List<ProductDB> getProductsByCategory(Integer categoryId) {
        List<ProductDB> products = new ArrayList<>();
        String sql = "SELECT product_id, category_id, product_name, points_price, description, image_url " +
                "FROM product WHERE is_active = 1";

        if (categoryId != null) {
            sql += " AND category_id = ?";
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = c3p0Tool.getConnection();
            pstmt = conn.prepareStatement(sql);

            if (categoryId != null) {
                pstmt.setInt(1, categoryId);
            }

            rs = pstmt.executeQuery();

            // 打印SQL语句
            System.out.println("执行商品查询: " + sql + (categoryId != null ? " 参数: " + categoryId : ""));

            while (rs.next()) {
                ProductDB product = new ProductDB();
                product.setProductId(rs.getInt("product_id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPointsPrice(rs.getInt("points_price"));
                product.setDescription(rs.getString("description"));
                product.setImageUrl(rs.getString("image_url"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("商品查询失败: " + e.getMessage());
            System.err.println("SQL: " + sql);
            return Collections.emptyList();
        } finally {
            // 确保资源关闭
            c3p0Tool.close(conn, pstmt, rs);
        }
        System.out.println("获取到商品数量: " + products.size());
        return products;
    }

    // 新增：根据关键词搜索商品（模糊查询）
    public List<ProductDB> searchProductsByKeyword(String keyword) {
        List<ProductDB> products = new ArrayList<>();
        String sql = "SELECT product_id, category_id, product_name, points_price, description, image_url " +
                "FROM product WHERE is_active = 1 AND product_name LIKE ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = c3p0Tool.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%"); // 模糊查询参数
            rs = pstmt.executeQuery();

            System.out.println("执行搜索SQL: " + sql + " 参数: " + keyword);

            while (rs.next()) {
                ProductDB product = new ProductDB();
                product.setProductId(rs.getInt("product_id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPointsPrice(rs.getInt("points_price"));
                product.setDescription(rs.getString("description"));
                product.setImageUrl(rs.getString("image_url"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("搜索商品失败: " + e.getMessage());
            System.err.println("SQL: " + sql);
            return Collections.emptyList();
        } finally {
            c3p0Tool.close(conn, pstmt, rs);
        }
        System.out.println("搜索到商品数量: " + products.size());
        return products;
    }

    // 根据ID获取单个商品
    public ProductDB getProductById(Connection conn, int productId) {
        String sql = "SELECT product_id, category_id, product_name, points_price, stock, description, image_url " +
                "FROM product WHERE product_id = ? AND is_active = 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ProductDB product = new ProductDB();
                    product.setProductId(rs.getInt("product_id"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setPointsPrice(rs.getInt("points_price"));
                    product.setStock(rs.getInt("stock"));
                    product.setDescription(rs.getString("description"));
                    product.setImageUrl(rs.getString("image_url"));
                    return product;
                }
            }
        } catch (SQLException e) {
            System.err.println("获取商品失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // 新增商品（完整字段版本）
    public boolean addProduct(ProductDB product) {
        String sql = "INSERT INTO product (" +
                "product_name, points_price, stock, description, image_url, category_id, is_active" +
                ") VALUES (" +
                "?, ?, ?, ?, ?, 1, 1" + // category_id默认设为1（礼品卡），可根据需求修改
                ")";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getPointsPrice());
            pstmt.setInt(3, product.getStock());
            pstmt.setString(4, product.getDescription());
            pstmt.setString(5, product.getImageUrl() != null ? product.getImageUrl() : ""); // 允许为空
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("新增商品失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 添加以下方法
    public boolean updateStock(Integer productId, Integer newStock) {
        String sql = "UPDATE product SET stock = ? WHERE product_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据商品ID查询库存数量
     *
     * @param productId 商品ID
     * @return 库存数量，如果未找到或出错返回 null
     */
    public Integer getStock(Integer productId) {
        String sql = "SELECT stock FROM product WHERE product_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject("stock", Integer.class); // 兼容 NULL 值
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ProductDB> getAllProducts() {
        List<ProductDB> productList = new ArrayList<>();
        String sql = "SELECT * FROM product"; // 直接查询所有商品（模仿 UserDao 的 SELECT * FROM user）

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
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
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // 在 ProductDao 类中添加 updateProduct 方法
    public boolean updateProduct(ProductDB product) {
        String sql = "UPDATE product SET product_name=?, points_price=?, stock=?, description=?, image_url=? WHERE product_id=?";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getPointsPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getImageUrl());
            stmt.setInt(6, product.getProductId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 模仿 UserDao 的 addUser 格式
    public boolean AdminaddProduct(ProductDB product) {
        String sql = "INSERT INTO product (product_name, points_price, stock, description, image_url, category_id, is_active) " +
                "VALUES (?, ?, ?, ?, ?, 1, 1)"; // category_id 默认 1，is_active 默认 1
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getPointsPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getImageUrl() != null ? product.getImageUrl() : "");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 模仿 UserDao 的 updateUserXinfo 格式
    public boolean AdminupdateProduct(ProductDB product) {
        String sql = "UPDATE product SET product_name=?, points_price=?, stock=?, description=?, image_url=? " +
                "WHERE product_id=?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getPointsPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getImageUrl());
            stmt.setInt(6, product.getProductId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 模仿 UserDao 的 deleteUser 格式
    public boolean AdmindeleteProduct(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 在 ProductDao 类中添加如下方法：
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}