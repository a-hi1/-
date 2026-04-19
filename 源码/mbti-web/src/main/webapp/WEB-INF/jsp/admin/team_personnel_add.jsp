<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="参测人员添加" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams/personnel?teamId=${team.id}">返回列表</a>
</div>

<c:if test="${not empty error}">
  <div class="card" style="margin-bottom:12px;border-left:4px solid #ef4444;">
    <div style="color:#b91c1c;font-weight:700;">保存失败</div>
    <div style="margin-top:6px;">${error}</div>
  </div>
</c:if>

<div class="card" style="padding:0;overflow:hidden;">
  <form method="post" action="${pageContext.request.contextPath}/admin/teams/personnel/add">
    <input type="hidden" name="teamId" value="${team.id}" />
    <table class="table" style="margin:0;">
      <tr>
        <th style="width:220px;">姓名</th>
        <td><input name="name" value="${name}" required style="max-width:260px;" /></td>
      </tr>
      <tr>
        <th>考核类型</th>
        <td>${team.id}</td>
      </tr>
      <tr>
        <th>手机号</th>
        <td><input name="phone" value="${phone}" required style="max-width:260px;" /></td>
      </tr>
      <tr>
        <th>性别</th>
        <td>
          <label style="margin-right:14px;"><input type="radio" name="gender" value="M" <c:if test="${empty gender || gender == 'M'}">checked</c:if> /> 男</label>
          <label><input type="radio" name="gender" value="F" <c:if test="${gender == 'F'}">checked</c:if> /> 女</label>
        </td>
      </tr>
      <tr>
        <th>生日</th>
        <td><input name="birthdate" type="date" value="${birthdate}" style="max-width:180px;" /></td>
      </tr>
      <tr>
        <th></th>
        <td>
          <button type="submit" class="btn btn-primary">保存</button>
          <button type="reset" class="btn btn-light" style="margin-left:8px;">重置</button>
        </td>
      </tr>
    </table>
  </form>
</div>

<%@ include file="../_layout_bottom.jspf" %>
