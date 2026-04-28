<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>登录 - MBTI职业性格测试系统</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    html { scroll-behavior: smooth; }
    body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Microsoft YaHei", sans-serif; background: linear-gradient(135deg, #eaeaea 0%, #f5f5f5 100%); color: #333; overflow: hidden; }

    /* 页面分屏背景与同心圆装饰 */
    .page-wrap { position: relative; width: 100vw; height: 100vh; display: flex; align-items: center; justify-content: center; }
    .bg-left { position: absolute; left: 0; top: 0; width: 50%; height: 100%; background: linear-gradient(135deg, #f6dc9c 0%, #f9e5b5 100%); z-index: 0; animation: gradientShift 8s ease infinite; }
    .bg-right { position: absolute; right: 0; top: 0; width: 50%; height: 100%; background: linear-gradient(135deg, #ececec 0%, #f9f9f9 100%); z-index: 0; }
    
    @keyframes gradientShift {
      0%, 100% { filter: brightness(1); }
      50% { filter: brightness(1.05); }
    }
    
    /* 同心圆背景线 */
    .rings { position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%); z-index: 0; pointer-events: none; }
    .ring { position: absolute; border: 1px solid rgba(220, 160, 40, 0.2); border-radius: 50%; top: 50%; left: 50%; transform: translate(-50%, -50%); transition: all 0.3s ease; }
    .ring-1 { width: 600px; height: 600px; animation: float 6s ease-in-out infinite; }
    .ring-2 { width: 900px; height: 900px; animation: float 8s ease-in-out infinite reverse; }
    .ring-3 { width: 1300px; height: 1300px; border-color: rgba(220, 160, 40, 0.1); animation: float 10s ease-in-out infinite; }
    .ring-4 { width: 1800px; height: 1800px; border-color: rgba(220, 160, 40, 0.05); animation: float 12s ease-in-out infinite reverse; }

    @keyframes float {
      0%, 100% { transform: translate(-50%, -50%) scale(1); }
      50% { transform: translate(-50%, -50%) scale(1.02); }
    }

    /* 周边小圆点 */
    .deco-dot { position: absolute; border-radius: 50%; z-index: 1; pointer-events: none; animation: pulse 3s ease-in-out infinite; }
    .dot-1 { width: 60px; height: 60px; background: #eda629; right: 15%; top: 15%; animation-delay: 0s; box-shadow: 0 0 20px rgba(237, 166, 41, 0.4); }
    .dot-2 { width: 40px; height: 40px; background: #e3a232; right: 25%; bottom: 15%; animation-delay: 1s; box-shadow: 0 0 15px rgba(227, 162, 50, 0.3); }
    .dot-3 { width: 20px; height: 20px; background: #f4cf82; left: 20%; top: 20%; animation-delay: 2s; box-shadow: 0 0 10px rgba(244, 207, 130, 0.2); }

    @keyframes pulse {
      0%, 100% { transform: scale(1); opacity: 0.8; }
      50% { transform: scale(1.1); opacity: 1; }
    }

    /* 中央登录卡片 */
    .card { position: relative; z-index: 10; display: flex; flex-direction: column; width: 380px; max-width: 95vw; min-height: auto; background: #fff; border-radius: 16px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15); overflow: hidden; animation: slideUp 0.6s cubic-bezier(0.34, 1.56, 0.64, 1); }

    @keyframes slideUp {
      from { opacity: 0; transform: translateY(40px); }
      to { opacity: 1; transform: translateY(0); }
    }

    /* 卡片表单区 */
    .card-right { width: 100%; background: #f8f9fb; display: flex; flex-direction: column; padding: 34px 32px; overflow-y: auto; }
    
    /* 隐藏左侧图片 */
    .card-left { display: none !important; }
    
    /* 选项卡 */
    .user-type-tabs { display: flex; gap: 0; margin-bottom: 24px; border-bottom: 2px solid #e5e7eb; }
    .user-type-tab { flex: 1; padding: 12px 16px; text-align: center; cursor: pointer; background: transparent; border: none; font-size: 14px; font-weight: 600; color: #9ca3af; transition: all 0.3s ease; position: relative; }
    .user-type-tab:hover { color: #333; }
    .user-type-tab.active { color: #eda526; }
    .user-type-tab.active::after { content: ''; position: absolute; bottom: -2px; left: 0; right: 0; height: 2px; background: #eda526; }
    
    .logo-main { display: flex; align-items: center; gap: 12px; margin: 0 auto 18px; justify-content: center; }
    .logo-icon { width: 42px; height: 42px; fill: #eda526; animation: spin 20s linear infinite; }
    @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
    .logo-text-wrap { display: flex; flex-direction: column; }
    .logo-mbti { font-size: 32px; font-weight: 900; font-style: italic; color: #222; line-height: 1; letter-spacing: 2px; }
    .logo-sub { font-size: 13px; font-weight: 700; color: #666; letter-spacing: 1px; margin-top: 2px; }

    .form-wrap { width: 100%; max-width: 320px; margin: 0 auto; }

    .input-group { display: flex; align-items: center; margin-bottom: 24px; position: relative; animation: fadeIn 0.4s ease; }
    @keyframes fadeIn { from { opacity: 0; transform: translateX(-10px); } to { opacity: 1; transform: translateX(0); } }
    
    .input-icon { width: 20px; height: 20px; fill: #eda526; margin-right: 12px; margin-bottom: 4px; }
    .input-field { flex: 1; min-width: 0; border: none; border-bottom: 2px solid #e5e7eb; background: transparent; padding: 8px 0; font-size: 14px; color: #333; outline: none; letter-spacing: 0.5px; transition: all 0.3s ease; }
    .input-field:hover { border-bottom-color: #eda526; }
    .input-field:focus { border-bottom-color: #eda526; box-shadow: 0 2px 0 #eda526; }
    .input-field::placeholder { color: #d1d5db; }
    
    .error-msg { color: #ef4444; font-size: 12px; text-align: center; margin-bottom: 12px; animation: shake 0.5s ease; }
    @keyframes shake { 0%, 100% { transform: translateX(0); } 25% { transform: translateX(-5px); } 75% { transform: translateX(5px); } }

    .action-btn { display: flex; align-items: center; justify-content: center; width: 100%; height: 42px; border-radius: 8px; background: linear-gradient(135deg, #eda526, #f59e0b); color: #fff; font-size: 15px; font-weight: 600; border: none; cursor: pointer; text-decoration: none; margin-bottom: 12px; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); box-shadow: 0 4px 15px rgba(237, 165, 38, 0.3); position: relative; overflow: hidden; }
    .action-btn::before { content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%; background: rgba(255, 255, 255, 0.2); transition: left 0.4s ease; }
    .action-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(237, 165, 38, 0.5); }
    .action-btn:hover::before { left: 100%; }
    .action-btn:active { transform: translateY(0); }
    
    /* 覆盖掉 autocomplete 背景色 */
    input:-webkit-autofill { -webkit-box-shadow: 0 0 0 30px #f8f9fb inset !important; -webkit-text-fill-color: #333 !important; }

    .login-footer { text-align: center; margin-top: 8px; font-size: 12px; color: #9ca3af; }
    .login-footer a { color: #eda526; text-decoration: none; transition: color 0.2s; }
    .login-footer a:hover { color: #df971b; }

    @media (max-width: 768px) {
      .bg-left, .rings, .deco-dot { display: none; }
      .card { width: 100%; height: 100vh; max-width: 100%; border-radius: 0; }
      .card-right { padding: 40px 20px; justify-content: center; }
      .user-type-tabs { margin-bottom: 20px; }
    }
  </style>
</head>
<body>

<div class="page-wrap">
  <!-- 背景层 -->
  <div class="bg-left"></div>
  <div class="bg-right"></div>
  <div class="rings">
    <div class="ring ring-1"></div>
    <div class="ring ring-2"></div>
    <div class="ring ring-3"></div>
    <div class="ring ring-4"></div>
  </div>
  <div class="deco-dot dot-1"></div>
  <div class="deco-dot dot-2"></div>
  <div class="deco-dot dot-3"></div>

  <!-- 卡片层 -->
  <div class="card">
    <div class="card-right">
      <div class="logo-main">
        <svg class="logo-icon" viewBox="0 0 64 64">
          <polygon points="32,4 56,18 56,46 32,60 8,46 8,18"/>
          <path d="M20 34c10-1 18-8 22-18 4 10 1 21-7 27-8 6-17 4-23 1" fill="none" stroke="#222" stroke-width="2" stroke-linecap="round"/>
          <path d="M30 22v28" stroke="#222" stroke-width="2" stroke-linecap="round"/>
          <path d="M30 30c6 0 11 2 16 6" stroke="#222" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <div class="logo-text-wrap">
          <div class="logo-mbti">MBTI</div>
          <div class="logo-sub">职业性格测试系统</div>
        </div>
      </div>

      <!-- 用户类型选项卡 -->
      <div class="user-type-tabs">
        <button type="button" class="user-type-tab active" data-type="admin" onclick="switchUserType('admin')">管理员登录</button>
        <button type="button" class="user-type-tab" data-type="personnel" onclick="switchUserType('personnel')">参测人员登录</button>
      </div>

      <div class="form-wrap">
        <c:if test="${not empty error}">
          <div class="error-msg"><c:out value="${error}"/></div>
        </c:if>

        <form method="post" action="<c:url value='/login'/>" autocomplete="off" id="loginForm">
          <input type="hidden" name="userType" id="userTypeField" value="admin" />
          
          <!-- 管理员登录字段 -->
          <div id="admin-fields">
            <div class="input-group">
              <svg class="input-icon" viewBox="0 0 24 24"><path d="M12 12c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0 2c4.42 0 8 1.79 8 4v2H4v-2c0-2.21 3.58-4 8-4z"/></svg>
              <input type="text" name="login" id="login-field" class="input-field" placeholder="请输入用户名" value="<c:out value='${login}'/>" required autofocus />
            </div>
          </div>

          <!-- 参测人员登录字段 -->
          <div id="personnel-fields" style="display: none;">
            <div class="input-group">
              <svg class="input-icon" viewBox="0 0 24 24"><path d="M17 10.5V7c0-.55-.45-1-1-1H4c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h12c.55 0 1-.45 1-1v-3.5l4 4v-11l-4 4z"/></svg>
              <input type="text" name="login" id="phone-field" class="input-field" placeholder="请输入手机号" />
            </div>
          </div>

          <div class="input-group">
            <svg class="input-icon" viewBox="0 0 24 24"><path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/></svg>
            <input type="password" name="passwd" class="input-field" placeholder="请输入密码" required />
          </div>

          <button type="submit" class="action-btn">登录</button>
          <a href="${ctx}/register" class="action-btn" style="background: linear-gradient(135deg, #64748b, #475569); margin-bottom: 8px;">注册新账号</a>
        </form>

        <div class="login-footer">
          <p>首次使用？请 <a href="${ctx}/register">注册账号</a></p>
        </div>
      </div>

    </div>
  </div>
</div>

<script>
function switchUserType(type) {
  // 更新选项卡状态
  document.querySelectorAll('.user-type-tab').forEach(tab => {
    tab.classList.remove('active');
  });
  document.querySelector('[data-type="' + type + '"]').classList.add('active');
  
  // 更新隐藏字段
  document.getElementById('userTypeField').value = type;
  
  // 切换输入字段
  const adminFields = document.getElementById('admin-fields');
  const personnelFields = document.getElementById('personnel-fields');
  const loginField = document.getElementById('login-field');
  const phoneField = document.getElementById('phone-field');
  
  if (type === 'admin') {
    adminFields.style.display = 'block';
    personnelFields.style.display = 'none';
    loginField.required = true;
    loginField.focus();
    phoneField.required = false;
  } else {
    adminFields.style.display = 'none';
    personnelFields.style.display = 'block';
    loginField.required = false;
    phoneField.focus();
    phoneField.required = true;
  }
}

// 表单提交时确保使用正确的input字段值
document.getElementById('loginForm').addEventListener('submit', function(e) {
  const userType = document.getElementById('userTypeField').value;
  const loginField = document.getElementById('login-field');
  const phoneField = document.getElementById('phone-field');
  
  // 确保传递正确的字段到login输入框
  if (userType === 'admin') {
    if (!loginField.value.trim()) {
      e.preventDefault();
      alert('请输入用户名');
      loginField.focus();
    }
  } else {
    if (!phoneField.value.trim()) {
      e.preventDefault();
      alert('请输入手机号');
      phoneField.focus();
    }
    // 将手机号值复制到隐藏的login字段
    loginField.value = phoneField.value;
  }
});
</script>

</body>
</html>
