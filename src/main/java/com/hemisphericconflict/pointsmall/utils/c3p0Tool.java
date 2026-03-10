package com.hemisphericconflict.pointsmall.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.*;

public class c3p0Tool {
    private static final ComboPooledDataSource DATA_SOURCE = new ComboPooledDataSource();

    // 添加连接池状态监控
    static {
        System.out.println("C3P0连接池初始化...");
        System.out.println("JDBC URL: " + DATA_SOURCE.getJdbcUrl());
        System.out.println("用户名: " + DATA_SOURCE.getUser());
    }
    public static Connection getConnection() throws SQLException {
        Connection conn = DATA_SOURCE.getConnection();

        // 新增数据库验证
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
            if (rs.next()) {
                System.out.println("当前数据库: " + rs.getString(1));
            }

            // 验证购物车记录
            rs = stmt.executeQuery("SELECT COUNT(*) FROM cart WHERE user_id=7");
            if (rs.next()) {
                System.out.println("验证记录数: " + rs.getInt(1));
            }
        }
        return conn;
    }


    // 添加此方法关闭所有资源
    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { /* 忽略 */ }
        }
        if (pstmt != null) {
            try { pstmt.close(); } catch (SQLException e) { /* 忽略 */ }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { /* 忽略 */ }
        }
    }

    // 测试连接
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("C3P0 测试连接成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

