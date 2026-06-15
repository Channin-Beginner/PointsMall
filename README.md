# PointsMall - 积分商城系统

## 项目简介
基于Java EE技术栈开发的积分商城系统，实现用户积分管理、商品兑换、订单处理等核心业务功能。采用传统的MVC架构，适合学习Java Web开发与企业级应用部署。

## 技术栈
- **后端**: Java 23 + Jakarta Servlet 6.1
- **构建工具**: Maven 3.x
- **数据库**: MySQL 8.0（预留JDBC接口）
- **加密**: jBCrypt密码哈希
- **JSON处理**: Jackson + org.json
- **前端**: JSP + JSTL + HTML5/CSS3
- **服务器**: Apache Tomcat 10+

## 核心功能模块

### 1. 用户管理
- 用户注册与登录（BCrypt加密）
- 个人信息维护
- 积分余额查询

### 2. 商品管理
- 商品列表展示
- 商品详情查看
- 库存管理

### 3. 积分交易
- 积分兑换商品
- 订单生成与状态跟踪
- 积分流水记录

### 4. 后台管理
- 管理员登录
- 商品上架/下架
- 订单审核

## 项目结构
```

PointsMall/
├── src/
│   ├── main/
│   │   ├── java/              # Java源代码
│   │   │   └── com/HemisphericConflict/PointsMall/
│   │   │       ├── controller/    # Servlet控制器
│   │   │       ├── service/       # 业务逻辑层
│   │   │       ├── dao/           # 数据访问层
│   │   │       ├── model/         # 实体类
│   │   │       └── util/          # 工具类
│   │   ├── resources/         # 配置文件
│   │   └── webapp/            # Web资源
│   │       ├── WEB-INF/
│   │       │   └── web.xml    # 部署描述符
│   │       ├── css/
│   │       ├── js/
│   │       └── *.jsp          # JSP页面
│   └── test/                  # 单元测试
├── pom.xml                    # Maven配置
└── mvnw                       # Maven Wrapper
```
## 快速开始

### 环境要求
- JDK 23+
- Maven 3.6+
- MySQL 8.0+
- Apache Tomcat 10+

### 构建项目
```
bash
# 克隆项目
cd PointsMall

# 编译打包
mvn clean package

# 或使用Maven Wrapper
./mvnw clean package
```
### 部署运行
```
bash
# 方式1: Tomcat部署
# 将target/PointsMall-1.0-SNAPSHOT.war复制到Tomcat的webapps目录
# 启动Tomcat服务器

# 方式2: Maven插件运行
mvn tomcat7:run
```
### 访问地址
- 首页: http://localhost:8080/PointsMall
- 管理后台: http://localhost:8080/PointsMall/admin

## 数据库设计

### 核心表结构
```
sql
-- 用户表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    points_price INT NOT NULL,
    stock INT DEFAULT 0,
    status TINYINT DEFAULT 1  -- 1上架 0下架
);

-- 订单表
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    points_spent INT NOT NULL,
    status TINYINT DEFAULT 0,  -- 0待处理 1已完成 2已取消
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```
## 技术亮点

### 1. 安全性设计
- **密码加密**: 使用jBCrypt进行单向哈希（防彩虹表攻击）
- **会话管理**: HttpSession用户状态跟踪
- **SQL注入防护**: PreparedStatement参数化查询

### 2. 架构分层
- **Controller层**: Servlet处理HTTP请求
- **Service层**: 业务逻辑封装
- **DAO层**: 数据库CRUD操作
- **Model层**: 数据实体映射

### 3. 依赖管理
- Maven标准化项目结构
- 阿里云镜像加速依赖下载
- WAR包打包便于部署

## 待完善功能
- 数据库连接池配置（HikariCP/Druid）
- 前端框架升级（Vue/React）
- RESTful API接口设计
- 单元测试覆盖（JUnit 5）
- Docker容器化部署

## 注意事项
1. 当前版本使用WAR打包，需部署到Servlet容器
2. MySQL驱动依赖已注释，实际使用需取消注释
3. JSTL标签库需根据Tomcat版本选择合适版本

## 扩展方向
- 集成Spring Boot简化配置
- 添加Redis缓存提升性能
- 实现消息队列处理异步订单
- 接入第三方支付接口

## 许可证
MIT License
```
