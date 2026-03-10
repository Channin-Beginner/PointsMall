<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>积分商城 - 我的</title>
    <!-- 添加favicon引用 -->
    <%@ include file="../../common/header.jsp" %>
    <link rel="stylesheet" href="/PointsMall/static/css/MineStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
<div class="container">
    <div class="header">
        <div class="logo">积分商城</div>
        <div class="user-info">
            <div class="user-name">${sessionScope.username}</div>
            <!-- 添加退出功能 -->
            <a href="/PointsMall/loginPage" class="logout-link">
                <i class="fas fa-sign-out-alt"></i> 退出
            </a>
        </div>
    </div>
    <div class="content-container">
        <div class="sidebar">
            <div class="sidebar-item active" data-target="orders">
                <i class="fas fa-list"></i> 我的订单
            </div>
            <div class="sidebar-item" data-target="points">
                <i class="fas fa-star"></i> 我的积分
            </div>
            <div class="sidebar-item" data-target="profile">
                <i class="fas fa-user-cog"></i> 账号管理
            </div>
        </div>
        <div class="content-area" id="orders">
            <div class="content-title">我的订单</div>
            <div class="order-filters">
                <div class="order-filter active" data-status="all">全部订单</div>
                <div class="order-filter" data-status="1">待付款</div>
                <div class="order-filter" data-status="2">待发货</div>
                <div class="order-filter" data-status="3">待收货</div>
                <div class="order-filter" data-status="4">已完成</div>
            </div>
            <div class="order-list">
                <!-- 动态渲染订单列表 -->
                <c:choose>
                    <c:when test="${empty orders}">
                        <div class="empty-orders">暂无订单记录</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${orders}" var="order">
                            <%--                            <!-- 调试：打印订单 ID -->--%>
                            <%--                            <div>调试：orderId = ${order.orderId}</div>--%>
                            <!-- 过滤状态为5的订单 -->
                            <c:if test="${order.status != 5}">
                                <div class="order-item" data-status="${order.status}" data-order-id="${order.orderId}">
                                    <div class="order-header">
                                        <span class="order-number">订单号：${order.orderId}</span>
                                        <span class="order-date"><fmt:formatDate value="${order.orderTime}" pattern="yyyy-MM-dd"/></span>
                                        <span class="order-status ${order.status == 4 ? 'status-delivered' : 'status-pending'}">
                                                ${order.status == 1 ? '待付款' : order.status == 2 ? '待发货' : order.status == 3 ? '待收货' : '已完成'}
                                        </span>
                                    </div>
                                    <div class="order-products">
                                        <div class="order-product">
                                            <!-- 商品图片：从OrderDB的ProductDB中获取 -->
                                            <img src="${order.product.imageUrl != null ? order.product.imageUrl : '/PointsMall/static/images/default-product.jpg'}"
                                                 alt="${order.product.productName}"
                                                 class="product-image">

                                            <!-- 商品名：从OrderDB的ProductDB中获取 -->
                                            <div>
                                                <div class="product-name">商品：${order.product.productName}</div>
                                                <div class="product-quantity">x${order.quantity}</div>
                                            </div>
                                        </div>
                                        <div class="order-footer">
                                            <div class="order-total">总计：${order.totalPoints} 积分</div>
                                            <div class="order-actions">
                                                <a href="#" class="btn btn-primary view-details">查看详情</a>

                                                <!-- Mine.jsp 按钮修改 -->
                                                <c:if test="${order.status == 1}">
                                                    <c:if test="${not empty order.orderId}">
                                                        <!-- 关键修改：href 改为 void(0) -->
                                                        <a href="javascript:void(0)"
                                                           class="btn btn-success pay-order"
                                                           data-order-id="${order.orderId}"
                                                           data-total-points="${order.totalPoints}">
                                                            立即付款
                                                        </a>
                                                        <c:if test="${not empty order.orderId}">
                                                            <a href="javascript:void(0)" class="btn btn-danger cancel-order" data-order-id="${order.orderId}">取消订单</a>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>

                                                <c:if test="${order.status == 2}">
                                                    <a href="#" class="btn btn-secondary remind-delivery">提醒发货</a>
                                                </c:if>

                                                <c:if test="${order.status == 4}">
                                                    <a href="#" class="btn btn-secondary repurchase">再次购买</a>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="content-area" id="points" style="display: none;">
            <div class="content-title">我的积分</div>
            <div class="points-container">
                <!-- 从数据库获取的当前积分 -->
                <div class="points-summary">
                    <div class="points-total">${user.totalPoints}</div>
                    <div class="points-label">当前积分</div>
                </div>

                <!-- 积分明细（从数据库获取） -->
                <div class="points-history">
                    <div class="content-title" style="margin-top: 0;">积分明细</div> <!-- 去掉“消费记录” -->
                    <c:choose>
                        <c:when test="${empty pointsLogs}">
                            <div class="empty-points">暂无积分记录</div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${pointsLogs}" var="log">
                                <div class="points-item">
                                    <div>
                                        <fmt:formatDate value="${log.createTime}" pattern="yyyy-MM-dd HH:mm"/>
                                        <span>消费</span> <!-- 保留变动类型，如需隐藏可删除 -->
                                        <span class="points-change-value">
                            -${log.changePoints} 积分
                        </span>
                                    </div>
                                    <div class="points-change points-decrease">
                                            ${log.changePoints}
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <div class="content-area" id="profile" style="display: none;">
            <div class="content-title">账号管理</div>
            <div class="profile-section">
                <div class="profile-form">
                    <!-- 用户名 -->
                    <div class="form-group">
                        <label for="username">用户名</label>
                        <input type="text" id="username" value="${user.username}" readonly> <!-- 只读，通过模态窗口修改 -->
                    </div>
                    <!-- 邮箱 -->
                    <div class="form-group">
                        <label for="email">邮箱</label>
                        <input type="email" id="email" value="${user.email}">
                    </div>
                    <!-- 手机号码 -->
                    <div class="form-group">
                        <label for="phone">手机号码</label>
                        <input type="tel" id="phone" value="${user.phone}">
                    </div>
                    <!-- 密码（带显示/隐藏图标） -->
                    <div class="form-group password-group">
                        <label for="password">密码</label>
                        <input type="password" id="password" placeholder="输入当前密码">
                        <span class="toggle-password" onclick="togglePassword('password', this)">
                    <i class="fas fa-eye"></i>
                </span>
                    </div>
                    <!-- 修改按钮 -->
                    <div class="form-actions">
                        <button class="btn btn-primary" id="openModifyModal">修改</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 模态窗口（修改信息） -->
<div class="modal" id="modifyModal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h3>修改用户信息</h3>
        <form id="modifyForm">
            <input type="hidden" name="userId" value="${user.userId}"> <!-- 隐藏字段存储用户 ID -->
            <!-- 新用户名 -->
            <div class="form-group">
                <label for="modalUsername">用户名</label>
                <input type="text" id="modalUsername" name="username"
                       placeholder="请输入新用户名">
            </div>
            <!-- 新邮箱 -->
            <div class="form-group">
                <label for="modalEmail">邮箱</label>
                <input type="email" id="modalEmail" name="email"
                       placeholder="请输入新邮箱">
            </div>
            <!-- 新手机号码 -->
            <div class="form-group">
                <label for="modalPhone">手机号码</label>
                <input type="tel" id="modalPhone" name="phone"
                       placeholder="请输入新手机号">
            </div>
            <!-- 新密码 -->
            <div class="form-group password-group">
                <label for="modalPassword">新密码</label>
                <input type="password" id="modalPassword" name="password"
                       placeholder="请输入新密码" >
                <span class="toggle-password" onclick="togglePassword('modalPassword', this)">
                    <i class="fas fa-eye"></i>
                </span>
            </div>
            <!-- 保存按钮 -->
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">保存修改</button>
            </div>
        </form>
    </div>
</div>

<!-- 新增的“回到首页”按钮 -->
<a href="/PointsMall/home" class="home-button">
    <i class="fas fa-home"></i>
</a>

<script>
    // 复用注册页面的密码显示/隐藏函数
    function togglePassword(inputId, element) {
        const input = document.getElementById(inputId);
        const icon = element.querySelector('i');
        if (input.type == 'password') {
            input.type = 'text';
            icon.className = 'fas fa-eye-slash';
        } else {
            input.type = 'password';
            icon.className = 'fas fa-eye';
        }
    }



    document.addEventListener('DOMContentLoaded', () => {
        // 初始化时获取用户信息（从后端接口）
        fetch('/PointsMall/userProfile?action=get')
            .then(response => response.json())
            .then(user => {
                document.getElementById('email').value = user.email;
                document.getElementById('phone').value = user.phone;
                document.getElementById('modalUserId').value = user.userId;
            })
            .catch(error => console.error('获取用户信息失败:', error));

        // 打开模态窗口
        document.getElementById('openModifyModal').addEventListener('click', () => {
            const modal = document.getElementById('modifyModal');
            // 从当前表单获取现有值（可优化为从后端获取最新值）
            const user = {
                username: document.getElementById('username').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value
            };
            // 填充模态窗口（用户名只读，不允许修改）
            document.getElementById('modalUsername').value = user.username;
            document.getElementById('modalEmail').value = user.email;
            document.getElementById('modalPhone').value = user.phone;
            modal.style.display = 'block';
        });

        // 关闭模态窗口
        document.querySelector('.close').addEventListener('click', () => {
            document.getElementById('modifyModal').style.display = 'none';
        });

        // 提交修改表单
        document.getElementById('modifyForm').addEventListener('submit', (e) => {
            e.preventDefault();

            const form = e.target;
            const formData = new FormData(form);

            fetch('/PointsMall/userProfile?action=Adminupdate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams(formData).toString()
            })
                .then(response => response.json())
                .then(data => {
                    alert(data.message);
                    if (data.success) {
                        document.getElementById('modifyModal').style.display = 'none';
                        // 更新页面显示（可重新获取用户信息）
                        location.reload();
                    }
                })
                .catch(error => console.error('修改失败:', error));
        });
    });


    // 修改按钮事件绑定，使用 bind 绑定 this
    document.querySelectorAll('.pay-order').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const orderId = this.getAttribute('data-order-id');
            payOrder.bind(this)(orderId); // 使用 bind 绑定当前按钮的 this
        });
    });

    document.querySelectorAll('.cancel-order').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const orderId = this.getAttribute('data-order-id');
            deleteOrder(orderId); // 调用删除函数
        });
    });

    function updateOrderStatus(orderId, status) {
        if (!orderId) {
            alert('订单ID不能为空');
            return;
        }
        // 构造参数对象
        const params = new URLSearchParams();
        params.append('orderId', orderId);
        params.append('status', status);

        fetch('/PointsMall/updateOrderStatus', {
            method: 'POST',
            body: params, // 通过请求体传递参数
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded', // 关键：设置请求头
            }
        })
            .then(response => response.text())
            .then(data => {
                if (data.includes('成功')) {
                    location.reload();
                } else {
                    alert('操作失败：' + data);
                }
            })
            .catch(error => {
                console.error('网络错误:', error);
                alert('操作失败，请检查网络');
            });
    }

    function deleteOrder(orderId) {
        if (!orderId) {
            alert('订单ID不能为空');
            return;
        }
        if (confirm('确认要永久删除此订单吗？')) {
            const params = new URLSearchParams();
            params.append('orderId', orderId);
            console.log(params.toString());

            fetch('/PointsMall/deleteOrder', {
                method: 'POST',
                body: params.toString(),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
                .then(response => response.text())
                .then(data => {
                    if (data == '操作成功') { // 严格匹配后端返回的成功信息
                        alert('订单已删除');
                        location.reload();
                    } else {
                        alert(data); // 显示具体失败原因，如“删除失败：无效的订单ID”
                    }
                })
                .catch(error => {
                    console.error('网络请求失败:', error);
                    alert('操作失败，请检查网络连接');
                });
        }
    }


    // 立即付款函数
    function payOrder(orderId) {
        if (!orderId) {
            alert('订单ID不能为空');
            return;
        }

        // 从当前按钮获取积分值（this 已绑定到按钮）
        const totalPoints = parseInt(this.getAttribute('data-total-points')); // 直接使用 this

        if (isNaN(totalPoints)) {
            alert('积分数据异常，请刷新页面');
            return;
        }

        if (confirm(`确认支付该订单？将扣除 ${totalPoints} 积分`)) {
            const params = new URLSearchParams();
            params.append('orderId', orderId);

            fetch('/PointsMall/payOrder', {
                method: 'POST',
                body: params,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
                .then(response => response.text())
                .then(data => {
                    if (data.includes('成功')) {
                        alert('付款成功');
                        location.reload();
                    } else {
                        alert(data);
                    }
                })
                .catch(error => {
                    console.error('支付请求失败:', error);
                    alert('操作失败，请检查网络连接');
                });
        }
    }


    document.addEventListener('DOMContentLoaded', function() {
        const sidebarItems = document.querySelectorAll('.sidebar-item');
        const contentAreas = document.querySelectorAll('.content-area');
        const orderFilters = document.querySelectorAll('.order-filter');
        const orderItems = document.querySelectorAll('.order-item');
        // const saveBtn = document.querySelector('.save-btn');
        const cancelBtn = document.querySelector('.cancel-btn');

        // 侧边栏切换
        sidebarItems.forEach(item => {
            item.addEventListener('click', function() {
                sidebarItems.forEach(i => i.classList.remove('active'));
                this.classList.add('active');

                const target = this.getAttribute('data-target');
                contentAreas.forEach(area => {
                    area.style.display = 'none';
                });
                document.getElementById(target).style.display = 'block';
            });
        });

        // 订单筛选
        orderFilters.forEach(filter => {
            filter.addEventListener('click', function() {
                orderFilters.forEach(f => f.classList.remove('active'));
                this.classList.add('active');

                const status = this.getAttribute('data-status');
                orderItems.forEach(item => {
                    if (status == 'all' || item.getAttribute('data-status') == status) {
                        item.style.display = 'block';
                    } else {
                        item.style.display = 'none';
                    }
                });
            });
        });

        // 订单操作按钮
        document.querySelectorAll('.view-details').forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                const orderId = this.closest('.order-item').querySelector('.order-number').textContent.split('：')[1];
                alert(`查看订单详情：${orderId}`);
            });
        });

        document.querySelectorAll('.remind-delivery').forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                const orderId = this.closest('.order-item').querySelector('.order-number').textContent.split('：')[1];
                alert(`已提醒商家发货，订单号：${orderId}`);
            });
        });

        document.querySelectorAll('.repurchase').forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                const orderId = this.closest('.order-item').querySelector('.order-number').textContent.split('：')[1];
                alert(`再次购买：${orderId}`);
            });
        });
    });
</script>
</body>
</html>