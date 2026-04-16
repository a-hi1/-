<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="更改密码" />
<%@ include file="_layout_top.jspf" %>

<div class="card" style="max-width:520px">
  <c:if test="${not empty error}">
    <div class="alert alert-error" style="margin-bottom:12px"><c:out value="${error}"/></div>
  </c:if>
  <c:if test="${not empty ok}">
    <div class="alert" style="margin-bottom:12px"><c:out value="${ok}"/></div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/app/password">
    <div class="form-row">
      <label>原密码</label>
      <input type="password" name="oldPwd" required />
    </div>
    <div class="form-row">
      <label>新密码</label>
      <input type="password" name="newPwd" required />
    </div>
    <div class="form-row">
      <label>确认新密码</label>
      <input type="password" name="newPwd2" required />
    </div>
    <div style="display:flex;justify-content:flex-end;gap:10px">
      <button type="submit" class="btn btn-primary">保存</button>
    </div>
  </form>
</div>

<%@ include file="_layout_bottom.jspf" %>
