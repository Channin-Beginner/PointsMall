<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>每日任务 - 积分中心</title>
  <!-- 添加favicon引用 -->
  <%@ include file="/common/header.jsp" %>
  <!-- 引入 Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>
  <!-- 引入 Font Awesome -->
  <link href="https://cdn.jsdelivr.net/npm/font-awesome@4.7.0/css/font-awesome.min.css" rel="stylesheet">
  <!-- Tailwind 配置 -->
  <script>
    tailwind.config = {
      theme: {
        extend: {
          colors: {
            primary: '#165DFF',
            secondary: '#FF7D00',
            success: '#00B42A',
            warning: '#FF7D00',
            danger: '#F53F3F',
            info: '#86909C',
            light: '#F2F3F5',
            dark: '#1D2129',
          },
          fontFamily: {
            inter: ['Inter', 'system-ui', 'sans-serif'],
          },
        }
      }
    }
  </script>
  <style type="text/tailwindcss">
    @layer utilities {
      .content-auto {
        content-visibility: auto;
      }
      .task-card-shadow {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      }
      .btn-gradient {
        background: linear-gradient(135deg, #165DFF 0%, #4080FF 100%);
      }
      .btn-gradient:hover {
        background: linear-gradient(135deg, #0E42B3 0%, #2A6CDA 100%);
      }
      .task-item-hover {
        transition: transform 0.3s ease, box-shadow 0.3s ease;
      }
      .task-item-hover:hover {
        transform: translateY(-4px);
        box-shadow: 0 10px 25px rgba(22, 93, 255, 0.15);
      }
    }
    /* 修复iOS点击高亮 */
    * {
      -webkit-tap-highlight-color: transparent;
    }
  </style>
</head>
<body class="bg-gray-50 font-inter text-dark">

<!-- 返回首页按钮 -->
<a href="/PointsMall/home"
   class="fixed bottom-6 right-6 z-50
          w-14 h-14 bg-primary hover:bg-primary/90
          text-white rounded-full flex items-center justify-center
          shadow-lg transition-all duration-300 transform hover:scale-110">
  <i class="fa fa-home text-xl" aria-hidden="true"></i>
  <span class="sr-only">返回首页</span>
</a>

<!-- 顶部导航栏 -->
<header class="bg-white shadow-sm fixed top-0 left-0 right-0 z-50 transition-all duration-300" id="navbar">
  <div class="container mx-auto px-4 py-3 flex items-center justify-between">
    <div class="flex items-center space-x-2">
      <i class="fa fa-diamond text-primary text-2xl" aria-hidden="true"></i>
      <span class="text-xl font-bold text-primary">积分中心</span>
    </div>
  </div>
</header>

<!-- 积分悬浮框（初始值从后端获取） -->
<div id="points-floating-box" class="fixed top-4 right-4 z-50 bg-white rounded-full shadow-md flex items-center justify-center px-4 py-2 border border-gray-200">
  <i class="fa fa-diamond text-primary mr-2" aria-hidden="true"></i>
  <span class="font-bold text-primary">
    <span id="floating-points-value">0</span> 积分 <!-- 初始值设为0，通过JavaScript动态更新 -->
  </span>
</div>

<!-- 主内容区 -->
<main class="container mx-auto px-4 pt-24 pb-16">
  <!-- 页面标题 -->
  <div class="mb-8 text-center">
    <h1 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-dark">每日任务</h1>
    <p class="text-gray-500 mt-2">完成任务赢积分，兑换好礼</p>
  </div>

  <!-- 每日任务列表 -->
  <div class="bg-white rounded-2xl shadow-lg p-6 mb-10 task-card-shadow">
    <div class="flex items-center justify-between mb-6">
        <div class="flex items-center">
        <div class="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center mr-4">
          <i class="fa fa-tasks text-primary text-xl" aria-hidden="true"></i>
        </div>
        <div>
          <h2 class="text-xl font-bold">日常任务</h2>
          <p class="text-gray-500 text-sm">完成每日任务，积累更多积分</p>
        </div>
      </div>
      <div class="text-sm text-primary">
        <span id="completedTasks">0</span>/<span id="totalTasks">0</span> 已完成
      </div>
    </div>
    <div class="bg-white rounded-2xl shadow-lg p-6 mb-10 task-card-shadow">
      <!-- 签到模块（直接作为任务列表子项） -->
      <div id="check-in-section" class="mb-10">
        <div class="flex flex-col md:flex-row items-center justify-between mb-6">
          <div class="flex items-center">
            <div class="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center mr-4">
              <i class="fa fa-calendar-check-o text-primary text-xl" aria-hidden="true"></i>
            </div>
            <div>
              <h2 class="text-xl font-bold">每日签到</h2>
              <p class="text-gray-500 text-sm">连续签到可获得更多积分奖励</p>
            </div>
          </div>
          <div class="mt-4 md:mt-0">
            <span class="text-sm text-gray-500 mr-2">已连续签到:</span>
            <span class="text-primary font-bold" id="consecutiveDays">0</span>
            <span class="text-sm text-gray-500">天</span>
          </div>
        </div>

        <div class="flex justify-center mb-6">
          <button id="simpleSignBtn"
                  class="btn-gradient hover:shadow-lg text-white font-bold py-3 px-12 rounded-full text-lg transition-all duration-300 transform hover:scale-105">
            <i class="fa fa-gift mr-2" aria-hidden="true"></i> 立即签到，获取 20 积分
          </button>
        </div>

        <div class="bg-gray-50 rounded-xl p-4">
          <div class="flex items-center mb-2">
            <i class="fa fa-info-circle text-primary mr-2" aria-hidden="true"></i>
            <span class="text-sm font-medium">签到奖励规则</span>
          </div>
          <ul class="text-xs text-gray-600 space-y-1">
            <li>• 每日签到可获得10积分</li>
            <li>• 连续签到5天额外获得50积分</li>
            <li>• 连续签到15天额外获得150积分</li>
            <li>• 连续签到30天额外获得300积分+神秘礼品</li>
            <li>• 中断签到后重新开始计数</li>
          </ul>
        </div>
      </div>
    </div>
    <div id="activeTasksContainer" class="space-y-4">
      <!-- 动态生成已推出任务 -->
    </div>
  </div>

  <!-- 即将推出的任务（待推出任务 - upcoming） -->
  <div class="bg-white rounded-2xl shadow-lg p-6 task-card-shadow">
    <div class="flex items-center mb-6">
      <div class="w-12 h-12 rounded-full bg-gray-100 flex items-center justify-center mr-4">
        <i class="fa fa-hourglass-half text-gray-500 text-xl" aria-hidden="true"></i>
      </div>
      <div>
        <h2 class="text-xl font-bold">即将推出</h2>
        <p class="text-gray-500 text-sm">更多精彩任务，敬请期待</p>
      </div>
    </div>

    <div id="upcomingTasksContainer" class="space-y-4">
      <!-- 动态生成待推出任务 -->
    </div>
  </div>
</main>

<!-- 页脚 -->
<footer class="bg-white border-t border-gray-200 py-8">
  <div class="container mx-auto px-4">
    <div class="flex flex-col md:flex-row justify-between items-center">
      <div>
        <div class="flex items-center space-x-2">
          <i class="fa fa-diamond text-primary text-xl" aria-hidden="true"></i>
          <span class="text-lg font-bold text-primary">积分中心</span>
        </div>
        <p class="text-gray-500 text-sm mt-2">© 2025 积分中心. 保留所有权利.</p>
      </div>
      <div class="flex space-x-6">
        <a href="#" class="text-gray-500 hover:text-primary transition-colors" aria-label="微博">
          <i class="fa fa-weibo text-xl" aria-hidden="true"></i>
        </a>
        <a href="#" class="text-gray-500 hover:text-primary transition-colors" aria-label="微信">
          <i class="fa fa-wechat text-xl" aria-hidden="true"></i>
        </a>
        <a href="#" class="text-gray-500 hover:text-primary transition-colors" aria-label="QQ">
          <i class="fa fa-qq text-xl" aria-hidden="true"></i>
        </a>
        <a href="#" class="text-gray-500 hover:text-primary transition-colors" aria-label="邮箱">
          <i class="fa fa-envelope text-xl" aria-hidden="true"></i>
        </a>
      </div>
    </div>
    <div class="border-t border-gray-100 mt-6 pt-6 flex flex-col md:flex-row justify-between items-center">
      <div class="text-gray-500 text-sm">
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

<!-- 签到成功弹窗 -->
<div id="signModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 hidden">
  <div class="bg-white rounded-2xl p-8 max-w-md mx-auto">
    <div class="text-center">
      <div class="w-20 h-20 mx-auto rounded-full bg-green-100 flex items-center justify-center mb-4">
        <i class="fa fa-check text-green-500 text-3xl" aria-hidden="true"></i>
      </div>
      <h3 class="text-2xl font-bold mb-2">签到成功</h3>
      <p class="text-gray-500 mb-6">恭喜您获得 <span class="text-primary font-bold">20</span> 积分</p>
      <button class="bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium py-2 px-6 rounded-full"
              id="closeSignModal">关闭</button>
    </div>
  </div>
</div>

<!-- 任务完成弹窗 -->
<div id="taskModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 hidden">
  <div class="bg-white rounded-2xl p-8 max-w-md mx-auto">
    <div class="text-center">
      <div class="w-20 h-20 mx-auto rounded-full bg-blue-100 flex items-center justify-center mb-4">
        <i class="fa fa-trophy text-blue-500 text-3xl" aria-hidden="true"></i>
      </div>
      <h3 class="text-2xl font-bold mb-2">任务完成</h3>
      <p class="text-gray-500 mb-6">恭喜您获得 <span class="text-primary font-bold">10</span> 积分</p>
      <button class="bg-primary hover:bg-primary/90 text-white font-medium py-2 px-8 rounded-full"
              id="closeTaskModal">确定</button>
    </div>
  </div>
</div>

<script>
  // 页面加载时获取用户数据
  document.addEventListener('DOMContentLoaded', async () => {
    try {
      // 调用TaskServlet的GET接口获取任务数据
      const response = await fetch('/PointsMall/api/tasks', {
        method: 'GET',
        credentials: 'include' // 携带Cookie
      });

      const data = await response.json();
      // 获取最后签到日期（新增字段）
      const lastSignDate = data.lastSignDate;
      const today = new Date().toISOString().split('T')[0]; // 当前日期

      // 渲染已推出任务（taskStatus = 'active'）
      renderTasks(data.activeTasks, 'activeTasksContainer');
      // console.log(data.activeTasks);
      // 渲染待推出任务（taskStatus = 'upcoming'）
      renderTasks(data.upcomingTasks, 'upcomingTasksContainer');
      // console.log(data.upcomingTasks);
      // 禁用按钮直到次日
      if (lastSignDate == today) {
        document.getElementById('simpleSignBtn').disabled = true;
        document.getElementById('simpleSignBtn').innerHTML = '<i class="fa fa-check mr-2"></i> 今日已签到';
        document.getElementById('simpleSignBtn').classList.replace('btn-gradient', 'bg-gray-200');
      }

      if (response.ok) {
        // 更新积分和连续签到天数
        document.getElementById('floating-points-value').textContent = data.totalPoints;
        document.getElementById('consecutiveDays').textContent = data.consecutiveDays;
        document.getElementById('completedTasks').textContent = data.completedTasks;
        document.getElementById('totalTasks').textContent = data.totalTasks;
      } else {
        if (data.error == '未登录或会话过期') {
          alert('请先登录');
          window.location.href = '/login'; // 跳转登录页
        } else {
          console.error('数据获取失败:', data.error);
        }
      }
    } catch (error) {
      console.error('网络请求失败:', error);
      // alert('网络错误，请重试');
    }
  })



  // 渲染任务列表
  function renderTasks(tasks, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';

    tasks.forEach(task => {
      if (task.taskName == '每日签到') {
         return;
      }
      const taskCard = document.createElement('div');

      if (task.taskStatus == 'active') {
        const taskTypeIcon = task.taskType == 'daily' ? 'fa-calendar-check-o' : 'fa-gift';
        const taskTypeColor = task.taskType == 'daily' ? 'blue' : 'green';

        // 创建任务卡片元素，完全不使用模板字符串和
        const cardContent = createActiveTaskElement(task, taskTypeIcon, taskTypeColor);
        taskCard.appendChild(cardContent);
      } else if (task.taskStatus == 'upcoming') {
        // 创建即将推出任务元素
        const cardContent = createUpcomingTaskElement(task);
        taskCard.appendChild(cardContent);
      }

      container.appendChild(taskCard);
    });
  }


  // 创建已激活任务的DOM元素
  function createActiveTaskElement(task, taskTypeIcon, taskTypeColor) {
    // 创建根容器
    const wrapper = document.createElement('div');
    wrapper.className = 'bg-white rounded-xl shadow-md p-6 task-item-hover';

    // 第一行：任务标题和积分
    const firstRow = document.createElement('div');
    firstRow.className = 'flex items-center justify-between';

    // 左侧图标和标题
    const leftSection = document.createElement('div');
    leftSection.className = 'flex items-center space-x-4';

    // 图标容器 - 使用字符串拼接替代模板字符串
    const iconContainer = document.createElement('div');
    iconContainer.className = 'w-10 h-10 rounded-full bg-' + taskTypeColor + '-100 flex items-center justify-center';

    // 图标 - 使用字符串拼接替代模板字符串
    const icon = document.createElement('i');
    icon.className = 'fa ' + taskTypeIcon + ' text-' + taskTypeColor + '-500 text-xl';
    iconContainer.appendChild(icon);

    // 文本内容容器
    const textContainer = document.createElement('div');

    // 任务标题
    const title = document.createElement('h3');
    title.className = 'text-lg font-bold';
    title.textContent = task.taskName;

    // 任务描述
    const description = document.createElement('p');
    description.className = 'text-gray-500 text-sm';
    description.textContent = task.taskDescription;

    // 组装左侧内容
    textContainer.appendChild(title);
    textContainer.appendChild(description);
    leftSection.appendChild(iconContainer);
    leftSection.appendChild(textContainer);

    // 右侧积分
    const points = document.createElement('div');
    points.className = 'text-primary font-bold';

    // 使用与积分更新相同的方式设置文本内容
    const pointsTextNode = document.createTextNode('');
    points.appendChild(pointsTextNode);
    pointsTextNode.textContent = '+' + task.pointsAwarded + ' 积分';

    // 组装第一行
    firstRow.appendChild(leftSection);
    firstRow.appendChild(points);

    // 第二行：任务类型和按钮
    const secondRow = document.createElement('div');
    secondRow.className = 'mt-4 flex justify-between items-center';

    // 任务类型标签
    const taskType = document.createElement('div');
    taskType.className = 'text-xs text-gray-500';

    // 使用与积分更新相同的方式设置文本内容
    const taskTypeTextNode = document.createTextNode('');
    taskType.appendChild(taskTypeTextNode);
    taskTypeTextNode.textContent = task.taskType === 'daily' ? '每日任务' : '特殊任务';

    // 完成任务按钮
    const button = document.createElement('button');
    button.className = 'task-complete-btn bg-primary hover:bg-primary/90 text-white font-medium py-1.5 px-4 rounded-full text-sm transition-all duration-300';
    button.setAttribute('data-task-id', task.taskId);
    button.textContent = '完成任务';

    // 组装第二行
    secondRow.appendChild(taskType);
    secondRow.appendChild(button);

    // 组装整个卡片
    wrapper.appendChild(firstRow);
    wrapper.appendChild(secondRow);

    return wrapper;
  }

  // 创建即将推出任务的DOM元素
  function createUpcomingTaskElement(task) {
    const wrapper = document.createElement('div');
    wrapper.className = 'bg-gray-50 rounded-xl p-6 opacity-70';

    // 第一行：任务标题和状态
    const firstRow = document.createElement('div');
    firstRow.className = 'flex items-center justify-between';

    // 左侧图标和标题
    const leftSection = document.createElement('div');
    leftSection.className = 'flex items-center space-x-4';

    // 图标容器
    const iconContainer = document.createElement('div');
    iconContainer.className = 'w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center';

    const icon = document.createElement('i');
    icon.className = 'fa fa-clock-o text-gray-500 text-xl';
    iconContainer.appendChild(icon);

    // 文本内容容器
    const textContainer = document.createElement('div');

    // 任务标题
    const title = document.createElement('h3');
    title.className = 'text-lg font-bold text-gray-700';
    title.textContent = task.taskName;

    // 任务描述
    const description = document.createElement('p');
    description.className = 'text-gray-500 text-sm';
    description.textContent = task.taskDescription;

    // 组装左侧内容
    textContainer.appendChild(title);
    textContainer.appendChild(description);
    leftSection.appendChild(iconContainer);
    leftSection.appendChild(textContainer);

    // 右侧状态
    const status = document.createElement('div');
    status.className = 'text-gray-500';
    status.textContent = '即将推出';

    // 组装第一行
    firstRow.appendChild(leftSection);
    firstRow.appendChild(status);

    // 第二行：预计奖励
    const secondRow = document.createElement('div');
    secondRow.className = 'mt-4 text-xs text-gray-500';

    // 使用与积分更新相同的方式设置文本内容
    const rewardTextNode = document.createTextNode('');
    secondRow.appendChild(rewardTextNode);
    rewardTextNode.textContent = '预计奖励: +' + task.pointsAwarded + ' 积分';

    // 组装整个卡片
    wrapper.appendChild(firstRow);
    wrapper.appendChild(secondRow);

    return wrapper;
  }

  // 签到按钮点击事件
  document.getElementById('simpleSignBtn').addEventListener('click', async () => {
    try {
      // 调用TaskServlet的POST接口处理签到
      const response = await fetch('/PointsMall/api/tasks?action=signIn', {
        method: 'POST',
        credentials: 'include' // 携带Cookie
      });

      const data = await response.json();
      // 这里打印完整响应，确认后端返回的真实结构
      console.log('签到响应:', data);
      if (response.ok) {
        // 更新积分和连续签到天数
        const pointsElement = document.getElementById('floating-points-value');
        pointsElement.textContent = data.totalPoints;

        document.getElementById('consecutiveDays').textContent = data.consecutiveDays;
        document.getElementById('completedTasks').textContent = data.completedTasks;
        // 显示弹窗
        document.getElementById('signModal').classList.remove('hidden');
        禁用签到按钮
        disableSignButton();
      } else {
        alert(data.error || '签到失败');
        if (data.error == '未登录或会话过期') {
          window.location.href = '/login'; // 跳转登录页
        }
      }
    } catch (error) {
      console.error('签到请求失败:', error);
      // alert('网络错误，请重试');
    }
  });

  // 禁用签到按钮
  function disableSignButton() {
    const signBtn = document.getElementById('simpleSignBtn');
    signBtn.disabled = true;
    signBtn.innerHTML = '<i class="fa fa-check mr-2"></i> 今日已签到';
    signBtn.classList.replace('btn-gradient', 'bg-gray-200');
    signBtn.style.cursor = 'not-allowed';
  }

  // 关闭弹窗
  document.getElementById('closeSignModal').addEventListener('click', () => {
    document.getElementById('signModal').classList.add('hidden');
    // 更新页面显示（可重新获取用户信息）
    location.reload();
  });
</script>
</body>
</html>