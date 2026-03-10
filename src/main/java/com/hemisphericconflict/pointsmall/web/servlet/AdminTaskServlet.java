package com.hemisphericconflict.pointsmall.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hemisphericconflict.pointsmall.dao.TaskDAO;
import com.hemisphericconflict.pointsmall.entity.TaskDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/task")
public class AdminTaskServlet extends HttpServlet {
    private final TaskDAO taskDAO = new TaskDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String action = request.getParameter("action");

        if ("all".equals(action)) {
            List<TaskDB> taskList = taskDAO.getAllTasks();
            response.getWriter().write(objectMapper.writeValueAsString(taskList));
        } else if ("get".equals(action)) { // 新增获取单个任务的 action
            int taskId = Integer.parseInt(request.getParameter("taskId"));
            TaskDB task = null;
            try {
                task = taskDAO.getTaskById(taskId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (task == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "任务不存在");
                return;
            }
            System.out.println(task);
            System.out.println(task.getTaskId());
            System.out.println(task.getTaskName());
            System.out.println(task.getPointsAwarded());
            System.out.println(task.getTaskDescription());
            System.out.println(task.getTaskStatus());
            System.out.println(task.getTaskType());
            System.out.println(task.getDisplayOrder());
            response.getWriter().write(objectMapper.writeValueAsString(task));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效操作");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Result result = new Result();
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) { // 模仿 UserServlet 的 add 逻辑
                TaskDB task = new TaskDB();
                task.setTaskName(request.getParameter("taskName"));
                task.setPointsAwarded(Integer.parseInt(request.getParameter("pointsAwarded")));
                task.setTaskDescription(request.getParameter("taskDescription"));
                task.setTaskStatus(request.getParameter("taskStatus"));
                task.setTaskType(request.getParameter("taskType"));

                if (taskDAO.AdminaddTask(task)) {
                    result.setSuccess(true);
                    result.setMessage("任务添加成功");
                } else {
                    result.setSuccess(false);
                    result.setMessage("任务添加失败");
                }
            } else if ("update".equals(action)) { // 模仿 UserServlet 的 update 逻辑
                System.out.println(request.getParameter("taskId"));
                System.out.println(request.getParameter("taskName"));
                System.out.println(request.getParameter("pointsAwarded"));
                System.out.println(request.getParameter("taskDescription"));
                System.out.println(request.getParameter("taskStatus"));
                System.out.println(request.getParameter("taskType"));
                TaskDB task = new TaskDB();
                task.setTaskId(Integer.parseInt(request.getParameter("taskId")));
                task.setTaskName(request.getParameter("taskName"));
                task.setPointsAwarded(Integer.parseInt(request.getParameter("pointsAwarded")));
                task.setTaskDescription(request.getParameter("taskDescription"));
                task.setTaskStatus(request.getParameter("taskStatus"));
                task.setTaskType(request.getParameter("taskType"));
//                task.setDisplayOrder(Integer.parseInt(request.getParameter("displayOrder")));

                if (taskDAO.AdminupdateTask(task)) {
                    result.setSuccess(true);
                    result.setMessage("任务更新成功");
                } else {
                    result.setSuccess(false);
                    result.setMessage("任务更新失败");
                }
            } else if ("delete".equals(action)) { // 模仿 UserServlet 的 delete 逻辑
                int taskId = Integer.parseInt(request.getParameter("taskId"));
                if (taskDAO.AdmindeleteTask(taskId)) {
                    result.setSuccess(true);
                    result.setMessage("任务删除成功");
                } else {
                    result.setSuccess(false);
                    result.setMessage("任务删除失败");
                }
            } else {
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

    private static class Result {
        private boolean success;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}