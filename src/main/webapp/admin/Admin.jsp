<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>积分商城管理后台</title>
  <!-- 添加favicon引用 -->
  <%@ include file="../../common/header.jsp" %>
  <link rel="stylesheet" href="/PointsMall/static/css/AdminStyle.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <style>
    .content-area {
      display: none;
      padding: 20px;
      min-height: calc(100vh - 120px);
    }
    .content-area.active {
      display: block;
    }

    .modal.active {
      display: block !important;
    }
  </style>
</head>
<body>
<div class="admin-container">
  <div class="sidebar">
    <div class="logo">
      <h2>积分商城管理</h2>
    </div>
    <ul class="menu">
      <li class="sidebar-item active" data-target="user-manage">
        <i class="fas fa-users"></i> 用户管理
      </li>
      <li class="sidebar-item" data-target="product-manage">
        <i class="fas fa-box"></i> 商品管理
      </li>
      <li class="sidebar-item" data-target="task-manage">
        <i class="fas fa-tasks"></i> 任务管理
      </li>
    </ul>
  </div>

  <div class="content">
    <div class="header">
      <h1>欢迎管理员，<span id="adminName"><%= session.getAttribute("username") %></span></h1>
      <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      </span>
      <div class="search-box">
        <input type="text" placeholder="搜索...">
        <button><i class="fas fa-search"></i></button>
      </div>
      <div class="logout">
        <a href="/PointsMall/loginPage">退出登录</a>
      </div>
    </div>

    <!-- 用户管理页面（纯前端渲染） -->
    <div class="content-area active" id="user-manage">
      <h2>用户管理</h2>
      <div class="add-user-btn-container">
        <button id="openAddUserModal" class="btn btn-primary">添加新用户</button>
      </div>
      <div class="user-table">
        <table>
          <thead>
          <tr>
            <th>用户ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>手机号码</th>
            <th>角色</th>
            <th>积分</th>
            <th>注册日期</th>
            <th>操作</th>
          </tr>
          </thead>
          <!-- 动态生成表格行，移除 JSTL 循环标签 -->
          <tbody id="userTableBody"></tbody>
        </table>
      </div>
    </div>

    <div class="content-area" id="product-manage">
      <h2>商品管理</h2>
      <div class="add-product-btn-container">
        <button id="openAddProductModal" class="btn btn-primary">添加新商品</button>
      </div>
      <div class="product-table">
        <table>
          <thead>
          <tr>
            <th>商品ID</th>
            <th>商品名称</th>
            <th>所需积分</th>
            <th>库存</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody id="productTableBody"></tbody>
        </table>
      </div>
    </div>

    <!-- 任务管理页面（同理，后续可扩展前端渲染） -->
    <div class="content-area" id="task-manage">
      <h2>任务管理</h2>
      <div class="add-task-btn-container">
        <button id="openAddTaskModal" class="btn btn-primary">添加新任务</button>
      </div>
      <div class="task-table">
        <table>
          <thead>
          <tr>
            <th>任务ID</th>
            <th>任务名称</th>
            <th>奖励积分</th>
            <th>状态</th>
            <th>类型</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody id="taskTableBody"></tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<!-- 移除预定义的表单，仅保留空的模态窗口容器 -->
<div id="editUserModal" class="modal">
  <div class="modal-content"></div> <!-- 动态添加表单内容 -->
</div>

<!-- 移除预定义的表单，仅保留空的模态窗口容器 -->
<div id="editProductModal" class="modal">
  <div class="modal-content"></div> <!-- 动态添加表单内容 -->
</div>

<!-- 移除预定义的表单，仅保留空的模态窗口容器 -->
<div id="editTaskModal" class="modal">
  <div class="modal-content"></div> <!-- 动态添加表单内容 -->
</div>


<%--添加用户模态窗口--%>
<div class="modal" id="addUserModal">
  <div class="modal-wrapper">
    <div class="modal-content">
      <h2 class="modal-title">添加用户</h2> <!-- 标题 -->
      <form id="addUserForm">
        <!-- 表单字段 -->
        <div class="form-group">
          <label>用户名</label>
          <input type="text" name="username" required>
        </div>
        <div class="form-group">
          <label>手机号</label>
          <input type="tel" name="phone" required>
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input type="email" name="email">
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" name="password" required>
        </div>
        <div class="form-group">
          <label>积分</label>
          <input type="number" name="points" required>
        </div>
        <div class="form-group">
          <label>角色</label>
          <select name="role">
            <option value="0">普通用户</option>
            <option value="1">管理员</option>
          </select>
        </div>
        <button type="submit" class="btn btn-primary">创建用户</button>
        <button type="button" class="btn btn-secondary" onclick="closeModal('addUserModal')">取消</button> <!-- 取消按钮 -->
      </form>
    </div>
  </div>
</div>


<!-- 添加商品模态窗口 -->
<div class="modal" id="addProductModal">
  <div class="modal-wrapper">
    <div class="modal-content">
      <h2 class="modal-title">添加商品</h2>
      <form id="addProductForm">
        <!-- 表单字段 -->
        <div class="form-group">
          <label>商品名称</label>
          <input type="text" name="productName" required>
        </div>
        <div class="form-group">
          <label>所需积分</label>
          <input type="number" name="pointsPrice" required>
        </div>
        <div class="form-group">
          <label>库存</label>
          <input type="number" name="stock" required>
        </div>
        <div class="form-group">
          <label>描述</label>
          <textarea name="description"></textarea>
        </div>
        <div class="form-group">
          <label>图片URL</label>
          <input type="text" name="imageUrl">
        </div>
        <button type="submit" class="btn btn-primary">保存</button>
        <button type="button" class="btn btn-secondary" onclick="closeModal('addProductModal')">取消</button>
      </form>
    </div>
  </div>
</div>


<!-- 添加任务模态窗口 -->
<div class="modal" id="addTaskModal">
  <div class="modal-wrapper">
    <div class="modal-content">
      <h2 class="modal-title">添加任务</h2>
      <form id="addTaskForm">
        <!-- 表单字段 -->
        <div class="form-group">
          <label>任务名称</label>
          <input type="text" name="taskName" required>
        </div>
        <div class="form-group">
          <label>奖励积分</label>
          <input type="number" name="pointsAwarded" required>
        </div>
        <div class="form-group">
          <label>任务描述</label>
          <textarea name="taskDescription"></textarea>
        </div>
        <div class="form-group">
          <label>任务状态</label>
          <select name="taskStatus">
            <option value="active">活动中</option>
            <option value="upcoming">即将开始</option>
          </select>
        </div>
        <div class="form-group">
          <label>任务类型</label>
          <select name="taskType">
            <option value="daily">日常任务</option>
            <option value="special">特殊任务</option>
          </select>
        </div>
        <button type="submit" class="btn btn-primary">保存</button>
        <button type="button" class="btn btn-secondary" onclick="closeModal('addTaskModal')">取消</button>
      </form>
    </div>
  </div>
</div>


<script>
  // 侧边栏切换逻辑不变
  document.addEventListener('DOMContentLoaded', () => {
    const sidebarItems = document.querySelectorAll('.sidebar-item');
    const contentAreas = document.querySelectorAll('.content-area');
    sidebarItems[0].classList.add('active');
    contentAreas[0].classList.add('active');
    sidebarItems.forEach(item => {
      item.addEventListener('click', function() {
        sidebarItems.forEach(li => li.classList.remove('active'));
        this.classList.add('active');
        contentAreas.forEach(area => area.classList.remove('active'));
        document.getElementById(this.getAttribute('data-target')).classList.add('active');
      });
    });

    // 初始化时只调用一次(用户管理页面数据渲染）
    if (document.getElementById('user-manage')) {
      loadAllUsers();
    }
    // 初始化时只调用一次(商品管理页面数据渲染）
    if (document.getElementById('product-manage')) {
      loadAllProducts();
    }
    // 初始化时只调用一次(任务管理页面数据渲染）
    if (document.getElementById('task-manage')) {
      loadAllTasks();
    }


  });

  // ======================
  // 用户管理：纯前端渲染
  // ======================
  function loadAllUsers() {
    const tbody = document.querySelector('#user-manage table tbody');
    if (!tbody) return; // 确保找到 tbody

    tbody.textContent = ''; // 更彻底的清空（清除所有子节点）
    fetch('/PointsMall/userProfile?action=all')
            .then(response => response.json())
            .then(userList => {
              userList.forEach(user => {
                const tr = document.createElement('tr');

                // 创建单元格并赋值
                const cells = [
                  { content: user.userId },
                  { content: user.username },
                  { content: user.email || '未填写' },
                  { content: user.phone || '未填写' },
                  { content: user.role == 1 ? '管理员' : '普通用户' },
                  { content: user.totalPoints },
                  { content: formatDate(user.createTime) },
                  {
                    type: 'buttons',
                    userId: user.userId
                  }
                ];
                cells.forEach(cell => {
                  // console.log('用户 ID:', cell.userId);
                  const td = document.createElement('td');
                  if (cell.type == 'buttons') {
                    const editBtn = document.createElement('button');
                    editBtn.className = 'edit-btn';
                    editBtn.textContent = '编辑';
                    editBtn.onclick = () => editUser(cell.userId);

                    const deleteBtn = document.createElement('button');
                    deleteBtn.className = 'delete-btn';
                    deleteBtn.textContent = '删除';

                    // 调试：在点击时打印 userId
                    deleteBtn.onclick = () => {
                      console.log('删除用户 ID:', cell.userId); // 确认是否为目标用户 ID
                      deleteUser(cell.userId);
                    };

                    td.append(editBtn, deleteBtn);
                  } else {
                    td.textContent = cell.content;
                  }
                  tr.appendChild(td);
                });

                tbody.appendChild(tr);
              });
            });
  }

  // 日期格式化函数（建议放在全局作用域）
  function formatDate(timestamp) {
    if (typeof timestamp == 'string') {
      return timestamp; // 若后端已返回字符串格式日期，直接返回
    }
    if (!timestamp) return '--';
    const date = new Date(timestamp);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  }

  // 打开添加用户模态窗口（模仿编辑按钮的事件绑定和初始化逻辑）
  document.getElementById('openAddUserModal').addEventListener('click', () => {
    const addUserModal = document.getElementById('addUserModal');
    addUserModal.classList.add('active'); // 显示模态窗口
    document.getElementById('addUserForm').reset(); // 清空表单（模仿编辑表单的初始化）
  });

  // 提交添加用户表单（模仿编辑按钮的提交逻辑）
  document.getElementById('addUserForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const form = document.getElementById('addUserForm');
    const formData = new FormData(form);
    const params = new URLSearchParams(formData); // 转换为 URL 编码参数
    console.log('提交参数：', params.toString());
    fetch('/PointsMall/userProfile?action=add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded', // 明确设置请求头
      },
      body: params.toString() // 发送 URL 编码的字符串
    })
            .then(response => response.json())
            .then(result => {
              if (result.success) {
                loadAllUsers();
                closeModal('addUserModal');
              }
              alert(result.message);
            })
            .catch(error => {
              console.error('添加用户失败:', error);
              alert('添加用户失败，请重试');
            });
  });

  // 编辑用户
  function editUser(userId) {
    const editModal = document.getElementById('editUserModal');
    if (!editModal) {
      console.error('编辑模态窗口未找到');
      return;
    }

    // 使用 URLSearchParams 构建查询参数
    const params = new URLSearchParams();
    params.append('action', 'get');
    params.append('userId', userId);

    fetch('/PointsMall/userProfile?' + params.toString())
            .then(function(response) {
              if (!response.ok) {
                throw new Error('HTTP错误: ' + response.status);
              }
              return response.json();
            })
            .then(function(user) {
              // 清空模态窗口并动态创建表单元素
              editModal.innerHTML = '';

              // 创建表单元素
              const form = document.createElement('form');
              form.id = 'edit-user-form';

              // 创建隐藏字段
              const userIdField = document.createElement('input');
              userIdField.type = 'hidden';
              userIdField.name = 'userId';
              userIdField.value = user.userId;
              form.appendChild(userIdField);

              // 创建用户名输入
              form.appendChild(createFormGroup('用户名', 'text', 'username', user.username, true));

              // 创建手机号输入
              form.appendChild(createFormGroup('手机号', 'text', 'phone', user.phone, true));

              // 创建邮箱输入
              const emailInput = createFormGroup('邮箱', 'email', 'email', user.email || '');
              form.appendChild(emailInput);

              // 创建积分输入
              const pointsInput = createFormGroup('积分', 'number', 'totalPoints', user.totalPoints, true);
              form.appendChild(pointsInput);

              // 创建角色选择
              const roleGroup = document.createElement('div');
              roleGroup.className = 'form-group';
              roleGroup.innerHTML = `
        <label>角色</label>
        <select name="role">
          <option value="0">普通用户</option>
          <option value="1">管理员</option>
        </select>
      `;
              form.appendChild(roleGroup);

              // 设置角色选择
              const roleSelect = roleGroup.querySelector('select');
              roleSelect.value = user.role;

              // 创建密码输入
              const passwordInput = document.createElement('div');
              passwordInput.className = 'form-group';
              passwordInput.innerHTML = `
        <label>密码</label>
        <input type="password" name="password" placeholder="不修改则留空">
      `;
              form.appendChild(passwordInput);

              // 创建操作按钮
              const actionsDiv = document.createElement('div');
              actionsDiv.className = 'form-actions';
              actionsDiv.innerHTML = `
        <button type="button" onclick="closeEditModal()">取消</button>
        <span>&nbsp;&nbsp;&nbsp;</span>
        <button type="button" onclick="submitEditUser()">保存</button>
      `;
              form.appendChild(actionsDiv);

              // 将表单添加到模态窗口
              const modalContent = document.createElement('div');
              modalContent.className = 'modal-content';
              modalContent.innerHTML = '<h3>编辑用户</h3>';
              modalContent.appendChild(form);
              editModal.appendChild(modalContent);

              // 显示模态窗口
              editModal.style.display = 'block';
            })
            .catch(function(error) {
              console.error('编辑用户失败:', error);
              alert('操作失败: ' + error.message);
              closeEditModal();
            });
  }

  // 辅助函数：创建表单组
  function createFormGroup(labelText, inputType, inputName, inputValue, isRequired = false) {
    const group = document.createElement('div');
    group.className = 'form-group';

    const label = document.createElement('label');
    label.textContent = labelText;
    group.appendChild(label);

    const input = document.createElement('input');
    input.type = inputType;
    input.name = inputName;
    input.value = inputValue || '';
    if (isRequired) {
      input.required = true;
    }
    group.appendChild(input);

    return group;
  }

  // 关闭模态窗口函数
  function closeEditModal() {
    document.getElementById('editUserModal').style.display = 'none';
  }

  // 提交编辑用户表单
  function submitEditUser() {
    const form = document.getElementById('edit-user-form');
    const formData = new FormData(form);

    // 将 FormData 转换为 URL 编码格式
    const params = new URLSearchParams(formData);

    fetch('/PointsMall/userProfile?action=update', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded', // 关键修改
      },
      body: params.toString() // 发送 URL 编码的字符串
    })
            .then(response => response.json())
            .then(result => {
              alert(result.message);
              if (result.success) {
                closeEditModal(); // 关闭模态窗口
                loadAllUsers(); // 刷新列表
              }
            })
            .catch(error => {
              console.error('更新用户失败:', error);
              alert("更新用户失败，请重试");
            });
  }

  // 删除用户函数
  function deleteUser(targetUserId) {
    if (confirm('确定删除此用户？')) {
      const formData = new URLSearchParams();
      formData.append('userId', targetUserId); // 自动处理编码
      console.log(formData.toString());

      fetch('/PointsMall/userProfile?action=delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString() // 生成 "userId=123" 格式
      })
              .then(response => response.json())
              .then(result => {
                alert(result.message);
                if (result.success) {
                  loadAllUsers(); // 刷新列表
                }
              })
              .catch(error => {
                console.error('删除用户失败:', error);
                alert("删除操作失败，请重试");
              });
    }
  }





  // ======================
  // 商品管理：暂留示例（后续可扩展前端渲染）
  // ======================
  // ======================
  // 商品管理：纯前端渲染（完全模仿 loadAllUsers 格式）
  // ======================
  function loadAllProducts() {
    const tbody = document.querySelector('#product-manage table tbody');
    if (!tbody) return; // 确保找到 tbody

    tbody.textContent = ''; // 更彻底的清空（清除所有子节点）
    fetch('/PointsMall/admin/product?action=all')
            .then(response => response.json())
            .then(productList => {
              productList.forEach(product => {
                const tr = document.createElement('tr');

                // 创建单元格并赋值（与用户管理列对应，按需调整字段）
                const cells = [
                  { content: product.productId },
                  { content: product.productName },
                  { content: product.pointsPrice },
                  { content: product.stock },
                  {
                    type: 'buttons',
                    productId: product.productId // 注意这里改为 productId
                  }
                ];

                cells.forEach(cell => {
                  const td = document.createElement('td');
                  if (cell.type === 'buttons') {
                    const editBtn = document.createElement('button');
                    editBtn.className = 'edit-btn';
                    editBtn.textContent = '编辑';
                    editBtn.onclick = () => editProduct(cell.productId); // 编辑函数参数为 productId

                    const deleteBtn = document.createElement('button');
                    deleteBtn.className = 'delete-btn';
                    deleteBtn.textContent = '删除';
                    deleteBtn.onclick = () => deleteProduct(cell.productId); // 删除函数参数为 productId

                    td.append(editBtn, deleteBtn);
                  } else {
                    td.textContent = cell.content;
                  }
                  tr.appendChild(td);
                });

                tbody.appendChild(tr);
              });
            });
  }

  // 打开添加商品模态窗口（与编辑按钮结构一致）
  document.getElementById('openAddProductModal').addEventListener('click', () => {
    const addProductModal = document.getElementById('addProductModal');
    addProductModal.classList.add('active'); // 显示模态窗口
    document.getElementById('addProductForm').reset(); // 清空表单
  });

  // 提交添加商品表单（与编辑按钮提交逻辑一致）
  document.getElementById('addProductForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const form = document.getElementById('addProductForm');
    const formData = new FormData(form);
    const params = new URLSearchParams(formData); // 转换为 URL 编码参数
    console.log(params.toString());

    fetch('/PointsMall/admin/product?action=add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded', // 明确设置请求头
      },
      body: params.toString() // 发送 URL 编码的字符串
    })
            .then(response => response.json())
            .then(result => {
              if (result.success) {
                loadAllProducts(); // 刷新商品列表
                document.getElementById('addProductModal').classList.remove('active'); // 关闭模态窗口
              }
              alert(result.message);
            })
            .catch(error => {
              console.error('添加商品失败:', error);
              alert('添加商品失败，请重试');
            });
  });

  // 单独处理商品删除按钮事件（避免与其他模块冲突）
  // 绑定删除按钮点击事件（纯JS，无EL）
  document.addEventListener('click', (e) => {
    if (e.target.classList.contains('delete-btn') && e.target.closest('[data-target="product-manage"]')) {
      const productId = e.target.dataset.id; // 从 data-id 属性获取 ID
      deleteProduct(productId); // 直接调用删除函数
    }
  });

  // 商品删除函数（纯JS，无EL）
  function deleteProduct(targetProductId) {
    if (confirm('确定删除此商品？')) {
      const formData = new URLSearchParams();
      formData.append('productId', targetProductId); // 完全模仿 user 的 userId 参数名
      fetch('/PointsMall/admin/product?action=delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
      })
              .then(res => res.json())
              .then(result => {
                alert(result.message);
                if (result.success) loadAllProducts(); // 刷新列表，模仿 user 的 loadAllUsers
              })
              .catch(error => {
                console.error('删除商品失败:', error);
                alert("删除商品失败，请重试");
              });
    }
  }

  async function getProductById(productId) {
    try {
      const response = await fetch(`/PointsMall/admin/product/${productId}`);
      if (!response.ok) throw new Error('获取商品详情失败');
      return await response.json();
    } catch (error) {
      console.error('获取商品详情失败:', error);
      return null;
    }
  }


  // 编辑商品
  function editProduct(productId) {
    const editModal = document.getElementById('editProductModal');
    if (!editModal) {
      console.error('编辑商品模态窗口未找到');
      return;
    }
    // 使用 URLSearchParams 构建查询参数
    const params = new URLSearchParams();
    params.append('action', 'get');
    params.append('productId', productId);
    console.log('发送的查询参数：', params.toString());

    fetch('/PointsMall/admin/product?' + params.toString())
            .then(function(response) {
              if (!response.ok) {
                throw new Error('HTTP错误: ' + response.status);
              }
              return response.json();
            })
            .then(function(product) {
              // 清空模态窗口并动态创建表单元素
              editModal.innerHTML = '';

              // 创建表单元素
              const form = document.createElement('form');
              form.id = 'edit-product-form';

              // 创建隐藏字段
              const productIdField = document.createElement('input');
              productIdField.type = 'hidden';
              productIdField.name = 'productId';
              productIdField.value = product.productId;
              form.appendChild(productIdField);
              console.log(product.stock);
              // 使用辅助函数创建表单组
              form.appendChild(createFormGroup('商品名称', 'text', 'productName', product.productName, true));
              form.appendChild(createFormGroup('所需积分', 'number', 'pointsPrice', product.pointsPrice, true));
              form.appendChild(createFormGroup('库存', 'number', 'stock', product.stock, true));

              // 描述字段（纯 JS 设置值）
              const descriptionGroup = document.createElement('div');
              descriptionGroup.className = 'form-group';
              descriptionGroup.innerHTML = '<label>描述</label><textarea name="description"></textarea>';
              form.appendChild(descriptionGroup);
              descriptionGroup.querySelector('textarea').value = product.description || '';

              // 图片URL字段（纯 JS 设置值）
              const imageUrlGroup = document.createElement('div');
              imageUrlGroup.className = 'form-group';
              imageUrlGroup.innerHTML = '<label>图片URL</label><input type="text" name="imageUrl">';
              form.appendChild(imageUrlGroup);
              imageUrlGroup.querySelector('input').value = product.imageUrl || '';

              // 创建操作按钮
              const actionsDiv = document.createElement('div');
              actionsDiv.className = 'form-actions';
              actionsDiv.innerHTML = `
        <button type="button" onclick="closeEditProductModal()">取消</button>
        <span>&nbsp;&nbsp;&nbsp;</span>
        <button type="button" onclick="submitEditProduct()">保存</button>
      `;
              form.appendChild(actionsDiv);

              // 将表单添加到模态窗口
              const modalContent = document.createElement('div');
              modalContent.className = 'modal-content';
              modalContent.innerHTML = '<h3>编辑商品</h3>';
              modalContent.appendChild(form);
              editModal.appendChild(modalContent);

              // 显示模态窗口
              editModal.style.display = 'block';
            })
            .catch(function(error) {
              console.error('编辑商品失败:', error);
              alert('操作失败: ' + error.message);
              closeEditProductModal();
            });
  }

  // 辅助函数：创建表单组（与用户管理完全一致）
  function createFormGroup(labelText, inputType, inputName, inputValue, isRequired = false) {
    const group = document.createElement('div');
    group.className = 'form-group';

    const label = document.createElement('label');
    label.textContent = labelText;
    group.appendChild(label);

    const input = document.createElement('input');
    input.type = inputType;
    input.name = inputName;
    input.value = inputValue || '';
    if (isRequired) {
      input.required = true;
    }
    group.appendChild(input);

    return group;
  }

  // 关闭模态窗口函数（与用户管理一致）
  function closeEditProductModal() {
    document.getElementById('editProductModal').style.display = 'none';
  }

  // 提交编辑商品表单（与用户管理一致）
  function submitEditProduct() {
    const form = document.getElementById('edit-product-form');
    const formData = new FormData(form);

    // 将 FormData 转换为 URL 编码格式（与用户管理一致）
    const params = new URLSearchParams(formData);

    fetch('/PointsMall/admin/product?action=update', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: params.toString()
    })
            .then(response => response.json())
            .then(result => {
              alert(result.message);
              if (result.success) {
                closeEditProductModal(); // 关闭模态窗口
                loadAllProducts(); // 刷新列表
              }
            })
            .catch(error => {
              console.error('更新商品失败:', error);
              alert("更新商品失败，请重试");
            });
  }







  // ======================
  // 任务管理：暂留示例（后续可扩展前端渲染）
  // ======================
  // ======================
  // 任务管理：纯前端渲染（完全模仿 loadAllUsers 格式）
  // ======================
  function loadAllTasks() {
    const tbody = document.querySelector('#task-manage table tbody');
    if (!tbody) return; // 确保找到 tbody

    tbody.textContent = ''; // 更彻底的清空（清除所有子节点）
    fetch('/PointsMall/admin/task?action=all') // 假设后端接口为 /admin/task?action=all
            .then(response => response.json())
            .then(taskList => {
              taskList.forEach(task => {
                const tr = document.createElement('tr');

                // 创建单元格并赋值（与用户管理列对应，按需调整字段）
                const cells = [
                  { content: task.taskId },
                  { content: task.taskName },
                  { content: task.pointsAwarded },
                  { content: task.taskStatus },
                  { content: task.taskType},
                  {
                    type: 'buttons',
                    taskId: task.taskId // 注意这里改为 taskId
                  }
                ];

                cells.forEach(cell => {
                  const td = document.createElement('td');
                  if (cell.type === 'buttons') {
                    const editBtn = document.createElement('button');
                    editBtn.className = 'edit-btn';
                    editBtn.textContent = '编辑';
                    editBtn.onclick = () => editTask(cell.taskId); // 编辑函数参数为 taskId

                    const deleteBtn = document.createElement('button');
                    deleteBtn.className = 'delete-btn';
                    deleteBtn.textContent = '删除';
                    deleteBtn.onclick = () => deleteTask(cell.taskId); // 删除函数参数为 taskId

                    td.append(editBtn, deleteBtn);
                  } else {
                    td.textContent = cell.content;
                  }
                  tr.appendChild(td);
                });

                tbody.appendChild(tr);
              });
            });
  }


  // 打开发布任务模态窗口（与编辑按钮结构一致）
  document.getElementById('openAddTaskModal').addEventListener('click', () => {
    const addTaskModal = document.getElementById('addTaskModal');
    addTaskModal.classList.add('active'); // 显示模态窗口
    document.getElementById('addTaskForm').reset(); // 清空表单
  });

  // 提交发布任务表单（与编辑按钮提交逻辑一致）
  document.getElementById('addTaskForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const form = document.getElementById('addTaskForm');
    const formData = new FormData(form);
    const params = new URLSearchParams(formData); // 转换为 URL 编码参数
    fetch('/PointsMall/admin/task?action=add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded', // 明确设置请求头
      },
      body: params.toString() // 发送 URL 编码的字符串
    })

            .then(response => response.json())
            .then(result => {
              if (result.success) {
                loadAllTasks(); // 刷新任务列表
                document.getElementById('addTaskModal').classList.remove('active'); // 关闭模态窗口
              }
              alert(result.message);
            })
            .catch(error => {
              console.error('添加任务失败:', error);
              alert('添加任务失败，请重试');
            });
  });


  // 编辑任务模态窗口相关逻辑
  function editTask(taskId) {
    const editModal = document.getElementById('editTaskModal');
    if (!editModal) {
      console.error('编辑任务模态窗口未找到');
      return;
    }

    // 使用 URLSearchParams 构建查询参数
    const params = new URLSearchParams();
    params.append('action', 'get');
    params.append('taskId', taskId);
    console.log('发送的查询参数：', params.toString());

    fetch('/PointsMall/admin/task?' + params.toString())
            .then(function(response) {
              if (!response.ok) {
                throw new Error('HTTP错误: ' + response.status);
              }
              return response.json();
            })
            .then(function(task) {
              // 清空模态窗口并动态创建表单元素
              editModal.innerHTML = '';

              // 创建表单元素
              const form = document.createElement('form');
              form.id = 'edit-task-form';

              // 创建隐藏字段
              const taskIdField = document.createElement('input');
              taskIdField.type = 'hidden';
              taskIdField.name = 'taskId';
              taskIdField.value = task.taskId;
              form.appendChild(taskIdField);

              // 使用辅助函数创建表单组
              form.appendChild(createFormGroup('任务名称', 'text', 'taskName', task.taskName, true));
              form.appendChild(createFormGroup('奖励积分', 'number', 'pointsAwarded', task.pointsAwarded, true));

              // 任务描述字段
              const descriptionGroup = document.createElement('div');
              descriptionGroup.className = 'form-group';
              descriptionGroup.innerHTML = '<label>任务描述</label><textarea name="taskDescription"></textarea>';
              form.appendChild(descriptionGroup);
              descriptionGroup.querySelector('textarea').value = task.taskDescription || ''; // 纯 JS 设置值

              // 任务状态选择
              const statusGroup = document.createElement('div');
              statusGroup.className = 'form-group';
              statusGroup.innerHTML = `
        <label>任务状态</label>
        <select name="taskStatus">
          <option value="active">活动中</option>
          <option value="upcoming">即将开始</option>
          <option value="inactive">已失效</option>
        </select>
      `;
              form.appendChild(statusGroup);
              const statusSelect = statusGroup.querySelector('select');
              statusSelect.value = task.taskStatus || 'active';

              // **新增 taskType 输入框（纯 JS 设置值，无 EL 表达式）**
              const taskTypeGroup = document.createElement('div');
              taskTypeGroup.className = 'form-group';
              taskTypeGroup.innerHTML = `
        <label>任务类型</label>
        <select name="taskType">
          <option value="daily">日常任务</option>
          <option value="special">特殊任务</option>
        </select>
      `;
              form.appendChild(taskTypeGroup);
              const taskTypeSelect = taskTypeGroup.querySelector('select');
              taskTypeSelect.value = task.taskType || 'daily';

              // 创建操作按钮
              const actionsDiv = document.createElement('div');
              actionsDiv.className = 'form-actions';
              actionsDiv.innerHTML = `
        <button type="button" onclick="closeEditTaskModal()">取消</button>
        <span>&nbsp;&nbsp;&nbsp;</span>
        <button type="button" onclick="submitEditTask()">保存</button>
      `;
              form.appendChild(actionsDiv);

              // 将表单添加到模态窗口
              const modalContent = document.createElement('div');
              modalContent.className = 'modal-content';
              modalContent.innerHTML = '<h3>编辑任务</h3>';
              modalContent.appendChild(form);
              editModal.appendChild(modalContent);

              // 显示模态窗口
              editModal.style.display = 'block';
            })
            .catch(function(error) {
              console.error('编辑任务失败:', error);
              alert('操作失败: ' + error.message);
              closeEditTaskModal();
            });
  }

  // 关闭模态窗口函数（与用户管理一致）
  function closeEditTaskModal() {
    document.getElementById('editTaskModal').style.display = 'none';
  }

  // 提交编辑任务表单（与用户管理一致）
  function submitEditTask() {
    const form = document.getElementById('edit-task-form');
    const formData = new FormData(form);
    // 将 FormData 转换为 URL 编码格式（与用户管理一致）
    const params = new URLSearchParams(formData);
    console.log(params.toString());
    fetch('/PointsMall/admin/task?action=update', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: params.toString()
    })
            .then(response => response.json())
            .then(result => {
              alert(result.message);
              if (result.success) {
                closeEditTaskModal(); // 关闭模态窗口
                loadAllTasks(); // 刷新列表
              }
            })
            .catch(error => {
              console.error('更新任务失败:', error);
              alert("更新任务失败，请重试");
            });
  }

  // 任务删除函数（纯JS，无EL）
  function deleteTask(targetTaskId) {
    if (confirm('确定删除此任务？')) {
      const formData = new URLSearchParams();
      formData.append('taskId', targetTaskId); // 完全模仿 user 的 userId 参数名
      fetch('/PointsMall/admin/task?action=delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
      })
              .then(res => res.json())
              .then(result => {
                alert(result.message);
                if (result.success) loadAllTasks(); // 刷新列表，模仿 user 的 loadAllUsers
              })
              .catch(error => {
                console.error('删除任务失败:', error);
                alert("删除任务失败，请重试");
              });
    }
  }

  // 绑定删除按钮点击事件（纯JS，无EL）
  document.addEventListener('click', (e) => {
    if (e.target.classList.contains('delete-btn') && e.target.closest('[data-target="task-manage"]')) {
      const taskId = e.target.dataset.id; // 从 data-id 属性获取 ID
      deleteTask(taskId); // 直接调用删除函数
    }
  });

  // 获取单个任务详情（用于编辑）
  async function getTaskById(taskId) {
    try {
      const response = await fetch(`/PointsMall/admin/task/${taskId}`);
      if (!response.ok) throw new Error('获取任务详情失败');
      return await response.json();
    } catch (error) {
      console.error('获取任务详情失败:', error);
      return null;
    }
  }


  function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.remove('active');
      // 重置表单
      const form = modal.querySelector('form');
      if (form) form.reset();
    }
  }

  // 专门为三个模态窗口添加的关闭函数
  function closeAddUserModal() {
    closeModal('addUserModal');
  }

  function closeAddProductModal() {
    closeModal('addProductModal');
  }

  function closeAddTaskModal() {
    closeModal('addTaskModal');
  }

</script>
</body>
</html>