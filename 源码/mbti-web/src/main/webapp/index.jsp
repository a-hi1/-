<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>MBTI职业性格测试系统</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/app.css" />
</head>
<body>
<div class="content" style="max-width:680px;margin:30px auto">
  <div class="card">
    <h2 style="margin:0 0 6px">MBTI职业性格测试系统</h2>
    <p class="muted" style="margin:0 0 14px">JavaWeb（Servlet/JSP/JDBC/MySQL）</p>
    <div style="display:flex;gap:10px;flex-wrap:wrap">
      <a class="btn" href="<c:url value='/login' />">进入登录</a>
      <a class="btn btn-light" href="<c:url value='/register' />">参测人员注册</a>
    </div>
  </div>
</div>
</body>
</html>
