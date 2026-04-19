<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="参测人员信息修改" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams/personnel?teamId=${teamId}">返回列表</a>
</div>

<c:if test="${not empty error}">
  <div class="card" style="margin-bottom:12px;border-left:4px solid #ef4444;">
    <div style="color:#b91c1c;font-weight:700;">保存失败</div>
    <div style="margin-top:6px;">${error}</div>
  </div>
</c:if>

<div class="card" style="padding:0;overflow:hidden;">
  <form method="post" action="${pageContext.request.contextPath}/admin/teams/personnel/edit">
    <input type="hidden" name="id" value="${p.id}" />
    <input type="hidden" name="teamId" value="${teamId}" />
    <table class="table" style="margin:0;">
      <tr>
        <th style="width:220px;">姓名</th>
        <td><input name="name" value="${p.name}" required style="max-width:260px;" /></td>
      </tr>
      <tr>
        <th>手机号</th>
        <td><input name="phone" value="${p.phone}" required style="max-width:260px;" /></td>
      </tr>
      <tr>
        <th>性别</th>
        <td>
          <label style="margin-right:14px;"><input type="radio" name="gender" value="M" <c:if test="${p.gender == 'M' || empty p.gender}">checked</c:if> /> 男</label>
          <label><input type="radio" name="gender" value="F" <c:if test="${p.gender == 'F'}">checked</c:if> /> 女</label>
        </td>
      </tr>
      <tr>
        <th>出生日期</th>
        <td><input name="birthdate" value="${p.birthdate}" placeholder="日期格式：yyyy-MM-dd" style="max-width:260px;" /></td>
      </tr>
      <tr>
        <th>状态</th>
        <td>
          <label style="margin-right:14px;"><input type="radio" name="status" value="1" <c:if test="${p.status == 1 || empty p.status}">checked</c:if> /> 正常</label>
          <label><input type="radio" name="status" value="2" <c:if test="${p.status == 2}">checked</c:if> /> 禁用</label>
        </td>
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
