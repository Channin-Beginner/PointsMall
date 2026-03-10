package com.hemisphericconflict.pointsmall.dao;

import com.hemisphericconflict.pointsmall.entity.CategoryDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections; // 添加缺失的Collections导入
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    public List<CategoryDB> getAllCategories() {
        List<CategoryDB> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM category";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = c3p0Tool.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // 打印SQL语句
            System.out.println("执行分类查询: " + sql);


            while (rs.next()) {
                CategoryDB category = new CategoryDB();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));

                // 设置图标类
                switch(category.getCategoryName()) {
                    case "礼品卡": category.setIconClass("fa-gift"); break;
                    case "电子产品": category.setIconClass("fa-laptop"); break;
                    case "生活家居": category.setIconClass("fa-home"); break;
                    case "服饰箱包": category.setIconClass("fa-suitcase"); break;
                    case "美食饮品": category.setIconClass("fa-utensils"); break;
                    case "旅行服务": category.setIconClass("fa-plane"); break;
                    case "游戏娱乐": category.setIconClass("fa-gamepad"); break;
                    default: category.setIconClass("fa-gift");
                }

                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("分类查询失败: " + e.getMessage());
            System.err.println("SQL: " + sql);
            return Collections.emptyList();
        } finally {
            // 确保资源关闭
            c3p0Tool.close(conn, pstmt, rs);
        }
        System.out.println("获取到分类数量: " + categories.size());
        return categories;
    }
}