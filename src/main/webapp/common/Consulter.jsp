<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>在线客服</title>
  <!-- 添加favicon引用 -->
  <%@ include file="../../common/header.jsp" %>
  <script src="https://cdn.tailwindcss.com"></script>
  <link href="https://cdn.jsdelivr.net/npm/font-awesome@4.7.0/css/font-awesome.min.css" rel="stylesheet">
  <link href="css/custom.css" rel="stylesheet">
  <script>
    tailwind.config = {
      theme: {
        extend: {
          colors: {
            primary: '#165DFF',
            secondary: '#FF7D00',
          }
        }
      }
    }
  </script>
</head>
<body class="bg-gray-50 font-sans text-dark">
<!-- 顶部导航栏 -->
<header class="bg-white shadow-sm fixed top-0 left-0 right-0 z-50">
  <div class="container mx-auto px-4 py-3 flex items-center justify-between">
    <div class="flex items-center space-x-2">
      <i class="fa fa-comments text-primary text-xl"></i>
      <span class="text-lg font-bold text-primary">在线客服</span>
    </div>
  </div>
</header>

<!-- 返回首页按钮 -->
<a href="/PointsMall/home"
   class="fixed bottom-6 right-6 z-50
          w-14 h-14 bg-primary hover:bg-primary/90
          text-white rounded-full flex items-center justify-center
          shadow-lg transition-all duration-300 transform hover:scale-110">
  <i class="fa fa-home text-xl"></i>
</a>

<!-- 主内容区 -->
<main class="container mx-auto px-4 pt-16 pb-10">
  <!-- 客服界面 -->
  <div class="bg-white rounded-2xl shadow-lg overflow-hidden">
    <!-- 客服信息 -->
    <div class="p-4 border-b border-gray-100 flex items-center">
      <div class="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center mr-3">
        <i class="fa fa-user text-primary"></i>
      </div>
      <div>
        <h3 class="font-medium">客服</h3>
        <p class="text-xs text-success flex items-center">
          <span class="inline-block w-2 h-2 rounded-full bg-success mr-1"></span>在线 · 服务中
        </p>
      </div>
    </div>

    <!-- 聊天内容 -->
    <div class="p-6 overflow-y-auto h-[400px]" id="chatContainer">
      <!-- 系统消息 -->
      <div class="text-center mb-4">
        <span class="text-xs text-gray-400 bg-gray-100 py-1 px-3 rounded-full">今天 14:30</span>
      </div>

      <!-- 客服消息 -->
      <div class="flex items-end mb-6">
        <div class="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center mr-3">
          <i class="fa fa-user text-primary"></i>
        </div>
        <div class="max-w-[80%]">
          <div class="chat-bubble-cs p-4 mb-1">
            <p>您好！我是客服，很高兴为您服务~ 请问有什么可以帮助您的吗？</p>
          </div>
          <span class="text-xs text-gray-400 ml-2">14:30</span>
        </div>
      </div>

      <!-- 用户消息 -->
      <div class="flex items-end justify-end mb-6">
        <div class="max-w-[80%]">
          <div class="chat-bubble-user p-4 mb-1">
            <p>你好，我想咨询一下积分兑换的问题</p>
          </div>
          <div class="flex justify-end">
            <span class="text-xs text-gray-400 mr-2">14:31</span>
          </div>
        </div>
      </div>

      <!-- 客服消息 -->
      <div class="flex items-end mb-6">
        <div class="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center mr-3">
          <i class="fa fa-user text-primary"></i>
        </div>
        <div class="max-w-[80%]">
          <div class="chat-bubble-cs p-4 mb-1">
            <p>您好，您可以在积分商城中查看具体的奖品和所需积分，确认积分足够后点击兑换即可</p>
          </div>
          <span class="text-xs text-gray-400 ml-2">14:32</span>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="p-4 border-t border-gray-100">
      <div class="flex">
        <input type="text" id="messageInput" placeholder="输入您的问题..." class="flex-1 px-4 py-3 rounded-l-lg border border-gray-200 focus:outline-none focus:border-primary">
        <button id="sendBtn" class="bg-primary hover:bg-primary/90 text-white font-medium py-3 px-6 rounded-r-lg">
          <i class="fa fa-paper-plane mr-2"></i> 发送
        </button>
      </div>
    </div>
  </div>
</main>

<!-- 页脚 -->
<footer class="bg-white border-t border-gray-200 py-8">
  <div class="container mx-auto px-4">
    <div class="flex flex-col md:flex-row justify-between items-center">
      <div class="mb-4 md:mb-0">
        <div class="flex items-center space-x-2">
          <i class="fa fa-comments text-primary text-xl"></i>
          <span class="text-lg font-bold text-primary">在线客服</span>
        </div>
        <p class="text-gray-500 text-sm mt-2">© 2025 在线客服. 保留所有权利.</p>
      </div>
      <div class="flex space-x-6">
        <a href="#" class="text-gray-500 hover:text-primary transition-colors">
          <i class="fa fa-weibo text-xl"></i>
        </a>
        <a href="#" class="text-gray-500 hover:text-primary transition-colors">
          <i class="fa fa-wechat text-xl"></i>
        </a>
        <a href="#" class="text-gray-500 hover:text-primary transition-colors">
          <i class="fa fa-qq text-xl"></i>
        </a>
        <a href="#" class="text-gray-500 hover:text-primary transition-colors">
          <i class="fa fa-envelope text-xl"></i>
        </a>
      </div>
    </div>
    <div class="border-t border-gray-100 mt-6 pt-6 flex flex-col md:flex-row justify-between items-center">
      <div class="text-gray-500 text-sm mb-4 md:mb-0">
        <a href="#" class="hover:text-primary transition-colors mr-4">服务条款</a>
        <a href="#" class="hover:text-primary transition-colors mr-4">隐私政策</a>
        <a href="#" class="hover:text-primary transition-colors">帮助中心</a>
      </div>
      <div class="text-gray-500 text-sm">
        联系我们: support@example.com | 400-123-4567
      </div>
    </div>
  </div>
</footer>

<script>
  // 发送消息功能
  document.getElementById('sendBtn').addEventListener('click', sendMessage);
  document.getElementById('messageInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') sendMessage();
  });

  function sendMessage() {
    const input = document.getElementById('messageInput');
    const message = input.value.trim();
    if (!message) return;

    const container = document.getElementById('chatContainer');
    const now = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});

    // 添加用户消息
    container.insertAdjacentHTML('beforeend', `
                <div class="flex items-end justify-end mb-6">
                    <div class="max-w-[80%]">
                        <div class="chat-bubble-user p-4 mb-1">
                            <p>${message}</p>
                        </div>
                        <div class="flex justify-end">
                            <span class="text-xs text-gray-400 mr-2">${now}</span>
                        </div>
                    </div>
                </div>
            `);

    input.value = '';
    container.scrollTop = container.scrollHeight;

    // 模拟客服回复
    setTimeout(() => {
      let reply = "感谢您的提问，我们会尽快为您解答";
      if (message.includes('积分') || message.includes('兑换')) {
        reply = "积分兑换可在'我的积分'页面查看，100积分=1元，兑换后3个工作日内到账";
      } else if (message.includes('订单')) {
        reply = "您可以在'我的订单'页面查看订单状态，如有问题请提供订单号";
      }

      container.insertAdjacentHTML('beforeend', `
                    <div class="flex items-end mb-6">
                        <div class="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center mr-3">
                            <i class="fa fa-user text-primary"></i>
                        </div>
                        <div class="max-w-[80%]">
                            <div class="chat-bubble-cs p-4 mb-1">
                                <p>${reply}</p>
                            </div>
                            <span class="text-xs text-gray-400 ml-2">${now}</span>
                        </div>
                    </div>
                `);

      container.scrollTop = container.scrollHeight;
    }, 1500);
  }
</script>
</body>
</html>
