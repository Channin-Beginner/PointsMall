package com.hemisphericconflict.pointsmall.service;

import com.hemisphericconflict.pointsmall.dao.OrderDao;
import com.hemisphericconflict.pointsmall.dao.PointsLogDao;
import com.hemisphericconflict.pointsmall.dao.UserDao;
import com.hemisphericconflict.pointsmall.entity.OrderDB;
import com.hemisphericconflict.pointsmall.entity.PointsLogDB;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final OrderDao orderDao = new OrderDao();
    private final PointsLogDao pointsLogDao = new PointsLogDao();


    // 获取用户详情（含总积分）
    public UserDB getUserById(int userId) {
        return userDao.findById(userId); // 使用新增的findById方法
    }


    // 获取用户订单列表
    public List<OrderDB> getOrdersByUserId(int userId) {
        return orderDao.getOrdersByUserId(userId);
    }

    // 获取用户积分变动记录
    public List<PointsLogDB> getPointsLogByUserId(int userId) {
        return pointsLogDao.getPointsLogByUserId(userId);
    }

    //    忘记密码验证
    private static final Map<String, String> verifyCodeMap = new HashMap<>(); // 验证码临时存储

    // 发送验证码
    public String sendVerificationCode(String identifier, boolean isPhone) {
        UserDB user = isPhone ? userDao.findByPhone(identifier) : userDao.findByEmail(identifier);
        if (user == null) {
            return "该" + (isPhone ? "手机号" : "邮箱") + "未注册";
        }

        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        verifyCodeMap.put(identifier, code);

        // 实际项目中这里应该调用短信/邮件发送服务
        System.out.println("向" + (isPhone ? "手机号" : "邮箱") + " " + identifier + " 发送验证码：" + code);
        return "验证码已发送";
    }

    // 验证验证码并重置密码
    public String resetPassword(String identifier, String code, String newPassword, boolean isPhone) {
        if (newPassword.length() < 6) {
            return "密码长度至少6位";
        }
        // 验证验证码
        String storedCode = verifyCodeMap.get(identifier);
        if (storedCode == null || !storedCode.equals(code)) {
            return "验证码错误";
        }

        // 密码加密（使用BCrypt）
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

        // 更新密码
        boolean success = userDao.updatePassword(identifier, hashedPassword, isPhone);
        if (success) {
            verifyCodeMap.remove(identifier); // 验证通过后移除验证码
            return "密码重置成功";
        } else {
            return "密码重置失败";
        }
    }

    // 验证码有效性检查
    public boolean isValidCode(String identifier, String code) {
        String storedCode = verifyCodeMap.get(identifier);
        return storedCode != null && storedCode.equals(code);
    }






    // 登录验证（整合用户名/手机/邮箱查询）
    public UserDB login(String identifier, String password) {
        // 1. 先通过用户名查询
        UserDB user = userDao.findByUsername(identifier);
        if (user != null && verifyPassword(password, user)) {
            return user;
        }

        // 2. 再通过手机号查询
        user = userDao.findByPhone(identifier);
        if (user != null && verifyPassword(password, user)) {
            return user;
        }

        // 3. 最后通过邮箱查询（需验证邮箱格式）
        if (isValidEmail(identifier)) {
            user = userDao.findByEmail(identifier);
            if (user != null && verifyPassword(password, user)) {
                return user;
            }
        }

        return null; // 所有方式均未找到用户或密码错误
    }

    // 密码验证工具方法
    private boolean verifyPassword(String inputPassword, UserDB user) {
        return BCrypt.checkpw(inputPassword, user.getPassword());
    }

    // 简单邮箱格式验证
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }


    /**
     * 注册用户
     * @param username 用户名
     * @param phone 手机号
     * @param password 原始密码（需在调用前验证格式）
     * @return 注册结果信息
     */
    public String register(String username, String phone, String password) {
        // 1. 基础参数校验
        if (Objects.isNull(username) || Objects.isNull(phone) || Objects.isNull(password)) {
            return "请填写完整注册信息";
        }
        if (password.length() < 6) {
            return "密码长度至少6位";
        }

        // 2. 检查用户是否已存在（调用DAO查询）
        if (userDao.findByUsername(username) != null) {
            return "用户名已注册";
        }
        if (userDao.findByPhone(phone) != null) {
            return "手机号已注册";
        }

        // 3. 加密密码
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        // 4. 插入数据库（调用DAO新增用户）
        UserDB user = new UserDB();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(hashedPassword);
        if (userDao.insertUser(user)) { // 需在DAO中新增插入方法
            return "注册成功";
        }
        return "注册失败，请联系管理员";
    }

    public String updateUserInfo(UserDB user) {
        UserDB existingUser = userDao.findById(user.getUserId());
        if (existingUser == null) {
            return "用户不存在";
        }

        // 更新邮箱和手机号
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        // 处理密码：若前端传入密码为空，则保留原有密码
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        } else {
            // 不修改密码，使用原有密码
            existingUser.setPassword(existingUser.getPassword());
        }

        if (userDao.updateUser(existingUser, existingUser.getUserId())) {
            return "修改成功";
        }
        return "修改失败，请重试";
    }


}