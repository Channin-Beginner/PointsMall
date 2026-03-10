<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>积分商城-找回密码</title>
  <!-- 添加favicon引用 -->
  <%@ include file="header.jsp" %>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@5.15.3/css/all.min.css">
  <link rel="stylesheet" href="/PointsMall/static/css/ForgotPasswordStyle.css">
</head>
<body>
<div class="forgot-container">
  <h2>找回密码</h2>
  <div class="recovery-tabs">
    <button id="phoneTab" class="active">手机找回</button>
    <button id="emailTab">邮箱找回</button>
  </div>
  <div id="phoneSection" class="recovery-section active">
    <form id="phoneForm">
      <div class="form-group">
        <input type="tel" id="phone" name="phone" required placeholder="请输入注册手机号">
      </div>
      <div class="form-group verification-code">
        <input type="text" id="phoneCode" name="phoneCode" required placeholder="验证码">
        <button type="button" id="sendPhoneCode">获取验证码</button>
      </div>
    </form>
  </div>
  <div id="emailSection" class="recovery-section">
    <form id="emailForm">
      <div class="form-group">
        <input type="email" id="email" name="email" required placeholder="请输入注册邮箱">
      </div>
      <div class="form-group verification-code">
        <input type="text" id="emailCode" name="emailCode" required placeholder="验证码">
        <button type="button" id="sendEmailCode">获取验证码</button>
      </div>
    </form>
  </div>
  <div class="form-group">
    <input type="password" id="newPassword" name="newPassword" required placeholder="请设置新密码">
    <span class="toggle-password" onclick="togglePasswordVisibility('newPassword', this)">
            <i class="fas fa-eye"></i>
        </span>
  </div>
  <div class="form-group">
    <input type="password" id="confirmPassword" name="confirmPassword" required placeholder="请确认新密码">
    <span class="toggle-password" onclick="togglePasswordVisibility('confirmPassword', this)">
            <i class="fas fa-eye"></i>
        </span>
  </div>
  <div class="form-group">
    <button type="button" id="resetBtn">重置密码</button>
  </div>
  <div class="links">
    <a href="/PointsMall/loginPage">返回登录</a>
  </div>
</div>

<script>
  const contextPath = '${pageContext.request.contextPath}';

  function togglePasswordVisibility(inputId, element) {
    const passwordInput = document.getElementById(inputId);
    const icon = element.querySelector('i');
    passwordInput.type = passwordInput.type === 'password' ? 'text' : 'password';
    icon.className = passwordInput.type === 'password' ? 'fas fa-eye' : 'fas fa-eye-slash';
  }

  function toggleRecoverySection(type) {
    document.getElementById('phoneTab').classList.toggle('active', type === 'phone');
    document.getElementById('emailTab').classList.toggle('active', type === 'email');
    document.getElementById('phoneSection').classList.toggle('active', type === 'phone');
    document.getElementById('emailSection').classList.toggle('active', type === 'email');
  }

  document.addEventListener('DOMContentLoaded', function() {
    // 初始化密码框为password类型（确保浏览器默认图标不显示）
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    passwordInputs.forEach(input => {
      input.type = 'password';
    });

    // 其他事件监听...
    document.getElementById('phoneTab').addEventListener('click', () => toggleRecoverySection('phone'));
    document.getElementById('emailTab').addEventListener('click', () => toggleRecoverySection('email'));
    document.getElementById('sendPhoneCode').addEventListener('click', () => sendCode('phone'));
    document.getElementById('sendEmailCode').addEventListener('click', () => sendCode('email'));
    document.getElementById('resetBtn').addEventListener('click', resetPassword);
  });

  function sendCode(type) {
    const input = document.getElementById(type === 'phone' ? 'phone' : 'email');
    const codeBtn = document.getElementById(type === 'phone' ? 'sendPhoneCode' : 'sendEmailCode');
    const value = input.value;

    if (!value) {
      alert('请输入' + (type === 'phone' ? '手机号' : '邮箱'));
      return;
    }

    const regex = type === 'phone' ? /^1[3-9]\d{9}$/ : /^[\w.-]+@[a-zA-Z0-9-]+\.[a-zA-Z]+$/;
    if (!regex.test(value)) {
      alert('请输入有效的' + (type === 'phone' ? '手机号' : '邮箱'));
      return;
    }

    const identifier = encodeURIComponent(value).replace(/\+/g, '%2B');
    const url = `${contextPath}/api/password?action=sendCode`;

    fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: `identifier=${identifier}&type=${type}`
    })
            .then(response => response.json())
            .then(data => {
              alert(data.message);
              if (data.success) startCountdown(codeBtn);
            })
            .catch(error => {
              console.error('验证码发送失败:', error);
              alert('验证码发送失败，请稍后重试');
            });
  }

  function resetPassword() {
    const activeTab = document.querySelector('.recovery-tabs button.active').id;
    const isPhone = activeTab === 'phoneTab';
    const identifier = document.getElementById(isPhone ? 'phone' : 'email').value;
    const code = document.getElementById(isPhone ? 'phoneCode' : 'emailCode').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
      alert('两次输入的密码不一致');
      return;
    }

    if (newPassword.length < 6) {
      alert('密码长度至少为6位');
      return;
    }

    const params = new URLSearchParams({
      identifier,
      type: isPhone ? 'phone' : 'email',
      code,
      newPassword
    });

    fetch(`${contextPath}/api/password?action=reset`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    })
            .then(response => response.json())
            .then(data => {
              alert(data.message);
              if (data.success) window.location.href = '/PointsMall/loginPage';
            })
            .catch(error => {
              console.error('密码重置失败:', error);
              alert('密码重置请求失败，请稍后重试');
            });
  }

  function startCountdown(button) {
    button.disabled = true;
    let countdown = 60;
    const timer = setInterval(() => {
      countdown--;
      button.textContent = `重新发送(${countdown}s)`;
      if (countdown <= 0) {
        clearInterval(timer);
        button.disabled = false;
        button.textContent = '获取验证码';
      }
    }, 1000);
  }
</script>
</body>
</html>