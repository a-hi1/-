<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="参测人员详情" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams/personnel?teamId=${teamId}">返回列表</a>
</div>

<div class="card" style="padding:0;overflow:hidden;">
  <table class="table" style="margin:0;">
    <tr>
      <th style="width:220px;">手机号</th>
      <td><c:out value="${p.phone}"/></td>
    </tr>
    <tr>
      <th>姓名</th>
      <td><c:out value="${p.name}"/></td>
    </tr>
    <tr>
      <th>性别</th>
      <td>
        <c:choose>
          <c:when test="${p.gender == 'M'}">男</c:when>
          <c:when test="${p.gender == 'F'}">女</c:when>
          <c:otherwise>-</c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <th>出生日期</th>
      <td><c:out value="${p.birthdate}"/></td>
    </tr>
    <tr>
      <th>状态</th>
      <td>
        <c:choose>
          <c:when test="${p.status == 1}">正常</c:when>
          <c:otherwise>禁用</c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <th>所属批次</th>
      <td><c:out value="${p.teamName}"/></td>
    </tr>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
