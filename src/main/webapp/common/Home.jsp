<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>积分商城首页</title>
  <link rel="stylesheet" href="/PointsMall/static/css/HomeStyle.css">
  <!-- 添加favicon引用 -->
  <%@ include file="../../common/header.jsp" %>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
<!-- 调试信息：检查数据是否传递到JSP -->
<div style="display:none;">
  <h3>DEBUG信息：</h3>
  <p>分类数量: ${fn:length(categories)}</p>
  <p>商品数量: ${fn:length(products)}</p>
  <c:if test="${not empty categories}">
    <h4>分类列表:</h4>
    <c:forEach items="${categories}" var="c">
      <p>${c.categoryId} - ${c.categoryName} - ${c.iconClass}</p>
    </c:forEach>
  </c:if>
</div>
<div class="header" id="header">
  <div class="logo">积分商城</div>

  <!-- 修改搜索按钮的点击事件 -->
  <div class="search-bar">
    <input type="text" placeholder="搜索商品..." id="searchInput">
    <button onclick="searchProducts()">搜索</button>
  </div>
</div>

<%-- 在 <body> 标签内部添加新的用户信息浮框 --%>
<div class="user-float-container" id="userFloatBox" style="display:none;">
  <div class="user-float-icon">
    <i class="fas fa-user-circle"></i>
  </div>
  <div class="user-float-name" id="userFloatName"></div>
  <div class="user-float-points">积分：<span id="userFloatPoints"></span></div>
</div>

<div class="announcement">
  <h2>积分兑换，惊喜不断</h2>
  <p>使用您的积分兑换精选商品，享受专属优惠</p>
</div>


<!-- 分类部分 -->
<div class="category-section">
  <div class="category-item active" data-category="all">
    <a href="${pageContext.request.contextPath}/home?category=all">
      <i class="fas fa-th"></i>
      <p>全部</p>
    </a>
  </div>

  <c:forEach items="${categories}" var="category">
    <div class="category-item" data-category="${category.categoryId}">
      <a href="${pageContext.request.contextPath}/home?category=${category.categoryId}">
        <i class="fas ${category.iconClass}"></i>
        <p>${category.categoryName}</p>
      </a>
    </div>
  </c:forEach>
</div>

<!-- 商品部分 -->
<div class="product-display">
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

<div class="float-bar">
  <a href="/PointsMall/cart"><i class="fas fa-shopping-cart"></i> 购物车</a>
  <a href="/PointsMall/mine"><i class="fas fa-user"></i> 我的</a>
  <a href="/PointsMall/common/Consulter.jsp"><i class="fas fa-headset"></i> 客服</a>
  <a href="/PointsMall/user/Assignment.jsp"><i class="fas fa-calendar-alt"></i> 每日任务</a>
  <a href="#" class="back-to-top"><i class="fas fa-arrow-up"></i> 回顶部</a>
</div>

<div class="home-button" id="homeButton">
  <i class="fas fa-home"></i>
</div>

<footer>
  <div class="container">
    <div class="footer-section">
      <div class="logo">
        <i class="fas fa-store" style="color: #007bff; font-size: 24px; margin-right: 10px;"></i>
        <h2 style="margin: 0; color: #007bff;">积分商城</h2>
      </div>
      <p style="color: #666; line-height: 1.6;">
        轻松积累积分，兑换心仪商品，享受更多福利
      </p>
      <div class="social-icons">
        <a href="#"><i class="fab fa-weibo"></i></a>
        <a href="#"><i class="fab fa-weixin"></i></a>
        <a href="#"><i class="fab fa-instagram"></i></a>
      </div>
    </div>
    <div class="footer-section">
      <h3 style="margin-top: 0; color: #333; margin-bottom: 20px;">快速链接</h3>
      <ul>
        <li><a href="${pageContext.request.contextPath}/home">首页</a></li>
        <li><a href="${pageContext.request.contextPath}/home">商品分类</a></li>
        <li><a href="${pageContext.request.contextPath}/user/Assignment.jsp">积分任务</a></li>
        <li><a href="${pageContext.request.contextPath}/mine">个人中心</a></li>
      </ul>
    </div>
    <div class="footer-section">
      <h3 style="margin-top: 0; color: #333; margin-bottom: 20px;">帮助中心</h3>
      <ul>
        <li><a href="#">积分规则</a></li>
        <li><a href="#">兑换流程</a></li>
        <li><a href="#">常见问题</a></li>
        <li><a href="common/Consulter.jsp">联系客服</a></li>
      </ul>
    </div>
  </div>
</footer>

<!-- 弹窗元素 -->
<div id="productModal" class="modal">
  <div class="modal-content">
    <span class="close">&times;</span>
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

  // 导航栏滚动效果
  window.addEventListener('scroll', function() {
    const header = document.getElementById('header');
    if (window.scrollY > 10) {
      header.classList.replace('py-3', 'py-2');
      header.classList.replace('shadow-sm', 'shadow-md');
    } else {
      header.classList.replace('py-2', 'py-3');
      header.classList.replace('shadow-md', 'shadow-sm');
    }
  });

  //搜索函数
  function searchProducts() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (keyword) {
      // 跳转到新的搜索Servlet
      window.location.href = "${pageContext.request.contextPath}/search?keyword=" + encodeURIComponent(keyword);
    }
  }

  // 在全局作用域定义登录状态变量 - 唯一源
  const isLoggedIn = ${not empty sessionScope.userId};

  // 未登录时跳转到登录页的函数
  function checkLoginAndRedirect(event) {
    if (!isLoggedIn) {
      if (event) event.preventDefault();
      alert("请先登录！");
      window.location.href = "/PointsMall/loginPage";
      return false;
    }
    return true;
  }

  // 分类筛选功能
  document.addEventListener('DOMContentLoaded', function() {
    // 声明所有需要的DOM元素
    const categoryItems = document.querySelectorAll('.category-item');
    const productItems = document.querySelectorAll('.product-item');
    const homeButton = document.getElementById('homeButton');
    const modal = document.getElementById('productModal');
    const modalProductImage = document.getElementById('modalProductImage');
    const modalProductName = document.getElementById('modalProductName');
    const modalProductDescription = document.getElementById('modalProductDescription');
    const modalProductPoints = document.getElementById('modalProductPoints');
    const modalAddress = document.getElementById('modalAddress');
    const modalPhone = document.getElementById('modalPhone');
    const closeBtn = document.querySelector('.close');
    const addToCartBtn = document.querySelector('.add-to-cart');
    const buyNowBtn = document.querySelector('.buy-now');

    // 登录时在右下角显示浮框
    if(isLoggedIn) {
      const username = "${sessionScope.username}";
      const points = "${sessionScope.points}";

      // 创建用户信息浮框
      const floatBox = document.createElement('div');
      floatBox.className = 'user-float-container';
      floatBox.id = 'userFloatBox';

      floatBox.innerHTML = `
        <div class="user-float-icon">
          <i class="fas fa-user-circle"></i>
        </div>
        <div class="user-float-name">${username}</div>
        <div class="user-float-points">积分：<span>${points}</span></div>
      `;

      document.body.appendChild(floatBox);
    }



    // 绑定五个按钮的登录检查事件
    // 1. 修改加入购物车按钮事件
    addToCartBtn.addEventListener('click', function(e) {
      if (!checkLoginAndRedirect(e)) return;

      // 发送AJAX请求到后端
      fetch('${pageContext.request.contextPath}/addToCart', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          productId: currentProductId,
          quantity: 1 // 默认数量为1
        })
      })
              .then(response => response.json())
              .then(data => {
                if(data.success) {
                  alert(`已将${modalProductName.textContent}添加到购物车！`);
                  modal.style.display = 'none';
                } else {
                  alert('添加失败: ' + data.message);
                }
              })
              .catch(error => {
                console.error('Error:', error);
                alert('网络错误，请重试');
              });
    });

    // 2. 立即购买按钮
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

    // 3. 购物车按钮（浮动栏）
    const cartLink = document.querySelector('.float-bar a[href="/PointsMall/cart"]');
    cartLink.addEventListener('click', function(e) {
      if (!checkLoginAndRedirect(e)) return;
    });

    // 4. 我的按钮（浮动栏）
    const mineLink = document.querySelector('.float-bar a[href="/PointsMall/mine"]');
    mineLink.addEventListener('click', function(e) {
      if (!checkLoginAndRedirect(e)) return;
    });

    // 5. 每日任务按钮（浮动栏）
    const assignmentLink = document.querySelector('.float-bar a[href="/PointsMall/user/Assignment.jsp"]');
    assignmentLink.addEventListener('click', function(e) {
      if (!checkLoginAndRedirect(e)) return;
    });

    // 6. 客服按钮（右侧浮动栏）
    const customerServiceLink = document.querySelector('.float-bar a[href="/PointsMall/common/Consulter.jsp"]');
    customerServiceLink.addEventListener('click', function(e) {
      if (!checkLoginAndRedirect(e)) return;
    });


    // 在商品点击事件中存储当前商品ID
    let currentProductId = 0; // 新增全局变量


    // 商品点击事件：直接从商品项的dataset获取数据
    productItems.forEach(item => {
      item.addEventListener('click', () => {
        currentProductId = item.dataset.id; // 存储商品ID
        modalProductImage.src = item.querySelector('img').src;
        modalProductName.textContent = item.querySelector('p:first-of-type').textContent;

        // 从dataset获取商品数据
        const description = item.dataset.description;
        const points = item.dataset.points;

        modalProductDescription.textContent = description || "暂无描述";
        modalProductPoints.textContent = "积分："+points;
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


    // 分类筛选功能
    categoryItems.forEach(item => {
      item.addEventListener('click', function(e) {
        e.preventDefault();

        // 更新分类激活状态
        categoryItems.forEach(cat => cat.classList.remove('active'));
        this.classList.add('active');

        // 筛选商品
        const selectedCategory = this.getAttribute('data-category');
        productItems.forEach(product => {
          const productCategory = product.getAttribute('data-category');
          product.classList.toggle('hidden', selectedCategory !== 'all' && productCategory !== selectedCategory);
        });
      });
    });

    // 回顶部按钮功能
    window.onscroll = function() {
      const backToTop = document.querySelector('.back-to-top');
      if (document.body.scrollTop > 500 || document.documentElement.scrollTop > 500) {
        backToTop.style.display = 'block';
      } else {
        backToTop.style.display = 'none';
      }
    };

    document.querySelector('.back-to-top').onclick = function() {
      document.body.scrollTop = 0;
      document.documentElement.scrollTop = 0;
    };

    // 回到首页按钮功能
    homeButton.addEventListener('click', function(e) {
      e.preventDefault();

      // 重置分类和商品显示
      categoryItems.forEach(cat => cat.classList.remove('active'));
      document.querySelector('[data-category="all"]').classList.add('active');
      productItems.forEach(product => product.classList.remove('hidden'));

      // 滚动到顶部
      document.body.scrollTop = 0;
      document.documentElement.scrollTop = 0;
    });
  });
</script>
</body>
</html>