<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="参测人员注册" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title><c:out value="${pageTitle}" default="MBTI职业性格测试系统"/></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/app.css" />
  <style>
    body{margin:0;background:#fff;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",Arial,sans-serif}
    .reg-scene{min-height:100vh;display:flex;align-items:center;justify-content:center;position:relative;overflow:hidden;padding:24px}
    .reg-bg{position:absolute;left:0;top:0;width:50%;height:100%;background:#f6dca1;z-index:0}

    .reg-ring{
      position:absolute;z-index:1;pointer-events:none;
      width:1120px;height:1120px;right:-210px;top:50%;transform:translateY(-50%);
    }
    .reg-ring img{width:100%;height:100%}

    .reg-dot{position:absolute;z-index:2;pointer-events:none}
    .reg-dot.tr{width:62px;height:62px;right:86px;top:56px}
    .reg-dot.br{width:42px;height:42px;right:300px;bottom:95px}
    .reg-dot img{width:100%;height:100%}

    .reg-card{position:relative;z-index:3;width:340px;max-width:100%;background:#fff;border-radius:2px;
      box-shadow:6px 10px 28px rgba(0,0,0,.12);padding:16px 18px 14px}

    .reg-brand{display:flex;align-items:center;justify-content:center;gap:8px;margin-top:2px;margin-bottom:8px}
    .reg-brand .logo-mark{width:46px;height:46px}
    .reg-brand .logo-text{display:flex;flex-direction:column;line-height:1}
    .reg-brand .logo-text .mbti{font-weight:900;font-size:22px;letter-spacing:.04em;color:#2c2c2c}
    .reg-brand .logo-text .sub{font-size:10px;color:#6b6b6b;letter-spacing:.2em;margin-top:4px}

    .reg-error{margin:8px 0 6px}

    /* 下方白色区域（表单承载区） */
    .reg-panel{margin-top:8px;background:#fff;border-radius:2px;box-shadow:0 8px 20px rgba(240,168,48,.25);padding:12px 12px 14px;position:relative}
    .reg-form{display:flex;flex-direction:column;gap:10px;position:relative;z-index:2;padding:2px 4px 0}

    .field-pill{display:flex;align-items:center;gap:8px;height:24px;border-radius:999px;background:#f6d692;padding:0 12px;color:#fff}
    .field-pill .lbl{width:72px;flex:0 0 72px;font-size:12px;font-weight:900;letter-spacing:.18em;text-align:left;text-shadow:0 1px 0 rgba(0,0,0,.06)}
    .field-pill input,.field-pill select{flex:1;min-width:0;border:none;background:transparent;padding:0;height:100%;font-size:12px;color:#4a4a4a}
    .field-pill input::placeholder{color:#c8c8c8}
    .field-pill input:focus,.field-pill select:focus{outline:none;box-shadow:none}

    .gender-wrap{display:flex;align-items:center;gap:12px;flex:1;color:#6b5840;font-size:12px;font-weight:700}
    .gender-wrap label{display:flex;align-items:center;gap:4px;margin:0;color:#6b5840;font-size:12px;font-weight:700;white-space:nowrap}
    .gender-wrap input[type="radio"]{margin:0}

    .reg-illustration{position:relative;z-index:1;margin-top:10px}
    .reg-illustration img{display:block;width:100%;height:auto}

    .reg-actions{position:absolute;left:0;right:0;bottom:22px;display:flex;justify-content:center;gap:18px;z-index:2}
    .reg-actions .btn-big{width:90px;height:26px;border-radius:999px;display:flex;align-items:center;justify-content:center;
      background:#f4b72f;border:none;color:#fff;font-size:12px;font-weight:800;text-decoration:none;box-shadow:none}
    .reg-actions .btn-big:hover{text-decoration:none;filter:brightness(.96)}

    @media (max-width:520px){
      .reg-ring,.reg-dot,.reg-bg{display:none}
      .reg-card{width:100%;max-width:340px}
      .field-pill .lbl{width:64px;flex-basis:64px;letter-spacing:.12em}
    }
  </style>
</head>
<body>
<div class="reg-scene">
  <div class="reg-bg"></div>
  <div class="reg-ring"><img src="${ctx}/static/img/ring-deco.svg" alt=""/></div>
  <div class="reg-dot tr"><img src="${ctx}/static/img/dot-gold-lg.svg" alt=""/></div>
  <div class="reg-dot br"><img src="${ctx}/static/img/dot-gold-sm.svg" alt=""/></div>
  <div class="reg-card">
      <div class="reg-brand" aria-label="MBTI职业性格测试系统">
        <svg class="logo-mark" viewBox="0 0 64 64" role="img" aria-hidden="true">
          <polygon points="32,4 56,18 56,46 32,60 8,46 8,18" fill="#f0a830"/>
          <path d="M20 34c10-1 18-8 22-18 4 10 1 21-7 27-8 6-17 4-23 1" fill="none" stroke="#1f1f1f" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M30 22v28" stroke="#1f1f1f" stroke-width="2.2" stroke-linecap="round"/>
          <path d="M30 30c6 0 11 2 16 6" stroke="#1f1f1f" stroke-width="2.2" stroke-linecap="round"/>
        </svg>
        <div class="logo-text">
          <div class="mbti">MBTI</div>
          <div class="sub">职业性格测试系统</div>
        </div>
      </div>

      <c:if test="${not empty error}">
        <div class="alert alert-error reg-error"><c:out value="${error}"/></div>
      </c:if>

      <div class="reg-panel">
        <form method="post" action="${ctx}/register" class="reg-form" autocomplete="off">
          <div class="field-pill">
            <div class="lbl">手机号:</div>
            <input name="login" value="<c:out value='${login}'/>" placeholder="请输入手机号" required />
          </div>

          <div class="field-pill">
            <div class="lbl">密 码:</div>
            <input name="passwd" type="password" placeholder="请输入密码" required />
          </div>

          <div class="field-pill">
            <div class="lbl">确认密码:</div>
            <input name="passwd2" type="password" placeholder="请再次输入密码" required />
          </div>

          <div class="field-pill">
            <div class="lbl">姓 名:</div>
            <input name="name" value="<c:out value='${name}'/>" placeholder="请输入姓名" required />
          </div>

          <div class="field-pill">
            <div class="lbl">性 别:</div>
            <div class="gender-wrap">
              <label>
                <input type="radio" name="gender" value="M" <c:if test="${empty gender || gender == 'M'}">checked</c:if> />
                男
              </label>
              <label>
                <input type="radio" name="gender" value="F" <c:if test="${gender == 'F'}">checked</c:if> />
                女
              </label>
            </div>
          </div>

          <div class="field-pill">
            <div class="lbl">批 次:</div>
            <select name="teamId" required>
              <option value="">请选择批次</option>
              <c:forEach items="${teams}" var="t">
                <option value="${t.id}" <c:if test="${not empty teamId && teamId == (t.id)}">selected</c:if>><c:out value="${t.name}"/></option>
              </c:forEach>
            </select>
          </div>

          <div class="reg-illustration" aria-hidden="true">
            <img src="${ctx}/static/img/register1.png" alt="" />
            <div class="reg-actions">
              <button class="btn btn-warn btn-big" type="submit">注册</button>
              <a class="btn btn-warn btn-big" href="${ctx}/login">返回</a>
            </div>
          </div>
        </form>
      </div>
  </div>
</div>
</body>
</html>
