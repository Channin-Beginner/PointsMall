<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>积分商城登录</title>
  <!-- 添加favicon引用 -->
  <%@ include file="header.jsp" %>
  <link rel="stylesheet" href="/PointsMall/static/css/logsStyle.css">
  <!-- 使用国内的Font Awesome镜像链接 -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@5.15.3/css/all.min.css">
</head>
<body>
<div class="login-container">
  <h2>积分商城登录</h2>
  <form id="loginForm">
    <div class="form-group">
      <input type="text" id="username" name="username" required placeholder="账号名/邮箱/手机号">
    </div>
    <div class="form-group">
      <input type="password" id="password" name="password" required placeholder="请输入登录密码">
      <span class="toggle-password" onclick="togglePasswordVisibility()">
                <i class="fas fa-eye"></i>
            </span>
    </div>
    <div class="links">
      <a href="/PointsMall/register">注册账号</a>
      <a href="/PointsMall/api/password">忘记密码</a>
    </div>
    <br>
    <div class="form-group">
      <button type="button" onclick="login()">登录</button>
    </div>
  </form>
</div>

<script>
  function togglePasswordVisibility() {
    var passwordInput = document.getElementById('password');
    var toggleSpan = document.querySelector('.toggle-password i');

    if (passwordInput.type === 'password') {
      passwordInput.type = 'text';
      toggleSpan.className = 'fas fa-eye-slash'; // 更改图标为隐藏图标
    } else {
      passwordInput.type = 'password';
      toggleSpan.className = 'fas fa-eye'; // 更改图标为显示图标
    }
  }

  function login() {
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    if (username === '' || password === '') {
      alert('请输入账号密码');
      return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/PointsMall/loginPage', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    xhr.onreadystatechange = function() {
      if (xhr.readyState === 4 && xhr.status === 200) {
        var response = JSON.parse(xhr.responseText);
        if (response.success) {
          alert('登录成功！');

          // 根据角色决定跳转路径
          if (response.role === 1) {
            window.location.href = '/PointsMall/admin/'; // 管理员跳转管理后台
          } else {
            window.location.href = '/PointsMall/home'; // 普通用户跳转首页
          }
        } else {
          alert('登录失败：' + response.message);
        }
      }
    };

    xhr.send('username=' + encodeURIComponent(username) +
            '&password=' + encodeURIComponent(password));
  }
</script>
</body>
</html>