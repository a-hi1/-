<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="用户详情" />
<%@ include file="../_layout_top.jspf" %>

<div class="page-actions">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/users">返回列表</a>
</div>

<div class="card">
  <c:if test="${empty u}">
    <div class="alert alert-error">用户不存在或已被删除</div>
  </c:if>
  <c:if test="${not empty u}">
    <table class="kv">
      <tr>
        <td>登录名称</td>
        <td><c:out value="${u.login}"/></td>
      </tr>
      <tr>
        <td>真实姓名</td>
        <td><c:out value="${u.name}"/></td>
      </tr>
      <tr>
        <td>用户类型</td>
        <td>
          <c:choose>
            <c:when test="${u.type == 1}">管理员</c:when>
            <c:when test="${u.type == 2}">题库管理员</c:when>
            <c:when test="${u.type == 3}">老师</c:when>
            <c:when test="${u.type == 4}">参测人员</c:when>
            <c:otherwise>${u.type}</c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td>用户状态</td>
        <td>
          <c:choose>
            <c:when test="${u.status == 1}"><span class="tag tag-green">启用</span></c:when>
            <c:otherwise><span class="tag tag-gray">禁用</span></c:otherwise>
          </c:choose>
        </td>
      </tr>
    </table>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
