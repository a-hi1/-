<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="添加批次" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams">返回列表</a>
</div>

<c:if test="${not empty error}">
  <div class="card" style="margin-bottom:12px;border-left:4px solid #ef4444;">
    <div style="color:#b91c1c;font-weight:700;">保存失败</div>
    <div style="margin-top:6px;">${error}</div>
  </div>
</c:if>

<div class="card" style="padding:0;overflow:hidden;">
  <form method="post" action="${pageContext.request.contextPath}/admin/teams/save">
    <table class="table" style="margin:0;">
      <tr>
        <th style="width:220px;">批次名称</th>
        <td><input name="name" value="${name}" required style="max-width:300px;" /></td>
      </tr>
      <tr>
        <th>批次创建时间</th>
        <td><input name="beginYear" type="date" value="${beginYear}" required style="max-width:220px;" /></td>
      </tr>
      <tr>
        <th>批次状态</th>
        <td>
          <label style="margin-right:14px;">
            <input type="radio" name="status" value="2" <c:if test="${status == '2'}">checked</c:if> /> 禁用
          </label>
          <label>
            <input type="radio" name="status" value="1" <c:if test="${empty status || status == '1'}">checked</c:if> /> 正常
          </label>
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
