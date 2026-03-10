package com.hemisphericconflict.pointsmall.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hemisphericconflict.pointsmall.dao.UserDao;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;

@WebServlet("/userProfile")
public class UserServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        UserDB currentUser = (UserDB) session.getAttribute("user");

        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未登录");
            return;
        }

        String action = request.getParameter("action");
        if ("all".equals(action)) {
            // 仅管理员可获取所有用户
            if (currentUser.getRole() != 1) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "权限不足");
                return;
            }
            List<UserDB> userList = userDao.getAllUsers();
            response.getWriter().write(objectMapper.writeValueAsString(userList));
        } else if ("get".equals(action)) {
            // 获取单个用户信息：管理员可获取任意用户，普通用户只能获取自己
            int targetUserId = Integer.parseInt(request.getParameter("userId"));
            if (currentUser.getRole() != 1 && targetUserId != currentUser.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "权限不足");
                return;
            }
            UserDB user = userDao.getUserById(targetUserId);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户不存在");
                return;
            }
            response.getWriter().write(objectMapper.writeValueAsString(user));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效操作");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        UserDB currentUser = (UserDB) session.getAttribute("user");
        Result result = new Result();

        if (currentUser == null) {
            result.setSuccess(false);
            result.setMessage("未登录");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                UserDB user = new UserDB();
                user.setUsername(request.getParameter("username"));
                user.setPhone(request.getParameter("phone"));
                user.setEmail(request.getParameter("email"));
                user.setPassword(BCrypt.hashpw(request.getParameter("password"), BCrypt.gensalt()));
                user.setTotalPoints(Integer.parseInt(request.getParameter("points")));
                user.setRole(Integer.parseInt(request.getParameter("role")));

                if (userDao.isPhoneExists(user.getPhone())) {
                    result.setSuccess(false);
                    result.setMessage("手机号已存在");
                }
                if (userDao.isEmailExists(user.getEmail())) {
                    result.setSuccess(false);
                    result.setMessage("邮箱已存在");
                }
                if (userDao.isUsernameExists(user.getUsername())) {
                    result.setSuccess(false);
                    result.setMessage("用户名已存在");
                }
                if (userDao.addUser(user)) {
                    result.setSuccess(true);
                    result.setMessage("用户添加成功");
                } else {
                    result.setSuccess(false);
                    result.setMessage("用户添加失败（用户名或手机号已存在）");
                }
            }
            else if ("delete".equals(action)) {
                // 校验权限：仅管理员可删除用户，且禁止删除自己
                if (currentUser.getRole() != 1) {
                    result.setSuccess(false);
                    result.setMessage("权限不足，仅管理员可删除用户");
                    response.getWriter().write(objectMapper.writeValueAsString(result));
                    return;
                }

                int targetUserId = Integer.parseInt(request.getParameter("userId"));
                if (targetUserId == currentUser.getUserId()) {
                    result.setSuccess(false);
                    result.setMessage("禁止删除当前登录用户");
                    response.getWriter().write(objectMapper.writeValueAsString(result));
                    return;
                }

                // 校验目标用户是否存在
                UserDB targetUser = userDao.getUserById(targetUserId);
                if (targetUser == null) {
                    result.setSuccess(false);
                    result.setMessage("目标用户不存在");
                    response.getWriter().write(objectMapper.writeValueAsString(result));
                    return;
                }

                // 校验是否尝试删除管理员（可选，根据业务需求）
                if (targetUser.getRole() == 1) {
                    result.setSuccess(false);
                    result.setMessage("禁止删除管理员用户");
                    response.getWriter().write(objectMapper.writeValueAsString(result));
                    return;
                }

                // 执行删除
                boolean success = userDao.deleteUser(targetUserId);
                result.setSuccess(success);
                result.setMessage(success ? "删除用户成功" : "删除用户失败");
            }
            else if ("update".equals(action)) {
                // 解析表单数据
                UserDB user = new UserDB();
                user.setUserId(Integer.parseInt(request.getParameter("userId")));
                user.setUsername(request.getParameter("username"));
                user.setPhone(request.getParameter("phone"));
                user.setEmail(request.getParameter("email"));
                user.setPassword(request.getParameter("password"));
                user.setTotalPoints(Integer.parseInt(request.getParameter("totalPoints")));
                user.setRole(Integer.parseInt(request.getParameter("role")));

                if (userDao.isPhoneExists(user.getPhone())) {
                    result.setSuccess(false);
                    result.setMessage("手机号已存在");
                }
                if (userDao.isEmailExists(user.getEmail())) {
                    result.setSuccess(false);
                    result.setMessage("邮箱已存在");
                }
                if (userDao.isUsernameExists(user.getUsername())) {
                    result.setSuccess(false);
                    result.setMessage("用户名已存在");
                }
                boolean success = userDao.updateUserXinfo(user);
                result.setSuccess(success);
                result.setMessage(success ? "更新用户成功" : "更新用户失败");
            }
            else if ("Adminupdate".equals(action)) {
                // 解析表单数据
                UserDB user = new UserDB();
                user.setUserId(Integer.parseInt(request.getParameter("userId")));
                user.setUsername(request.getParameter("username"));
                user.setPhone(request.getParameter("phone"));
                user.setEmail(request.getParameter("email"));
                user.setPassword(request.getParameter("password"));


                // 校验唯一性（注意：需要排除当前用户自身）
                if (userDao.isPhoneExists(user.getPhone()) && !user.getPhone().equals(userDao.getUserById(user.getUserId()).getPhone())) {
                    throw new IllegalArgumentException("手机号已存在");
                }
                if (userDao.isEmailExists(user.getEmail()) && !user.getEmail().equals(userDao.getUserById(user.getUserId()).getEmail())) {
                    throw new IllegalArgumentException("邮箱已存在");
                }
                if (userDao.isUsernameExists(user.getUsername()) && !user.getUsername().equals(userDao.getUserById(user.getUserId()).getUsername())) {
                    throw new IllegalArgumentException("用户名已存在");
                }

                boolean success = userDao.updateUser(user,  user.getUserId());
                result.setSuccess(success);
                result.setMessage(success ? "更新用户成功" : "更新用户失败");
            }
            else {
                result.setSuccess(false);
                result.setMessage("无效操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败: " + e.getMessage());
        }

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    // Result 类保持不变
    private static class Result {
        private boolean success;
        private String message;

        public Result() {}
        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}