<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>购物车 - 积分商城</title>
  <!-- 添加favicon引用 -->
  <%@ include file="../../common/header.jsp" %>
  <link rel="stylesheet" href="/PointsMall/static/css/ShoppingcartStyle.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>

<div class="cart-container">
  <div class="announcement">
    <h2 style="margin: 0; font-size: 24px;">积分购物车</h2>
  </div>
  <table>
    <thead>
    <tr>
      <th>
        <input type="checkbox" id="selectAll" class="select-all-checkbox">
        <span>全选</span>
      </th>
      <th>商品</th>
      <th>积分</th>
      <th>数量</th>
      <th>操作</th>
    </tr>
    </thead>

    <tbody>
      <c:choose>
        <c:when test="${empty cartItems}">
          <tr class="empty-cart-row">
            <td colspan="5">
              <div class="empty-cart">
                <i class="fas fa-shopping-cart fa-3x"></i>
                <p>您的购物车是空的</p>
                <button onclick="window.location.href='/PointsMall/home'">去逛逛</button>
              </div>
            </td>
          </tr>
        </c:when>
        <c:otherwise>
          <c:forEach items="${cartItems}" var="item">
            <tr>
              <td><input type="checkbox" class="itemCheckbox"></td>
              <td>
                <img src="${item.product.imageUrl}" alt="${item.product.productName}" class="product-image">
                <div style="margin-left: 12px; display: inline-block; vertical-align: middle;">
                    ${item.product.productName}
                </div>
              </td>
              <td>
                <div>积分：<span class="points">${item.product.pointsPrice}</span></div>
              </td>
              <td>
                <div class="quantity-container">
                  <span>数量：</span>
                  <input type="number" value="${item.quantity}"
                         class="quantity quantity-input"
                         data-product-id="${item.product.productId}">
                </div>
              </td>
              <td>
                <button onclick="removeItem(this, ${item.product.productId})">删除</button>
              </td>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
      <c:if test="${not empty cartItems}">
        <c:forEach items="${cartItems}" var="item" varStatus="status">
          <script>
            console.log("商品${status.index}:", {
              id: ${item.product.productId},
              name: "${item.product.productName}",
              qty: ${item.quantity}
            });
          </script>
        </c:forEach>
      </c:if>
    </tbody>

    <tfoot>
    <tr class="total-section">
      <td colspan="4">总积分：<span id="totalPoints">0</span></td>
      <td></td>
    </tr>
    </tfoot>
  </table>
  <!-- 按钮容器 -->
  <div class="button-container">
    <button type="button" onclick="window.location.href='/PointsMall/home'" class="add-button">
      <i class="fas fa-plus"></i> 添加商品
    </button>
    <button type="button" onclick="proceedToCheckout()" class="add-button checkout-button">
      <i class="fas fa-check"></i> 结算
    </button>
  </div>
</div>

<div class="home-button" onclick="window.location.href='/PointsMall/home'">
  <i class="fas fa-home"></i>
</div>

<script>


  // 删除商品
  function removeItem(button, productId) {
    const params = new URLSearchParams();
    params.append('productId', productId);
    if(confirm('确定要删除该商品吗？')) {
      fetch('${pageContext.request.contextPath}/cart?action=remove', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
      })
              .then(response => response.json())
              .then(data => {
                if(data.success) {
                  button.closest('tr').remove();
                  updateTotal();
                  // 检查是否变为空购物车
                  if(document.querySelectorAll('tbody tr').length === 1) { // 包含空提示行
                    location.reload(); // 重新加载显示空购物车状态
                  }
                } else {
                  alert('删除失败: ' + data.message);
                }
              });
    }
  }

  // 更新数量
  function updateQuantity(productId, newQuantity) {
    fetch('${pageContext.request.contextPath}/cart', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: `action=update&productId=${productId}&quantity=${newQuantity}`
    })
            .then(response => response.json())
            .then(data => {
              if(!data.success) {
                alert('更新失败: ' + data.message);
              }
            });
  }

  // 计算总积分（只计算选中的商品）
  function updateTotal() {
    const items = document.querySelectorAll('tbody tr:not(.empty-cart-row)');
    let totalPoints = 0;

    items.forEach(item => {
      const checkbox = item.querySelector('.itemCheckbox');
      if(!checkbox || !checkbox.checked) return; // 跳过未选中的商品

      const points = parseInt(item.querySelector('.points').textContent) || 0;
      const quantity = parseInt(item.querySelector('.quantity-input').value) || 1;
      totalPoints += points * quantity;
    });

    document.getElementById('totalPoints').textContent = totalPoints;
  }

  // 页面加载完成后初始化
  window.addEventListener('DOMContentLoaded', () => {

    console.log("用户ID: ", "${sessionScope.userId}");
    console.log("购物车数据: ", `${cartItems}`);

    // 修复点：添加空元素检查
    const selectAll = document.getElementById('selectAll');
    if (selectAll) { // 当购物车非空时执行
      selectAll.addEventListener('change', function() {
        const checkboxes = document.querySelectorAll('.itemCheckbox');
        checkboxes.forEach(checkbox => checkbox.checked = this.checked);
        updateTotal();
      });
    }

    // 检查localStorage是否干扰
    if(localStorage.getItem("orderItems")) {
      console.warn("检测到localStorage中存在orderItems：",
              localStorage.getItem("orderItems"));
    }

    localStorage.removeItem('orderItems');
    console.log("已清除遗留的orderItems数据");

    // 全选功能
    document.getElementById('selectAll').addEventListener('change', function() {
      const checkboxes = document.querySelectorAll('.itemCheckbox');
      checkboxes.forEach(checkbox => checkbox.checked = this.checked);
      updateTotal();
    });

    // 单个商品选中状态变化
    document.querySelectorAll('.itemCheckbox').forEach(checkbox => {
      checkbox.addEventListener('change', updateTotal);
    });

    // 数量变化事件
    const quantityInputs = document.querySelectorAll('.quantity-input');
    quantityInputs.forEach(input => {
      input.addEventListener('change', function() {
        const productId = this.dataset.productId;
        let newQuantity = parseInt(this.value);

        if(isNaN(newQuantity) || newQuantity < 1) {
          this.value = 1;
          newQuantity = 1;
        }

        updateQuantity(productId, newQuantity);
        updateTotal();
      });
    });

    // 初始化总积分
    updateTotal();
  });

  function proceedToCheckout() {
    const checkboxes = document.querySelectorAll('.itemCheckbox:checked');
    if (checkboxes.length === 0) {
      alert("请至少选择一个商品");
      return;
    }

    const selectedItems = Array.from(checkboxes).map(checkbox => {
      const row = checkbox.closest('tr');
      return {
        productId: row.querySelector('.quantity-input').dataset.productId,
        quantity: parseInt(row.querySelector('.quantity-input').value) || 1
      };
    });

    // 直接传 JSON 字符串，设置正确的 Content-Type
    fetch('${pageContext.request.contextPath}/checkout', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', // 关键：明确 JSON 格式
      },
      body: JSON.stringify(selectedItems) // 传 JSON 数组
    })
            .then(response => response.json())
            .then(({ success, message, data }) => {
              if (success) {
                alert(message);
                // location.reload();
                // 跳转到我的订单页面并刷新
                window.location.href = '/PointsMall/mine';
                data.forEach(productId => {
                  document.querySelector(`[data-product-id="${productId}"]`).closest('tr').remove();
                });
                updateTotal();
              } else {
                alert('结算失败：' + message);
              }
            })
            .catch(error => {
              console.error('结算请求失败:', error);
            });
  }

  document.getElementById('selectAll').addEventListener('click', function () {
    const isChecked = this.checked;
    const itemCheckboxes = document.querySelectorAll('.itemCheckbox');
    itemCheckboxes.forEach(checkbox => {
      checkbox.checked = isChecked;
    });
  });

  // 将事件绑定移到DOMContentLoaded中
  window.addEventListener('DOMContentLoaded', () => {
    const quantityInputs = document.querySelectorAll('.quantity-input');
    quantityInputs.forEach(input => {
      input.addEventListener('change', function() {
        const productId = this.dataset.productId;
        const newQuantity = parseInt(this.value);
        if(newQuantity < 1) {
          this.value = 1;
          return;
        }
        updateQuantity(productId, newQuantity);
        updateTotal(); // 更新总积分
      });
    });
    updateTotal(); // 初始化计算
  });

  function addItem() {
    const tbody = document.querySelector('tbody');
    const newRow = document.createElement('tr');

    newRow.innerHTML = `
                <td><input type="checkbox" class="itemCheckbox"></td>
                <td>
                    <img src="https://via.placeholder.com/50" alt="新商品" class="product-image">
                    <div style="margin-left: 12px; display: inline-block; vertical-align: middle;">新商品</div>
                </td>
                <td>
                    <div>积分：<span class="points">200</span></div>
                </td>
                <td>
                    <div class="quantity-container">
                        <span>数量：</span>
                        <input type="number" value="1" class="quantity quantity-input">
                    </div>
                </td>
                <td><button onclick="removeItem(this)">删除</button></td>
            `;

    tbody.appendChild(newRow);

    const quantityInput = newRow.querySelector('.quantity-input');
    quantityInput.addEventListener('input', updateTotal);

    updateTotal();
  }

  function getSelectedItems() {
    const selectedItems = [];
    const checkedRows = document.querySelectorAll('tbody tr input.itemCheckbox:checked');

    checkedRows.forEach(checkbox => {
      const row = checkbox.closest('tr');
      const name = row.querySelector('td:nth-child(2) div').textContent.trim();
      const points = parseInt(row.querySelector('.points').textContent);
      const quantity = Math.max(1, parseInt(row.querySelector('.quantity-input').value) || 1);

      selectedItems.push({
        name: name,
        points: points,
        quantity: quantity
      });
    });

    return selectedItems;
  }

  const quantityInput = document.querySelector('.quantity-input');
  quantityInput.addEventListener('input', function () {
    if (this.value === '' || parseInt(this.value) < 1) {
      this.value = 1;
    }
    updateTotal();
  });
</script>
</body>
</html>