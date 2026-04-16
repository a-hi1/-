<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="批次管理" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:14px">
  <form method="post" action="${pageContext.request.contextPath}/admin/teams" class="grid" style="align-items:end">
    <input type="hidden" name="action" value="save" />
    <input type="hidden" name="id" value="${edit.id}" />
    <div class="col-6">
      <label>批次名称</label>
      <input name="name" value="${edit.name}" placeholder="例如：2026年第一批" required />
    </div>
    <div class="col-3">
      <label>开始时间</label>
      <input name="beginYear" type="date" value="${edit.beginYear}" />
    </div>
    <div class="col-3">
      <label>状态</label>
      <select name="status">
        <option value="">未设置</option>
        <option value="1" <c:if test="${edit.status == 1}">selected</c:if>>启用</option>
        <option value="2" <c:if test="${edit.status == 2}">selected</c:if>>停用</option>
      </select>
    </div>
    <div class="col-12" style="display:flex;justify-content:flex-end;gap:10px">
      <c:choose>
        <c:when test="${not empty edit}">
          <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams">取消</a>
          <button type="submit" class="btn btn-primary">保存修改</button>
        </c:when>
        <c:otherwise>
          <button type="submit" class="btn btn-primary">新增批次</button>
        </c:otherwise>
      </c:choose>
    </div>
  </form>
</div>

<div class="card">
  <table class="table">
    <tr>
      <th style="width:70px">ID</th>
      <th>批次名称</th>
      <th style="width:160px">开始时间</th>
      <th style="width:90px">状态</th>
      <th style="width:220px">操作</th>
    </tr>
    <c:forEach items="${teams}" var="t">
      <tr>
        <td>${t.id}</td>
        <td><c:out value="${t.name}"/></td>
        <td><c:out value="${t.beginYear}"/></td>
        <td>
          <c:choose>
            <c:when test="${t.status == 1}"><span class="tag tag-green">启用</span></c:when>
            <c:when test="${t.status == 2}"><span class="tag tag-gray">停用</span></c:when>
            <c:otherwise><span class="muted">未设置</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams?id=${t.id}">编辑</a>
          <form method="post" action="${pageContext.request.contextPath}/admin/teams" style="display:inline-block;margin-left:8px">
            <input type="hidden" name="action" value="delete" />
            <input type="hidden" name="id" value="${t.id}" />
            <button type="submit" class="btn btn-warn" onclick="return confirm('确认删除该批次？')">删除</button>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
