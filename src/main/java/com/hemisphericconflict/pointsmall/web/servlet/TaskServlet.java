package com.hemisphericconflict.pointsmall.web.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hemisphericconflict.pointsmall.entity.TaskDB;
import com.hemisphericconflict.pointsmall.dao.TaskDAO;
import com.hemisphericconflict.pointsmall.dao.SigninDAO;
import com.hemisphericconflict.pointsmall.entity.UserDB;
import com.hemisphericconflict.pointsmall.service.TaskService;
import com.hemisphericconflict.pointsmall.utils.LocalDateAdapter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/tasks")
public class TaskServlet extends HttpServlet {
    private final TaskService taskService = new TaskService();
    // 创建 Gson 实例并注册 LocalDate 适配器
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting() // 可选：格式化 JSON 输出
            .create();
    private final TaskDAO  taskDAO = new TaskDAO();
    private final SigninDAO signinDAO = new SigninDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            UserDB user = (UserDB) request.getSession().getAttribute("user");
            if (user == null) {
                response.setStatus(401);
                response.getWriter().write(gson.toJson(Map.of("error", "未登录")));
                return;
            }

            // 获取任务数据
            List<TaskDB> activeTasks = taskDAO.getActiveTasks();
            List<TaskDB> upcomingTasks = taskDAO.getUpcomingTasks();
            int completedTasks = taskService.getCompletedTasks(user.getUserId());
            int consecutiveDays = taskService.getConsecutiveSignDays(user.getUserId());
            LocalDate lastSignDate = signinDAO.getLastSignDate(user.getUserId()); // 获取最后签到日期

            // 获取用户任务数据
            Map<String, Object> data = new HashMap<>();
            data.put("totalPoints", user.getTotalPoints());
            data.put("consecutiveDays", taskService.getConsecutiveSignDays(user.getUserId()));
            data.put("completedTasks", taskService.getCompletedTasks(user.getUserId()));
            data.put("totalTasks", taskService.getTotalActiveTasks());
            data.put("activeTasks", activeTasks);
            data.put("upcomingTasks", upcomingTasks);
            data.put("signInTask", taskService.getSignInTask());
            data.put("lastSignDate", lastSignDate); // 新增最后签到日期
            // 返回 JSON 响应
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(gson.toJson(data));
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().write(gson.toJson(Map.of("error", "服务器错误")));
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String action = request.getParameter("action");

        try {
            UserDB user = (UserDB) request.getSession().getAttribute("user");
            if (user == null) {
                response.setStatus(401);
                response.getWriter().write(gson.toJson(Map.of("error", "未登录")));
                return;
            }

            if ("signIn".equals(action)) {
                // 处理签到逻辑
                Map<String, Object> result = taskService.processSignIn(user.getUserId());
                // 打印调试信息
                System.out.println("签到处理结果: " + gson.toJson(result));
                response.getWriter().write(gson.toJson(result));
                return; // 处理完签到后直接返回，不执行后续代码
            } else if ("add".equals(action) || "update".equals(action) || "delete".equals(action)) {
                // 处理任务管理相关操作
                Result result = new Result();

                try {
                    if ("add".equals(action)) {
                        // 解析表单数据
                        TaskDB task = new TaskDB();
                        task.setTaskName(request.getParameter("taskName"));
                        task.setTaskDescription(request.getParameter("taskDescription"));
                        task.setPointsAwarded(Integer.parseInt(request.getParameter("pointsAwarded")));
                        task.setTaskStatus(request.getParameter("taskStatus"));
                        task.setDisplayOrder(Integer.parseInt(request.getParameter("displayOrder")));
                        task.setTaskType(request.getParameter("taskType"));

                        boolean success = TaskDAO.addTask(task);
                        result.setSuccess(success);
                        result.setMessage(success ? "发布任务成功" : "发布任务失败");
                    } else if ("update".equals(action)) {
                        // 解析表单数据
                        TaskDB task = new TaskDB();
                        task.setTaskId(Integer.parseInt(request.getParameter("taskId")));
                        task.setTaskName(request.getParameter("taskName"));
                        task.setTaskDescription(request.getParameter("taskDescription"));
                        task.setPointsAwarded(Integer.parseInt(request.getParameter("pointsAwarded")));
                        task.setTaskStatus(request.getParameter("taskStatus"));
                        task.setDisplayOrder(Integer.parseInt(request.getParameter("displayOrder")));
                        task.setTaskType(request.getParameter("taskType"));

                        boolean success = TaskDAO.updateTask(task);
                        result.setSuccess(success);
                        result.setMessage(success ? "更新任务成功" : "更新任务失败");
                    } else if ("delete".equals(action)) {
                        int taskId = Integer.parseInt(request.getParameter("taskId"));
                        boolean success = TaskDAO.deleteTask(taskId);
                        result.setSuccess(success);
                        result.setMessage(success ? "删除任务成功" : "删除任务失败");
                    }

                    // 返回任务管理操作结果
                    System.out.println("任务管理操作结果: " + gson.toJson(result));
                    response.getWriter().write(gson.toJson(result));
                } catch (Exception e) {
                    e.printStackTrace();
                    result.setSuccess(false);
                    result.setMessage("操作失败: " + e.getMessage());
                    response.getWriter().write(gson.toJson(result));
                }
            } else {
                // 未知操作
                response.setStatus(400);
                response.getWriter().write(gson.toJson(Map.of("error", "无效操作")));
            }
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().write(gson.toJson(Map.of("error", "服务器错误")));
        }
    }

    private static class Result {
        private boolean success;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}