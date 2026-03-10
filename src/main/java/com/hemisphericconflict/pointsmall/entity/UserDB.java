package com.hemisphericconflict.pointsmall.entity; // 注意：包名可能拼写错误，应为"entity"

import java.util.Date;

/** 用户表实体类 */
public class UserDB {
    private Integer userId;
    private String username;
    private String phone;
    private String email; // 新增：邮箱字段
    private String password;
    private Integer totalPoints;
    private Integer role; // 新增：用户角色字段
    private Date createTime;

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; } // 新增：邮箱Getter
    public void setEmail(String email) { this.email = email; } // 新增：邮箱Setter

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

    public Integer getRole() { return role; } // 新增：角色Getter
    public void setRole(Integer role) { this.role = role; } // 新增：角色Setter

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}