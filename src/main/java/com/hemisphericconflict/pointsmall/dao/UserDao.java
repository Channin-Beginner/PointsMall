package com.hemisphericconflict.pointsmall.dao;

//用户的数据连接层
//对有关用户的操作
import com.hemisphericconflict.pointsmall.entity.UserDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final ComboPooledDataSource dataSource = new ComboPooledDataSource();

    // 根据手机号查询用户
    public UserDB findByPhone(String phone) {
        String sql = "SELECT * FROM user WHERE phone = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 根据ID获取用户
    public UserDB getUserById(int userId) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserDB user = new UserDB();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPhone(rs.getString("phone"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setTotalPoints(rs.getInt("total_points"));
                    user.setRole(rs.getInt("role"));
                    user.setCreateTime(rs.getTimestamp("create_time"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 根据邮箱查询用户
    public UserDB findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 更新用户密码
    public boolean updatePassword(String identifier, String newPassword, boolean isPhone) {
        String sql = "UPDATE user SET password = ? WHERE " + (isPhone ? "phone" : "email") + " = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            System.out.println("密码长度: " + newPassword.length()); // 添加长度日志
            pstmt.setString(2, identifier);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新密码失败: " + e.getMessage()); // 打印具体错误
            e.printStackTrace();
            return false;
        }
    }

    // 结果集映射为用户对象
    private UserDB mapToUser(ResultSet rs) throws SQLException {
        UserDB user = new UserDB();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setTotalPoints(rs.getInt("total_points"));
        user.setRole(rs.getInt("role"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        return user;
    }
    // UserDao.java（新增方法）
    public UserDB findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs); // 复用原有结果集映射方法
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean insertUser(UserDB user) {
        String sql = "INSERT INTO user (username, phone, password) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getPassword());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UserDao.java
    public UserDB findById(int userId) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs); // 复用结果集映射方法
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新增：更新用户积分
    public boolean updateUserPoints(int userId, int newPoints) {
        String sql = "UPDATE user SET total_points = ? WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newPoints);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新用户积分失败: " + e.getMessage());
            return false;
        }
    }

    // 修改更新用户信息的方法，确保密码加密
    public boolean updateUser(UserDB user, Integer userId) {
        String sql = "UPDATE user SET email = ?, phone = ?, password = ? ,username = ? WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 关键：如果密码不为空，则进行BCrypt加密
            String hashedPassword = user.getPassword() != null ?
                    BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()) :
                    user.getPassword(); // 保留原有密码（若未修改密码）

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, hashedPassword); // 存入加密后的密码
            pstmt.setString(4, user.getUsername());
            pstmt.setInt(5, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取所有用户（管理员专用）
    public List<UserDB> getAllUsers() {
        List<UserDB> userList = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UserDB user = new UserDB();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setTotalPoints(rs.getInt("total_points"));
                user.setRole(rs.getInt("role"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    // 删除用户
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 检查用户是否为管理员
    public boolean isAdmin(int userId) {
        String sql = "SELECT role FROM user WHERE user_id = ?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("role") == 1; // 假设role=1表示管理员
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 更新用户信息
    public boolean updateUserXinfo(UserDB user) {
        String sql = "UPDATE user SET username=?, phone=?, email=?, password=?, total_points=?, role=? " +
                "WHERE user_id=?";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 关键：如果密码不为空，则进行BCrypt加密
            String hashedPassword = user.getPassword() != null ?
                    BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()) :
                    user.getPassword(); // 保留原有密码（若未修改密码）

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, hashedPassword);
            pstmt.setInt(5, user.getTotalPoints());
            pstmt.setInt(6, user.getRole());
            pstmt.setInt(7, user.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(UserDB user) {
        String sql = "INSERT INTO user (username, phone, email, password, role, total_points) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = c3p0Tool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setInt(5, user.getRole());
            stmt.setInt(6, user.getTotalPoints());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean isPhoneExists(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE phone = ?";
        Connection conn;
        conn = c3p0Tool.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        Connection conn;
        conn = c3p0Tool.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        Connection conn;
        conn = c3p0Tool.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {}
        return false;
    }

}
