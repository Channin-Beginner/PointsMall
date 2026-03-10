<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>积分商城 - 注册账号</title>
    <!-- 添加favicon引用 -->
    <%@ include file="header.jsp" %>
    <!-- 使用CDN引入Font Awesome图标库 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@5.15.3/css/all.min.css">
    <link rel="stylesheet" href="/PointsMall/static/css/RegisterStyle.css">
</head>
<body>
<div class="register-container">
    <h2>创建新账号</h2>
    <form id="registerForm">
        <div class="form-group">
            <input type="text" id="regUsername" name="username" required placeholder="请输入用户名">
        </div>
        <div class="form-group">
            <input type="tel" id="regPhone" name="phone" required placeholder="请输入手机号">
        </div>
        <div class="form-group verification-code">
            <input type="text" id="verifyCode" name="verifyCode" required placeholder="验证码">
            <button type="button" id="sendCodeBtn">获取验证码</button>
        </div>
        <div class="form-group">
            <input type="password" id="regPassword" name="password" required placeholder="请设置密码">
            <span class="toggle-password" onclick="togglePasswordVisibility('regPassword', this)">
                <i class="fas fa-eye"></i>
            </span>
        </div>
        <div class="form-group">
            <input type="password" id="confirmPassword" name="confirmPassword" required placeholder="请确认密码">
            <span class="toggle-password" onclick="togglePasswordVisibility('confirmPassword', this)">
                <i class="fas fa-eye"></i>
            </span>
        </div>
        <div class="terms">
            <input type="checkbox" id="agreeTerms" required>
            <label for="agreeTerms">我已阅读并同意<a href="#">用户协议</a>和<a href="#">隐私政策</a></label>
        </div>
        <div class="form-group">
            <button type="submit">注册账号</button>
        </div>
    </form>
    <div class="links">
        <a href="/PointsMall/loginPage">已有账号？立即登录</a>
    </div>
</div>

<script>
    function togglePasswordVisibility(inputId, element) {
        var passwordInput = document.getElementById(inputId);
        var icon = element.querySelector('i');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            icon.className = 'fas fa-eye-slash';
        } else {
            passwordInput.type = 'password';
            icon.className = 'fas fa-eye';
        }
    }

    // 验证码倒计时功能
    document.getElementById('sendCodeBtn').addEventListener('click', function() {
        var btn = this;
        var phone = document.getElementById('regPhone').value;

        if (!phone) {
            alert('请先输入手机号');
            return;
        }

        // 验证手机号格式（简化版）
        if (!/^1[3-9]\d{9}$/.test(phone)) {
            alert('请输入有效的手机号');
            return;
        }

        // 禁用按钮并开始倒计时
        btn.disabled = true;
        var countdown = 60;
        btn.textContent = `重新发送(${countdown}s)`;

        var timer = setInterval(function() {
            countdown--;
            btn.textContent = `重新发送(${countdown}s)`;

            if (countdown <= 0) {
                clearInterval(timer);
                btn.disabled = false;
                btn.textContent = '获取验证码';
            }
        }, 1000);

        // 这里应该发送AJAX请求到后端获取验证码
        console.log('向手机号 ' + phone + ' 发送验证码...');
    });

    document.getElementById('registerForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const contextPath = '${pageContext.request.contextPath}'; // 动态获取上下文
        const formData = {
            username: document.getElementById('regUsername').value,
            phone: document.getElementById('regPhone').value,
            password: document.getElementById('regPassword').value,
            verifyCode: document.getElementById('verifyCode').value
        };

        fetch(contextPath + '/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(formData).toString() // 更安全的参数处理
        })
            .then(response => {
                if (!response.ok) throw new Error('网络响应错误');
                return response.json();
            })
            .then(data => {
                alert(data.message);
                if (data.success) window.location.href = '/PointsMall/loginPage';
            })
            .catch(error => {
                console.error('注册失败:', error);
                alert('注册异常，请重试');
            });
    });
</script>
</body>
</html>