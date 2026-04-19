<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="批次详情" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px; display:flex; justify-content:space-between; align-items:center;">
  <div style="font-weight:700; color:#334155;">测试批次详情</div>
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams">返回列表</a>
</div>

<div class="card" style="padding:0; overflow:hidden;">
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
          <c:when test="${team.status == 1}">启用</c:when>
          <c:when test="${team.status == 2}">禁用</c:when>
          <c:otherwise>未设置</c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
