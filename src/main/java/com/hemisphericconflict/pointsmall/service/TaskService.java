package com.hemisphericconflict.pointsmall.service;
import com.hemisphericconflict.pointsmall.dao.SigninDAO;
import com.hemisphericconflict.pointsmall.dao.TaskDAO;
import com.hemisphericconflict.pointsmall.dao.UserDao;
import com.hemisphericconflict.pointsmall.entity.TaskDB;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import com.hemisphericconflict.pointsmall.utils.c3p0Tool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskService {
    private final TaskDAO taskDAO = new TaskDAO();
    private final UserDao userDao = new UserDao();
    private final SigninDAO signinDAO = new SigninDAO();

    // 获取连续签到天数
    public int getConsecutiveSignDays(int userId) throws SQLException {
        return signinDAO.getConsecutiveDays(userId);
    }

    // 获取已完成任务数（示例：签到是否完成）
    public int getCompletedTasks(int userId) throws SQLException {
        return signinDAO.hasSignedToday(userId) ? 1 : 0;
    }

    // 获取总活跃任务数
    public int getTotalActiveTasks() throws SQLException {
        return taskDAO.getActiveTasks().size();
    }

    // 获取签到任务信息
    public TaskDB getSignInTask() throws SQLException {
        return taskDAO.getSignInTask();
    }

    // 处理签到逻辑
    public Map<String, Object> processSignIn(int userId) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate lastSignDate = signinDAO.getLastSignDate(userId);
        System.out.println("获取签到任务信息：" + lastSignDate);

        // 检查是否在当天已签到或未到次日
        if (lastSignDate != null && lastSignDate.isEqual(today)) {
            result.put("error", "今日已签到");
            return result;
        }

        // 获取签到任务奖励
        TaskDB signInTask = taskDAO.getSignInTask();
        int pointsAwarded = signInTask.getPointsAwarded();
        System.out.println("签到任务奖励积分：" + pointsAwarded);

        // 更新用户积分
        UserDB user = userDao.findById(userId);
        int newTotalPoints = user.getTotalPoints() + pointsAwarded;
        System.out.println("更新用户积分：" + newTotalPoints);
        userDao.updateUserPoints(userId, newTotalPoints);

        // 更新签到记录
        int newConsecutiveDays = signinDAO.recordSignIn(userId, today);

        result.put("success", true);
        result.put("totalPoints", newTotalPoints);
        result.put("consecutiveDays", newConsecutiveDays);
        result.put("pointsAwarded", pointsAwarded);
        System.out.println("返回给 Servlet 的结果：" + result);
        return result;
    }
}