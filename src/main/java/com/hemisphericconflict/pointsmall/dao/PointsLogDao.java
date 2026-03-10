package com.hemisphericconflict.pointsmall.dao;

import com.hemisphericconflict.pointsmall.entity.PointsLogDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointsLogDao {
    // 从order表获取积分消费记录（change_type固定为1，即消费）
    public List<PointsLogDB> getPointsLogByUserId(int userId) {
        List<PointsLogDB> logs = new ArrayList<>();
        String sql = "SELECT " + "order_id AS log_id, user_id, 1 AS change_type, total_points AS change_points, order_time AS create_time  FROM `order` WHERE user_id = ? ORDER BY order_time DESC";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PointsLogDB log = new PointsLogDB();
                    log.setLogId(rs.getInt("log_id"));
                    log.setUserId(rs.getInt("user_id"));
                    log.setChangeType(1); // 固定为消费
                    log.setChangePoints(rs.getInt("change_points")); // 消费的积分（负数）
                    log.setCreateTime(rs.getTimestamp("create_time"));
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("查询积分记录失败: " + e.getMessage());
        }
        return logs;
    }

    public boolean addPointsLog(PointsLogDB log) {
        String sql = "INSERT INTO points_log (user_id, change_points, create_time) VALUES (?, ?, ?)";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, log.getUserId());
            pstmt.setInt(2, log.getChangePoints());
            pstmt.setTimestamp(3, new Timestamp(log.getCreateTime().getTime()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("添加积分明细失败: " + e.getMessage());
            return false;
        }
    }
}