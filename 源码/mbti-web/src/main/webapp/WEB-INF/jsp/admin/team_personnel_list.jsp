<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="批次内参测人员查看" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams">返回列表</a>
  <c:if test="${not empty teamId}">
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/teams/personnel/add?teamId=${teamId}">添加参测人员</a>
    <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams/personnel/import?teamId=${teamId}">导入参测人员</a>
  </c:if>
</div>

<div class="card" style="margin-bottom:12px;">
  <form method="get" action="${pageContext.request.contextPath}/admin/teams/personnel" class="grid" style="align-items:end;">
    <div class="col-3">
      <label>批次</label>
      <select name="teamId">
        <option value="">全部批次</option>
        <c:forEach items="${teams}" var="t">
          <option value="${t.id}" <c:if test="${not empty teamId && teamId == t.id}">selected</c:if>><c:out value="${t.name}"/></option>
        </c:forEach>
      </select>
    </div>
    <div class="col-2">
      <label>姓名</label>
      <input name="name" value="${name}" />
    </div>
    <div class="col-2">
      <label>手机号</label>
      <input name="phone" value="${phone}" />
    </div>
    <div class="col-2" style="display:flex;justify-content:flex-start;gap:8px;">
      <button class="btn btn-primary" type="submit">查询</button>
      <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams/personnel">重置</a>
    </div>
  </form>
</div>

<div class="card" style="padding:0;overflow:hidden;">
  <table class="table" style="margin:0;">
    <tr>
      <th style="width:70px;">序号</th>
      <th>手机号</th>
      <th>姓名</th>
      <th style="width:80px;">性别</th>
      <th style="width:140px;">出生日期</th>
      <th style="width:80px;">状态</th>
      <th style="width:220px;">操作</th>
    </tr>
    <c:forEach items="${personnel}" var="p" varStatus="st">
      <tr>
        <td>${st.index + 1}</td>
        <td><c:out value="${p.phone}"/></td>
        <td><c:out value="${p.name}"/></td>
        <td>
          <c:choose>
            <c:when test="${p.gender == 'M'}">男</c:when>
            <c:when test="${p.gender == 'F'}">女</c:when>
            <c:otherwise>-</c:otherwise>
          </c:choose>
        </td>
        <td><c:out value="${p.birthdate}"/></td>
        <td>
          <c:choose>
            <c:when test="${p.status == 1}">正常</c:when>
            <c:otherwise>禁用</c:otherwise>
          </c:choose>
        </td>
        <td>
          <a href="${pageContext.request.contextPath}/admin/teams/personnel/view?id=${p.id}&teamId=${p.teamId}">查看</a>
          <a href="${pageContext.request.contextPath}/admin/teams/personnel/edit?id=${p.id}&teamId=${p.teamId}">修改</a>
          <a href="${pageContext.request.contextPath}/admin/teams/personnel/delete?id=${p.id}&teamId=${p.teamId}">删除</a>
        </td>
      </tr>
    </c:forEach>
  </table>

  <c:if test="${empty personnel}">
    <div class="muted" style="padding:18px; text-align:center;">暂无参测人员</div>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
