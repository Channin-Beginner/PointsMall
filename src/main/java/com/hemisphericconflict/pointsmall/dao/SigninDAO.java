package com.hemisphericconflict.pointsmall.dao;

import com.hemisphericconflict.pointsmall.utils.c3p0Tool;

import java.sql.*;
import java.time.LocalDate;

public class SigninDAO {
    // 检查用户今天是否已签到
    public boolean hasSignedToday(int userId) throws SQLException {
        String sql = "SELECT 1 FROM user_signin WHERE user_id = ? AND signin_date = CURDATE()";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // 记录签到并返回新的连续天数
    public int recordSignIn(int userId, LocalDate signinDate) throws SQLException {
        String sql = "INSERT INTO user_signin (user_id, signin_date, consecutive_days) " +
                "VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE consecutive_days = consecutive_days + 1";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(signinDate));
            stmt.executeUpdate();

            // 查询最新连续天数
            return getConsecutiveDays(userId);
        }
    }

    // 获取用户当前连续签到天数
    public int getConsecutiveDays(int userId) throws SQLException {
        String sql = "SELECT SUM(consecutive_days) FROM user_signin WHERE user_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // 最后签到日期查询
    public LocalDate getLastSignDate(int userId) throws SQLException {
        String sql = "SELECT MAX(signin_date) FROM user_signin WHERE user_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date date = rs.getDate(1);
                    return date != null ? date.toLocalDate() : null;
                }
                return null;
            }
        }
    }
}
