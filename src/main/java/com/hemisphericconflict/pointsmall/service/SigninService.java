package com.hemisphericconflict.pointsmall.service;
import com.hemisphericconflict.pointsmall.dao.TaskDAO;
import com.hemisphericconflict.pointsmall.entity.TaskDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SigninService {
    private final TaskDAO taskDAO = new TaskDAO();

    // 处理签到
    public Map<String, Object> processSignin(int userId) {
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = c3p0Tool.getConnection()) {
            // 检查今日是否已签到
            if (hasSignedToday(conn, userId)) {
                result.put("error", "今日已签到");
                return result;
            }

            // 获取签到任务信息
            TaskDB signInTask = taskDAO.getSignInTask();
            if (signInTask == null) {
                result.put("error", "签到任务未配置");
                return result;
            }

            // 计算连续天数
            int consecutiveDays = calculateConsecutiveDays(conn, userId);

            // 计算奖励积分
            int points = calculatePoints(consecutiveDays);

            // 记录签到
            recordSignin(conn, userId, points, consecutiveDays);

            // 返回结果
            result.put("success", true);
            result.put("points", points);
            result.put("consecutiveDays", consecutiveDays);

        } catch (SQLException e) {
            result.put("error", "数据库错误: " + e.getMessage());
        }
        return result;
    }

    // 获取用户连续签到天数
    public int getUserConsecutiveDays(int userId) {
        try (Connection conn = c3p0Tool.getConnection()) {
            return getCurrentConsecutiveDays(conn, userId);
        } catch (SQLException e) {
            return 0;
        }
    }

    // 检查今日是否已签到
    private boolean hasSignedToday(Connection conn, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_signin WHERE user_id = ? AND signin_date = CURDATE()";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // 计算连续天数
    private int calculateConsecutiveDays(Connection conn, int userId) throws SQLException {
        // 获取上次签到日期
        String lastSigninSql = "SELECT signin_date, consecutive_days " +
                "FROM user_signin " +
                "WHERE user_id = ? " +
                "ORDER BY signin_date DESC LIMIT 1";

        Date lastSigninDate = null;
        int lastConsecutiveDays = 0;

        try (PreparedStatement stmt = conn.prepareStatement(lastSigninSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lastSigninDate = rs.getDate("signin_date");
                    lastConsecutiveDays = rs.getInt("consecutive_days");
                }
            }
        }

        // 如果上次签到是昨天，则连续天数+1
        LocalDate yesterday = LocalDate.now().minusDays(1);
        if (lastSigninDate != null && lastSigninDate.toLocalDate().equals(yesterday)) {
            return lastConsecutiveDays + 1;
        }

        // 否则从1开始
        return 1;
    }

    // 获取当前连续天数
    private int getCurrentConsecutiveDays(Connection conn, int userId) throws SQLException {
        String sql = "SELECT consecutive_days FROM user_signin " +
                "WHERE user_id = ? AND signin_date = CURDATE()";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("consecutive_days");
                }
            }
        }

        // 如果今日未签到，返回昨日连续天数（如果有）
        String yesterdaySql = "SELECT consecutive_days FROM user_signin " +
                "WHERE user_id = ? AND signin_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)";

        try (PreparedStatement stmt = conn.prepareStatement(yesterdaySql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("consecutive_days");
                }
            }
        }
        return 0;
    }

    // 计算奖励积分
    private int calculatePoints(int consecutiveDays) {
        int basePoints = 10; // 基础积分
        int bonusPoints = 0;

        if (consecutiveDays >= 5) bonusPoints += 50;
        if (consecutiveDays >= 15) bonusPoints += 150;
        if (consecutiveDays >= 30) bonusPoints += 300;

        return basePoints + bonusPoints;
    }

    // 记录签到
    private void recordSignin(Connection conn, int userId, int points, int consecutiveDays) throws SQLException {
        // 记录签到
        String signinSql = "INSERT INTO user_signin (user_id, signin_date, consecutive_days) VALUES (?, CURDATE(), ?)";
        try (PreparedStatement stmt = conn.prepareStatement(signinSql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, consecutiveDays);
            stmt.executeUpdate();
        }

        // 更新用户积分
        String updatePointsSql = "UPDATE user SET total_points = total_points + ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updatePointsSql)) {
            stmt.setInt(1, points);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }

        // 记录积分变动
        String pointsLogSql = "INSERT INTO points_log (user_id, change_type, change_points, " +
                "before_points, after_points, description) " +
                "SELECT ?, 3, ?, total_points, total_points + ?, '每日签到奖励' " +
                "FROM user WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(pointsLogSql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, points);
            stmt.setInt(3, points);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        }
    }
}
