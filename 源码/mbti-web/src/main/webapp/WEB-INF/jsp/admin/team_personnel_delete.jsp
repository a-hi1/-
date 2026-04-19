<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="删除参测人员" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams/personnel?teamId=${teamId}">返回列表</a>
</div>

<div class="card" style="padding:0;overflow:hidden;">
  <table class="table" style="margin:0;">
    <tr><th style="width:220px;">姓名</th><td><c:out value="${p.name}"/></td></tr>
    <tr><th>手机号</th><td><c:out value="${p.phone}"/></td></tr>
    <tr><th>性别</th><td><c:out value="${p.gender}"/></td></tr>
    <tr><th>出生日期</th><td><c:out value="${p.birthdate}"/></td></tr>
    <tr>
      <th></th>
      <td>
        <form method="post" action="${pageContext.request.contextPath}/admin/teams/personnel/delete" style="margin:0;">
          <input type="hidden" name="id" value="${p.id}" />
          <input type="hidden" name="teamId" value="${teamId}" />
          <button type="submit" class="btn btn-danger" onclick="return confirm('确认删除该参测人员？')">确认删除</button>
        </form>
      </td>
    </tr>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
