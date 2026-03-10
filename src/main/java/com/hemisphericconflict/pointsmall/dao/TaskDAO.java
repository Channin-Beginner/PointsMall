package com.hemisphericconflict.pointsmall.dao;

import com.hemisphericconflict.pointsmall.entity.TaskDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    // 获取单个任务
    public TaskDB getTaskById(int taskId) throws SQLException {
        String sql = "SELECT * FROM task_definition WHERE task_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTask(rs);
                }
            }
        }
        return null;
    }

    // 模仿 UserDao 的 getAllUsers() 结构
    public List<TaskDB> getAllTasks() {
        List<TaskDB> taskList = new ArrayList<>();
        String sql = "SELECT * FROM task_definition"; // 直接查询所有任务（模仿 UserDao 的 SELECT * FROM user）

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                TaskDB task = new TaskDB();
                task.setTaskId(rs.getInt("task_id"));
                task.setTaskName(rs.getString("task_name"));
                task.setTaskDescription(rs.getString("task_description"));
                task.setPointsAwarded(rs.getInt("points_awarded"));
                task.setTaskStatus(rs.getString("task_status"));
                task.setDisplayOrder(rs.getInt("display_order"));
                task.setTaskType(rs.getString("task_type"));
                task.setCreatedAt(rs.getTimestamp("created_at"));
                taskList.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskList;
    }


    // 获取所有任务（修改：原方法仅获取active任务，现改为获取所有任务）
    public List<TaskDB> getTasks() throws SQLException {
        List<TaskDB> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task_definition ORDER BY display_order"; // 修改SQL查询语句

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tasks.add(mapRowToTask(rs));
            }
        }
        return tasks;
    }

    // 获取签到任务信息
    public TaskDB getSignInTask() throws SQLException {
        String sql = "SELECT * FROM task_definition WHERE task_name = '每日签到' LIMIT 1";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapRowToTask(rs);
            }
        }
        return null;
    }


    // 添加新任务
    public static boolean addTask(TaskDB task) throws SQLException {
        String sql = "INSERT INTO task_definition (task_name, task_description, points_awarded, " +
                "task_status, display_order, task_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getTaskDescription());
            stmt.setInt(3, task.getPointsAwarded());
            stmt.setString(4, task.getTaskStatus());
            stmt.setInt(5, task.getDisplayOrder());
            stmt.setString(6, task.getTaskType());
            return stmt.executeUpdate() > 0;
        }
    }

    // 更新任务
    public static boolean updateTask(TaskDB task) throws SQLException {
        String sql = "UPDATE task_definition SET " +
                "task_name=?, task_description=?, points_awarded=?, " +
                "task_status=?, display_order=?, task_type=? " +
                "WHERE task_id=?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getTaskDescription());
            stmt.setInt(3, task.getPointsAwarded());
            stmt.setString(4, task.getTaskStatus());
            stmt.setInt(5, task.getDisplayOrder());
            stmt.setString(6, task.getTaskType());
            stmt.setInt(7, task.getTaskId());
            return stmt.executeUpdate() > 0;
        }
    }

    // 删除任务（逻辑删除）
    public static boolean deleteTask(int taskId) throws SQLException {
        String sql = "UPDATE task_definition SET task_status='inactive' WHERE task_id=?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            return stmt.executeUpdate() > 0;
        }
    }
    // 映射结果集到Task对象
    private TaskDB mapRowToTask(ResultSet rs) throws SQLException {
        TaskDB task = new TaskDB();
        task.setTaskId(rs.getInt("task_id"));
        task.setTaskName(rs.getString("task_name"));
        task.setTaskDescription(rs.getString("task_description"));
        task.setPointsAwarded(rs.getInt("points_awarded"));
        task.setTaskStatus(rs.getString("task_status"));
        task.setDisplayOrder(rs.getInt("display_order"));
        task.setTaskType(rs.getString("task_type"));
        task.setCreatedAt(rs.getTimestamp("created_at"));
        return task;
    }

    // TaskDAO.java
    public List<TaskDB> getActiveTasks() throws SQLException {
        return getTasksByStatus("active");
    }

    public List<TaskDB> getUpcomingTasks() throws SQLException {
        return getTasksByStatus("upcoming");
    }

    private List<TaskDB> getTasksByStatus(String status) throws SQLException {
        List<TaskDB> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task_definition WHERE task_status = ? ORDER BY display_order";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }
        }
        return tasks;
    }

    // 模仿 UserDao 的 addUser 格式
    public boolean AdminaddTask(TaskDB task) {
        String sql = "INSERT INTO task_definition (task_name, task_description, points_awarded, task_status, display_order, task_type) " +
                "VALUES (?, ?, ?, ?, 0, ?)"; // display_order 默认 0
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getTaskDescription());
            stmt.setInt(3, task.getPointsAwarded());
            stmt.setString(4, task.getTaskStatus());
            stmt.setString(5, task.getTaskType());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 模仿 UserDao 的 updateUserXinfo 格式
    public boolean AdminupdateTask(TaskDB task) {
        String sql = "UPDATE task_definition SET task_name=?, task_description=?, points_awarded=?, task_status=?, display_order=?, task_type=? " +
                "WHERE task_id=?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getTaskDescription());
            stmt.setInt(3, task.getPointsAwarded());
            stmt.setString(4, task.getTaskStatus());
            stmt.setInt(5, task.getDisplayOrder());
            stmt.setString(6, task.getTaskType());
            stmt.setInt(7, task.getTaskId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 模仿 UserDao 的 deleteUser 格式（逻辑删除改为物理删除，或保持原逻辑）
    public boolean AdmindeleteTask(int taskId) {
        String sql = "DELETE FROM task_definition WHERE task_id = ?"; // 直接物理删除，或改为更新状态
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
