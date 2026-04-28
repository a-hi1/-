<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>参测人员注册 - MBTI职业性格测试系统</title>
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

    /* 中央卡片 */
    .card { position: relative; z-index: 10; display: flex; width: 960px; max-width: 95vw; height: auto; max-height: 90vh; background: #fff; border-radius: 16px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15); overflow: hidden; animation: slideUp 0.6s cubic-bezier(0.34, 1.56, 0.64, 1); }

    @keyframes slideUp {
      from { opacity: 0; transform: translateY(40px); }
      to { opacity: 1; transform: translateY(0); }
    }

    /* 卡片左侧插画区 */
    .card-left { flex: 1; background: #fff; position: relative; display: flex; align-items: center; justify-content: center; min-height: 520px; }
    .card-left .logo-top { position: absolute; top: 30px; left: 0; width: 100%; display: flex; justify-content: center; padding-right: 90px; }
    .illustration { width: 85%; max-width: 340px; position: relative; z-index: 2; margin-bottom:20px; animation: float 4s ease-in-out infinite; }
    .badge { position: absolute; bottom: 45px; left: 50%; transform: translateX(-50%); background: linear-gradient(135deg, #eda526, #f59e0b); color: #fff; padding: 8px 24px; border-radius: 999px; font-weight: bold; font-size: 14px; letter-spacing: 1px; z-index: 3; box-shadow: 0 6px 20px rgba(237, 165, 38, 0.5); white-space: nowrap; animation: bounce 2s ease-in-out infinite; }

    @keyframes bounce {
      0%, 100% { transform: translateX(-50%) translateY(0); }
      50% { transform: translateX(-50%) translateY(-8px); }
    }

    /* 卡片右侧表单区 */
    .card-right { flex: 1; background: linear-gradient(135deg, #f8f9fb, #fafbfc); display: flex; flex-direction: column; justify-content: flex-start; align-items: center; padding: 40px 50px; overflow-y: auto; }
    .logo-main { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
    .logo-icon { width: 42px; height: 42px; fill: #eda526; animation: spin 20s linear infinite; }
    @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
    .logo-text-wrap { display: flex; flex-direction: column; }
    .logo-mbti { font-size: 32px; font-weight: 900; font-style: italic; color: #222; line-height: 1; letter-spacing: 2px; }
    .logo-sub { font-size: 13px; font-weight: 700; color: #666; letter-spacing: 1px; margin-top: 2px; }
    .form-wrap { width: 100%; max-width: 360px; }
    .input-group { display: flex; align-items: center; position: relative; margin-bottom: 20px; animation: fadeIn 0.4s ease; }
    @keyframes fadeIn { from { opacity: 0; transform: translateX(-10px); } to { opacity: 1; transform: translateX(0); } }
    .input-label { width: 70px; font-size: 14px; font-weight: 700; color: #4b5563; text-align: right; margin-right: 15px; flex-shrink: 0; }
    .input-field { flex: 1; min-width: 0; padding: 8px 0; border: none; border-bottom: 2px solid #e5e7eb; background: transparent; font-size: 14px; color: #333; outline: none; letter-spacing: 0.5px; transition: all 0.3s ease; }
    .input-field:hover { border-bottom-color: #eda526; }
    .input-field:focus { border-bottom-color: #eda526; box-shadow: 0 2px 0 #eda526; background: #fffbf0; }
    .input-field::placeholder { color: #d1d5db; }
    .gender-wrap { display: flex; align-items: center; gap: 18px; flex: 1; padding: 8px 0; border-bottom: 2px solid #e5e7eb; transition: all 0.3s; }
    .gender-wrap:hover { border-bottom-color: #eda526; }
    .gender-wrap label { display: flex; align-items: center; gap: 8px; font-size: 14px; cursor: pointer; color: #333; font-weight: 500; transition: color 0.2s; }
    .gender-wrap label:hover { color: #eda526; }
    .gender-wrap input[type="radio"] { cursor: pointer; transition: transform 0.2s; }
    .gender-wrap input[type="radio"]:hover { transform: scale(1.1); }
    .error-msg { color: #ef4444; font-size: 12px; text-align: center; margin-bottom: 15px; animation: shake 0.5s ease; }
    @keyframes shake { 0%, 100% { transform: translateX(0); } 25% { transform: translateX(-5px); } 75% { transform: translateX(5px); } }
    .action-btn { display: inline-flex; align-items: center; justify-content: center; width: 100%; height: 44px; border-radius: 10px; background: linear-gradient(135deg, #eda526, #f59e0b); color: #fff; font-size: 16px; font-weight: 700; border: none; cursor: pointer; text-decoration: none; margin-bottom: 12px; transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); box-shadow: 0 4px 15px rgba(237, 165, 38, 0.3); position: relative; overflow: hidden; }
    .action-btn::before { content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%; background: rgba(255, 255, 255, 0.2); transition: left 0.4s ease; }
    .action-btn:hover::before { left: 100%; }
    .action-btn:hover { background: linear-gradient(135deg, #df971b, #d97706); transform: translateY(-2px); box-shadow: 0 8px 25px rgba(237, 165, 38, 0.5); text-decoration: none; color: #fff; }
    .action-btn:active { transform: translateY(0); }
    .btn-light { background: #f3f4f6; color: #495057; border: 1px solid #e5e7eb; box-shadow: none; }
    .btn-light:hover { background: #e5e7eb; color: #1f2937; border-color: #d1d5db; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); text-decoration: none; }
    input:-webkit-autofill { -webkit-box-shadow: 0 0 0 30px #f8f9fb inset !important; -webkit-text-fill-color: #333 !important; }
    @media (max-width: 768px) {
      .card-left, .bg-left, .rings, .deco-dot { display: none; }
      .card { width: 100%; height: 100vh; max-width: 100%; border-radius: 0; }
      .bg-right { width: 100%; }
      .card-right { background: #fff; padding: 40px 20px; justify-content: center; }
    }
  </style>
</head>
<body>
<div class="page-wrap">
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
  <div class="card">
    <div class="card-left">
      <div class="logo-top">
        <svg viewBox="0 0 64 64" fill="#eda526" style="width:24px; height:24px; margin-right:8px;"><polygon points="32,4 56,18 56,46 32,60 8,46 8,18"/></svg>
        <span style="font-size:12px; font-weight:bold; letter-spacing:1px; line-height:24px;">职业性格测试系统</span>
      </div>
      <img class="illustration" src="${ctx}/static/img/register1.png" alt="Illustration" />
      <div class="badge">MBTI职业性格测试系统</div>
    </div>
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
          <div class="logo-sub">账号注册</div>
        </div>
      </div>
      <div class="form-wrap">
        <c:if test="${not empty error}">
          <div class="error-msg"><c:out value="${error}"/></div>
        </c:if>
        <form method="post" action="<c:url value='/register'/>" autocomplete="off">
          <div class="input-group">
            <div class="input-label">手机号</div>
            <input type="text" name="login" class="input-field" placeholder="请输入手机号" value="<c:out value='${login}'/>" required autofocus />
          </div>
          <div class="input-group">
            <div class="input-label">密 码</div>
            <input type="password" name="passwd" class="input-field" placeholder="请设置密码" required />
          </div>
          <div class="input-group">
            <div class="input-label">确认密码</div>
            <input type="password" name="passwd2" class="input-field" placeholder="请再次确认密码" required />
          </div>
          <div class="input-group">
            <div class="input-label">姓 名</div>
            <input type="text" name="name" class="input-field" placeholder="请输入真实姓名" value="<c:out value='${name}'/>" required />
          </div>
          <div class="input-group">
            <div class="input-label">性 别</div>
            <div class="gender-wrap">
              <label>
                <input type="radio" name="gender" value="M" <c:if test="${empty gender || gender == 'M'}">checked</c:if> /> 男
              </label>
              <label>
                <input type="radio" name="gender" value="F" <c:if test="${gender == 'F'}">checked</c:if> /> 女
              </label>
            </div>
          </div>
          <div class="input-group">
            <div class="input-label">批 次</div>
            <select name="teamId" class="input-field" required>
              <option value="">请选择批次</option>
              <c:forEach items="${teams}" var="t">
                <option value="${t.id}" <c:if test="${not empty teamId && teamId == (t.id)}">selected</c:if>><c:out value="${t.name}"/></option>
              </c:forEach>
            </select>
          </div>
          <div style="margin-top: 30px;">
            <button type="submit" class="action-btn">立即注册</button>
            <a href="${ctx}/login" class="action-btn btn-light" style="letter-spacing: 2px;">返回登录</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
</body>
</html>
