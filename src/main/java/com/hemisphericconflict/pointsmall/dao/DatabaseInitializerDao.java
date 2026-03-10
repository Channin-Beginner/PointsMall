package com.hemisphericconflict.pointsmall.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializerDao {
    private static final ComboPooledDataSource dataSource = new ComboPooledDataSource();

    public static void main(String[] args) {
        try {
            createDatabase();
            createAndPopulateUserTable();
            createAndPopulateCategoryTable();
            createAndPopulateProductTable();
            createCartTable();
            createAndPopulateOrderTable();
            createAndPopulatePointsLogTable();
            // 新增任务定义表
            createTaskDefinitionTable();
            // 新增用户签到记录表
            createUserSigninTable();
            System.out.println("数据库初始化完成！");
        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dataSource.close(); // 关闭连接池
        }
    }

    private static void createDatabase() throws SQLException {
        // 创建数据库时需要使用无数据库名的连接
        String url = "jdbc:mysql://localhost:3306?serverTimeZone=UTC";
        try (Connection conn = getConnectionWithoutDB(url);
             Statement stmt = conn.createStatement()) {
            String createDbSql = "CREATE DATABASE IF NOT EXISTS market " +
                    "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(createDbSql);
            System.out.println("数据库 market 创建成功。");
        }
    }

    // 获取不指定数据库的连接
    private static Connection getConnectionWithoutDB(String url) throws SQLException {
        try {
            // 使用DriverManager创建临时连接（用于创建数据库）
            return java.sql.DriverManager.getConnection(url, "root", "Hlr17806756371");
        } catch (SQLException e) {
            System.err.println("创建数据库连接失败: " + e.getMessage());
            throw e;
        }
    }

    // 创建并填充用户表
    private static void createAndPopulateUserTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS user (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL," +
                    "phone VARCHAR(20) UNIQUE NOT NULL," +
                    "email VARCHAR(100) UNIQUE," + // 新增邮箱字段（唯一约束）
                    "password VARCHAR(255) NOT NULL," +
                    "total_points INT DEFAULT 0," +
                    "role TINYINT DEFAULT 0 COMMENT '0:普通用户, 1:管理员'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createTableSql);
            System.out.println("用户表（user）创建成功。");

            String insertDataSql = "INSERT INTO user (username, phone, email, password, total_points, role) VALUES " + // 添加email字段
                    "('admin', '17806756371', 'admin@example.com', '$2a$10$...', 0, 1), " + // 管理员邮箱
                    "('张三', '13800138001', 'zhangsan@example.com', '$2a$10$...', 5000, 0), " +
                    "('李四', '13900139002', 'lisi@example.com', '$2a$10$...', 3200, 0), " +
                    "('王五', '13700137003', 'wangwu@example.com', '$2a$10$...', 8700, 0), " +
                    "('赵六', '13600136004', 'zhaoliu@example.com', '$2a$10$...', 1200, 0), " +
                    "('钱七', '13500135005', 'qianqi@example.com', '$2a$10$...', 6300, 0)";
            stmt.executeUpdate(insertDataSql);
            System.out.println("用户数据插入成功");
        }
    }

    // 创建并填充商品分类表
    private static void createAndPopulateCategoryTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS category (" +
                    "category_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "category_name VARCHAR(50) UNIQUE NOT NULL" +
                    ")";
            stmt.executeUpdate(createTableSql);
            System.out.println("商品分类表（category）创建成功。");

            // 修改后的商品类别名称
            String insertDataSql = "INSERT INTO category (category_name) VALUES " +
                    "('礼品卡'), " +
                    "('电子产品'), " +
                    "('生活家居'), " +
                    "('服饰箱包'), " +
                    "('美食饮品'), " +
                    "('旅行服务'), " +
                    "('游戏娱乐')"; // 新增类别，原5个变为7个
            stmt.executeUpdate(insertDataSql);
            System.out.println("商品分类数据插入成功");
        }
    }

    // 创建并填充商品表
    private static void createAndPopulateProductTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS product (" +
                    "product_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "category_id INT NOT NULL," +
                    "product_name VARCHAR(100) NOT NULL," +
                    "points_price INT NOT NULL," +
                    "stock INT DEFAULT 0," +
                    "description VARCHAR(200)," +
                    "image_url VARCHAR(200)," +
                    "is_active TINYINT DEFAULT 1," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createTableSql);
            System.out.println("商品表（product）创建成功。");

            // 修改category_id对应的类别（需与新类别顺序一致）
            String insertDataSql = "INSERT INTO product (category_id, product_name, points_price, stock, description, image_url) VALUES " +
                    // 礼品卡（category_id=1）
                    "(1, '500元礼品卡', 50000, 100, '通用积分礼品卡', 'static/images/500元礼品卡.png'), " +
                    // 电子产品（category_id=2）
                    "(2, '智能手表', 2500, 50, '多功能运动智能手表', 'static/images/智能手表.png'), " +
                    // 生活家居（category_id=3）
                    "(3, '空气净化器', 4800, 32, '高效去除PM2.5', 'static/images/空气净化器.png'), " +
                    // 服饰箱包（category_id=4）
                    "(4, '商务双肩包', 3000, 80, '防水耐磨商务包', 'static/images/商务双肩包.png'), " +
                    // 美食饮品（category_id=5）
                    "(5, '有机坚果礼盒', 800, 120, '精选坚果组合', 'static/images/有机坚果礼盒.png'), " +
                    // 旅行服务（category_id=6）
                    "(6, '三亚酒店套餐', 20000, 20, '三亚海景酒店3天2晚套餐', 'static/images/三亚酒店套餐.png'), " +
                    // 游戏娱乐（category_id=7）
                    "(7, '游戏点卡', 1000, 500, '通用游戏充值点卡', 'static/images/游戏点卡.png')";
            stmt.executeUpdate(insertDataSql);
            System.out.println("商品数据插入成功");
        }
    }


    //购物车表
    private static void createCartTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS cart (" +
                    "cart_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "product_id INT NOT NULL," +
                    "quantity INT DEFAULT 1," +
                    "added_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createTableSql);
            System.out.println("购物车表（cart）创建成功。");
        }
    }

    // 创建并填充订单表
    private static void createAndPopulateOrderTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS `order` (" +
                    "order_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "product_id INT NOT NULL," +
                    "quantity INT DEFAULT 1," +
                    "address VARCHAR(200) NOT NULL," +
                    "status TINYINT DEFAULT 1," +
                    "total_points INT NOT NULL," +
                    "order_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createTableSql);
            System.out.println("订单表（order）创建成功。");

            String insertDataSql = "INSERT INTO `order` (user_id, product_id, quantity, address, total_points) VALUES " +
                    "(1, 3, 2, '北京市朝阳区建国路88号', 1600), " +
                    "(2, 1, 1, '上海市浦东新区张江高科技园区', 2500), " +
                    "(3, 5, 3, '广州市天河区珠江新城', 900), " +
                    "(4, 4, 2, '深圳市南山区科技园', 3000), " +
                    "(5, 2, 1, '杭州市西湖区文三路', 4800)";
            stmt.executeUpdate(insertDataSql);
            System.out.println("订单数据插入成功");
        }
    }

    // 创建任务定义表
    private static void createTaskDefinitionTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS task_definition (" +
                    "task_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "task_name VARCHAR(100) NOT NULL," +
                    "task_description VARCHAR(255) NOT NULL," +
                    "points_awarded INT NOT NULL," +
                    "task_status ENUM('active', 'upcoming', 'inactive') NOT NULL DEFAULT 'upcoming'," +
                    "display_order INT NOT NULL DEFAULT 0," +
                    "task_type ENUM('daily', 'special') NOT NULL DEFAULT 'daily'," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createTableSql);
            System.out.println("任务定义表（task_definition）创建成功。");

            // 插入任务定义数据
            String insertDataSql = "INSERT INTO task_definition (task_name, task_description, points_awarded, task_status, display_order, task_type) VALUES " +
                    "('每日登录', '每天首次登录奖励', 10, 'active', 1, 'daily'), " +
                    "('每日签到', '每日签到奖励', 20, 'active', 0, 'special'), " +
                    "('发表评论', '在任意文章下发表评论', 20, 'upcoming', 2, 'daily'), " +
                    "('点赞文章', '点赞5篇喜欢的文章', 30, 'upcoming', 3, 'daily'), " +
                    "('分享内容', '分享到社交媒体', 25, 'upcoming', 4, 'daily'), " +
                    "('观看视频', '观看并完成一个视频', 35, 'upcoming', 5, 'daily')";
            stmt.executeUpdate(insertDataSql);
            System.out.println("任务定义数据插入成功");
        }
    }

    // 创建用户签到记录表
    private static void createUserSigninTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS user_signin (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "signin_date DATE NOT NULL," +
                    "consecutive_days INT DEFAULT 1," +
                    "UNIQUE KEY (user_id, signin_date)," +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createTableSql);
            System.out.println("用户签到记录表（user_signin）创建成功。");
        }
    }

    // 创建并填充积分变动表
    private static void createAndPopulatePointsLogTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSql = "CREATE TABLE IF NOT EXISTS points_log (" +
                    "log_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "change_type TINYINT NOT NULL," +
                    "change_points INT NOT NULL," +
                    "before_points INT NOT NULL," +
                    "after_points INT NOT NULL," +
                    "order_id INT," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (order_id) REFERENCES `order`(order_id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createTableSql);
            System.out.println("积分变动表（points_log）创建成功。");

            String insertDataSql = "INSERT INTO points_log (user_id, change_type, change_points, before_points, after_points, order_id) VALUES " +
                    "(1, 2, -1600, 6600, 5000, 1), " +
                    "(2, 2, -2500, 5700, 3200, 2), " +
                    "(3, 2, -900, 9600, 8700, 3), " +
                    "(4, 2, -3000, 4200, 1200, 4), " +
                    "(5, 2, -4800, 11100, 6300, 5)";
            stmt.executeUpdate(insertDataSql);
            System.out.println("积分变动数据插入成功");
        }
    }
}