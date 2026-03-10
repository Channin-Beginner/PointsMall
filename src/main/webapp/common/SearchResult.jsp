<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>搜索结果 - 积分商城</title>
    <link rel="stylesheet" href="/PointsMall/static/css/HomeStyle.css">
    <%@ include file="../../common/header.jsp" %>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        /* 去除回首页按钮的超链接样式 */
        .home-button a {
            text-decoration: none; /* 去除下划线 */
            color: inherit; /* 继承父元素颜色 */
        }
    </style>
</head>
<body>
<div class="header">
    <div class="logo">积分商城</div>
    <div class="search-bar">
        <input type="text" placeholder="搜索商品..." id="searchInput" value="${currentKeyword}">
        <button onclick="searchProducts()">搜索</button>
    </div>
</div>

<div class="announcement">
    <h2>搜索结果："${currentKeyword}"</h2>
    <p>共找到 ${fn:length(products)} 个商品</p>
</div>

<!-- 商品展示部分 -->
<div class="product-display">
    <c:if test="${empty products}">
        <div class="no-result">
            <i class="fas fa-search fa-4x"></i>
            <p>没有找到与"${currentKeyword}"相关的商品</p>
            <p>请尝试其他关键词或检查拼写</p>
        </div>
    </c:if>
    <c:forEach items="${products}" var="product">
        <div class="product-item"
             data-category="${product.categoryId}"
             data-id="${product.productId}"
             data-description="${fn:escapeXml(product.description)}"
             data-points="${product.pointsPrice}">
            <img src="${product.imageUrl}" alt="${product.productName}">
            <p>${product.productName}</p>
            <p>积分：${product.pointsPrice}</p>
        </div>
    </c:forEach>
</div>


<!-- 回首页按钮 -->
<div class="home-button" id="homeButton">
    <a href="${pageContext.request.contextPath}/home"><i class="fas fa-home"></i></a>
</div>

<!-- 弹窗元素 -->
<div id="productModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span> <!-- 关闭按钮 -->
        <img src="" alt="商品图片" id="modalProductImage">
        <h2 id="modalProductName"></h2>
        <p id="modalProductDescription"></p>
        <p id="modalProductPoints"></p>
        <div class="form-group">
            <label for="modalAddress">送货地址：</label>
            <input type="text" id="modalAddress" placeholder="请输入送货地址">
        </div>
        <div class="form-group">
            <label for="modalPhone">联系号码：</label>
            <input type="text" id="modalPhone" placeholder="请输入联系号码">
        </div>
        <button class="add-to-cart">加入购物车</button>
        <button class="buy-now">立即购买</button>
    </div>
</div>

<script>
    // 搜索函数
    function searchProducts() {
        const keyword = document.getElementById('searchInput').value.trim();
        if (keyword) {
            window.location.href = "${pageContext.request.contextPath}/search?keyword=" + encodeURIComponent(keyword);
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        // 声明所有需要的DOM元素（仅一次）
        const modal = document.getElementById('productModal');
        const modalProductImage = document.getElementById('modalProductImage');
        const modalProductName = document.getElementById('modalProductName');
        const modalProductPoints = document.getElementById('modalProductPoints');
        const addToCartBtn = document.querySelector('.add-to-cart');
        const buyNowBtn = document.querySelector('.buy-now');
        const closeBtn = document.querySelector('.modal .close');
        let currentProductId = 0;

        // 商品点击事件
        const productItems = document.querySelectorAll('.product-item');
        productItems.forEach(item => {
            item.addEventListener('click', () => {
                currentProductId = item.dataset.id;
                modalProductImage.src = item.querySelector('img').src;
                modalProductName.textContent = item.querySelector('p:first-of-type').textContent;
                modalProductPoints.textContent = "积分：" + item.dataset.points;
                modal.style.display = 'flex';
            });
        });

        // 关闭弹窗功能
        closeBtn.addEventListener('click', () => {
            modal.style.display = 'none';
        });

        window.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });

        // 登录检查
        function checkLoginAndRedirect(event) {
            const isLoggedIn = ${not empty sessionScope.userId};
            if (!isLoggedIn) {
                if (event) event.preventDefault();
                alert("请先登录！");
                window.location.href = "/PointsMall/loginPage";
                return false;
            }
            return true;
        }

        // 添加购物车按钮
        addToCartBtn.addEventListener('click', function(e) {
            if (!checkLoginAndRedirect(e)) return;
            fetch('${pageContext.request.contextPath}/addToCart', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ productId: currentProductId, quantity: 1 })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert(`已将${modalProductName.textContent}添加到购物车！`);
                        modal.style.display = 'none';
                    } else {
                        alert(data.message);
                    }
                });
        });

        // 立即购买按钮
        buyNowBtn.addEventListener('click', async function(e) {
            if (!checkLoginAndRedirect(e)) return;

            const address = modalAddress.value.trim() || ""; // 防止 null
            const phone = modalPhone.value.trim() || ""; // 防止 null
            const productId = currentProductId; // 当前商品ID
            const quantity = 1; // 立即购买默认数量为1


            // 验证必填字段
            if (!address || !phone) {
                return alert('请填写完整的地址和联系电话！');
            }

            // 检查是否包含非法字符（如引号）
            if (address.includes('"') || phone.includes('"')) {
                alert('地址或手机号不能包含引号');
                return;
            }

            // console.log('Address:', address);
            // console.log('Phone:', phone);
            // console.log('Product ID:', productId);
            // console.log('Quantity:', quantity);
            try {
                // 发送创建订单的请求（参考购物车结算的JSON格式）
                const response = await fetch('${pageContext.request.contextPath}/createOrder', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({
                        productId: productId,
                        quantity: quantity,
                        address: address,
                        phone: phone
                    })
                });

                // const text = await response.text(); // 添加此行
                // console.log('Raw Response:', text); // 添加此行
                // const data1 = JSON.parse(text); // 手动解析，方便定位错误

                const data = await response.json();
                console.log('Response:', data);
                if (data.success) {
                    alert('购买成功！订单已生成');
                    modal.style.display = 'none';

                    // 跳转到我的订单页面并刷新
                    window.location.href = '/PointsMall/mine';
                } else {
                    alert(`购买失败：${data.message}`);
                }
            } catch (error) {
                console.error('网络错误:', error);
                // alert('操作失败，请检查网络连接');
            }
        });
    });
</script>
</body>
</html>