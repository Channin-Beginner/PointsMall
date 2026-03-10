<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>订单确认 - 积分商城</title>
    <!-- 添加favicon引用 -->
    <%@ include file="../../common/header.jsp" %>
    <link rel="stylesheet" href="/PointsMall/static/css/OrderconfirmStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
<div class="order-confirm-container">
    <div class="announcement">
        <h2 style="margin: 0; font-size: 24px;">订单确认页</h2>
    </div>

    <table>
        <thead>
        <tr>
            <th>商品</th>
            <th>积分</th>
            <th>数量</th>
        </tr>
        </thead>
        <tbody id="orderItems">
        <!-- 示例商品行 -->
        <tr>
            <td>
                <img src="https://via.placeholder.com/50" alt="耿鬼" class="product-image">
                <span style="margin-left: 12px;">耿鬼</span>
            </td>
            <td><span class="points">100</span></td>
            <td><span id="quantity">1</span></td>
        </tr>
        <!-- 可以动态插入多个商品 -->
        </tbody>
        <tfoot>
        <tr class="total-section">
            <td colspan="2">总积分：<span id="totalPoints">100</span></td>
            <td></td>
        </tr>
        </tfoot>
    </table>

    <div class="button-group">
        <button onclick="goBackToCart()">返回购物车</button>
        <button onclick="confirmOrder()">确认订单</button>
    </div>
    <!-- 浮标按钮 -->
    <div class="home-button" onclick="window.location.href='http://localhost:8080/SpotsMarket_war_exploded/Home.jsp'">
        <i class="fas fa-home"></i>
    </div>
    <!-- 收货地址部分 -->
    <div class="address-section" style="margin: 20px 0; padding: 15px; background-color: #f9f9f9; border-radius: 6px;">
        <label for="deliveryAddress" style="display: block; font-weight: bold; margin-bottom: 8px;">收货地址：</label>
        <input type="text" id="deliveryAddress" class="address-input" style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;" />
    </div>
</div>

<script>
    function renderOrderItems() {
        const tbody = document.getElementById('orderItems');
        let totalPoints = 0;

        tbody.innerHTML = ''; // 清空原有数据

        const orderItems = JSON.parse(localStorage.getItem('orderItems')) || [];

        if (orderItems.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">暂无商品</td></tr>';
            document.getElementById('totalPoints').textContent = 0;
            return;
        }

        orderItems.forEach(item => {
            const row = document.createElement('tr');

            row.innerHTML = `
                    <td>
                        <img src="https://via.placeholder.com/50" alt="${item.name}" class="product-image">
                        <span style="margin-left: 12px;">${item.name}</span>
                    </td>
                    <td><span class="points">${item.points}</span></td>
                    <td><span>${item.quantity}</span></td>
                `;

            tbody.appendChild(row);

            totalPoints += item.points * item.quantity;
        });

        document.getElementById('totalPoints').textContent = totalPoints;
    }

    function confirmOrder() {
        alert("订单提交成功！感谢您的兑换！");

        // 可在此处添加跳转到支付完成页或清空购物车等逻辑
    }

    function goBackToCart() {
        window.location.href = 'Shoppingcart.jsp';
    }

    window.onload = function () {
        renderOrderItems();

        // 获取默认地址
        const defaultAddress = localStorage.getItem('defaultAddress') || '未设置收货地址';

        // 设置为输入框的值
        const addressInput = document.getElementById('deliveryAddress');
        addressInput.value = defaultAddress;

        // 监听输入变化（仅临时生效）
        addressInput.addEventListener('input', function () {
            localStorage.setItem('tempAddress', this.value); // 临时保存
        });
    };
</script>
</body>
</html>