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
    :root{
      --bg:#ececec;
      --gold:#f2ba34;
      --gold-soft:#f6dd9e;
      --card:#f3f3f3;
      --text:#2f3338;
      --muted:#7a8796;
      --line:#e6bc66;
    }

    *{box-sizing:border-box;margin:0;padding:0}
    body{margin:0;font-family:"Microsoft YaHei","PingFang SC","Segoe UI",sans-serif;background:var(--bg);color:var(--text)}

    .page{position:relative;min-height:100vh;display:flex;align-items:center;justify-content:center;overflow:hidden;padding:20px}
    .bg-gold{position:absolute;left:0;top:0;width:50%;height:100%;background:var(--gold-soft);z-index:0}
    .ring-deco{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:1250px;height:1250px;z-index:1;pointer-events:none;opacity:.92}
    .ring-deco img{width:100%;height:100%}
    .dot{position:absolute;z-index:2;pointer-events:none}
    .dot-tr{width:70px;height:70px;right:140px;top:96px}
    .dot-br{width:52px;height:52px;right:440px;bottom:112px}
    .dot img{width:100%;height:100%}

    .card{
      position:relative;
      z-index:3;
      width:820px;
      max-width:calc(100vw - 28px);
      min-height:380px;
      background:var(--card);
      border-radius:3px;
      box-shadow:0 10px 30px rgba(227,166,41,.28);
      display:grid;
      grid-template-columns:1fr 1fr;
      overflow:hidden;
    }

    .card-left{position:relative;overflow:hidden}
    .card-deco-lines{position:absolute;left:40px;top:56px;width:206px;opacity:.55}
    .card-deco-dot{position:absolute;width:16px;height:16px;border-radius:50%;background:#d9d9d9;opacity:.7}
    .card-deco-dot-1{left:250px;top:58px}
    .card-deco-dot-2{left:271px;top:80px}
    .card-hero{position:absolute;left:24px;bottom:-2px;width:334px;z-index:2}
    .card-hero img{display:block;width:100%;height:auto}
    .card-badge{position:absolute;left:86px;bottom:74px;z-index:3;min-width:206px;height:30px;border-radius:999px;background:var(--gold);color:#fff;font-size:13px;font-weight:800;letter-spacing:.04em;display:flex;align-items:center;justify-content:center}

    .card-right{padding:34px 42px 34px 8px;display:flex;flex-direction:column;justify-content:flex-start}
    .logo{display:flex;align-items:center;gap:10px;margin-bottom:26px}
    .logo-mark{width:50px;height:50px;flex:0 0 auto}
    .logo-text .mbti{font-weight:900;font-size:52px;line-height:.82;letter-spacing:.03em;font-style:italic;color:#1f2328;transform:scaleY(.62);transform-origin:left top;display:block;margin-bottom:-16px}
    .logo-text .sub{font-size:10px;color:#31363f;letter-spacing:.2em;font-weight:700;white-space:nowrap}

    .err{background:#fff2f2;border:1px solid #f4c2c2;color:#a12626;padding:8px 10px;border-radius:8px;font-size:13px;margin-bottom:12px}

    .form{display:flex;flex-direction:column;gap:16px}
    .field{display:flex;align-items:flex-end;gap:10px}
    .field-icon{width:26px;height:26px;color:var(--gold);display:flex;align-items:center;justify-content:center;margin-bottom:7px}
    .field-icon svg{width:21px;height:21px;fill:currentColor}
    .field input{width:100%;height:40px;border:none;border-bottom:2px solid var(--line);background:transparent;padding:0 6px 5px;outline:none;color:#5a6674;font-size:33px;transform:scaleY(.42);transform-origin:left bottom;letter-spacing:.02em}
    .field input::placeholder{color:#8593a2}

    .actions{display:flex;flex-direction:column;gap:12px;padding-top:4px}
    .actions .btn{height:30px;border:none;border-radius:999px;background:var(--gold);color:#fff;display:flex;align-items:center;justify-content:center;text-decoration:none;cursor:pointer;font-size:34px;font-weight:700;transform:scaleY(.42);transform-origin:center;letter-spacing:.2em;transition:filter .18s}
    .actions .btn:hover{filter:brightness(.95);text-decoration:none}

    @media (max-width:860px){
      .ring-deco{width:1080px;height:1080px}
      .card{width:min(760px,calc(100vw - 20px))}
      .dot-tr{right:74px}
      .dot-br{right:240px}
    }

    @media (max-width:760px){
      .bg-gold,.ring-deco,.dot{display:none}
      .card{grid-template-columns:1fr;min-height:auto;width:min(430px,calc(100vw - 20px))}
      .card-left{display:none}
      .card-right{padding:26px 22px}
      .logo-text .sub{letter-spacing:.15em}
    }
  </style>
</head>
<body>
<div class="page">
  <div class="bg-gold"></div>
  <div class="ring-deco"><img src="${ctx}/static/img/ring-deco.svg" alt=""/></div>
  <div class="dot dot-tr"><img src="${ctx}/static/img/dot-gold-lg.svg" alt=""/></div>
  <div class="dot dot-br"><img src="${ctx}/static/img/dot-gold-sm.svg" alt=""/></div>
  <div class="card">
    <div class="card-left">
      <img class="card-deco-lines" src="${ctx}/static/img/deco-lines.svg" alt=""/>
      <div class="card-deco-dot card-deco-dot-1"></div>
      <div class="card-deco-dot card-deco-dot-2"></div>
      <div class="card-hero">
        <img src="${ctx}/static/img/register1.png" alt=""/>
      </div>
      <div class="card-badge">MBTI职业性格测试系统</div>
    </div>
    <div class="card-right">
      <div class="logo" aria-label="MBTI职业性格测试系统">
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
        <div class="err"><c:out value="${error}"/></div>
      </c:if>
      <form method="post" action="<c:url value='/login'/>" class="form" autocomplete="off">
        <div class="field">
          <div class="field-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5Zm0 2c-4.42 0-8 2.01-8 4.5A1.5 1.5 0 0 0 5.5 20h13a1.5 1.5 0 0 0 1.5-1.5C20 16.01 16.42 14 12 14Z"/></svg>
          </div>
          <input name="login" type="text" value="<c:out value='${login}'/>" placeholder="请输入你的手机号" autocomplete="username" required/>
        </div>
        <div class="field">
          <div class="field-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M17 9h-1V7a4 4 0 0 0-8 0v2H7a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2Zm-6 7.73V18a1 1 0 0 0 2 0v-1.27a2 2 0 1 0-2 0ZM10 9V7a2 2 0 0 1 4 0v2Z"/></svg>
          </div>
          <input name="passwd" type="password" placeholder="请输入你的密码" autocomplete="current-password" required/>
        </div>
        <div class="actions">
          <button class="btn" type="submit">登录</button>
          <a class="btn" href="${ctx}/register">注册</a>
        </div>
      </form>
    </div>
  </div>
</div>
</body>
</html>
