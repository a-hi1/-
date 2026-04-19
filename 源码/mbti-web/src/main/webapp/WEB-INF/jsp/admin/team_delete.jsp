<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="删除批次" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams">返回列表</a>
</div>

<c:if test="${not empty error}">
  <div class="card" style="margin-bottom:12px;border-left:4px solid #ef4444;">
    <div style="color:#b91c1c;font-weight:700;">删除失败</div>
    <div style="margin-top:6px;">${error}</div>
  </div>
</c:if>

<div class="card" style="padding:0;overflow:hidden;">
  <table class="table" style="margin:0;">
    <tr>
      <th style="width:220px;">批次名称</th>
      <td><c:out value="${team.name}"/></td>
    </tr>
    <tr>
      <th>批次创建时间</th>
      <td><c:out value="${team.beginYear}"/></td>
    </tr>
    <tr>
      <th>批次状态</th>
      <td>
        <c:choose>
          <c:when test="${team.status == 1}">正常</c:when>
          <c:when test="${team.status == 2}">禁用</c:when>
          <c:otherwise>未设置</c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <th></th>
      <td>
        <form method="post" action="${pageContext.request.contextPath}/admin/teams/delete" style="margin:0;">
          <input type="hidden" name="id" value="${team.id}" />
          <button type="submit" class="btn btn-danger" onclick="return confirm('确认删除?')">确认删除?</button>
        </form>
      </td>
    </tr>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
