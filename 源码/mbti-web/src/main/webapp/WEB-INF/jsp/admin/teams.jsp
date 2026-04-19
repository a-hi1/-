<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="批次管理" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/teams/create">添加批次</a>
</div>

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
      <th style="width:70px; text-align:center;">ID</th>
      <th>批次名称</th>
      <th style="width:160px; text-align:center;">开始时间</th>
      <th style="width:90px; text-align:center;">状态</th>
      <th style="min-width:160px; text-align:right; padding-right:24px; white-space:nowrap;">操作</th>
    </tr>
    <c:forEach items="${teams}" var="t">
      <tr>
        <td style="text-align:center; color:#666;">${t.id}</td>
        <td style="font-weight:500;"><c:out value="${t.name}"/></td>
        <td style="text-align:center; color:#777;"><c:out value="${t.beginYear}"/></td>
        <td style="text-align:center;">
          <c:choose>
            <c:when test="${t.status == 1}"><span class="tag tag-green">启用</span></c:when>
            <c:when test="${t.status == 2}"><span class="tag tag-gray">停用</span></c:when>
            <c:otherwise><span class="muted">未设置</span></c:otherwise>
          </c:choose>
        </td>
        <td style="text-align:right; padding-right:24px; white-space:nowrap;">
          <div style="display:flex; justify-content:flex-end; align-items:center; gap:8px;">
            <a class="btn btn-light btn-sm" href="${pageContext.request.contextPath}/admin/teams/view?id=${t.id}" style="padding:5px 12px; margin:0;">查看</a>
            <a class="btn btn-light btn-sm" href="${pageContext.request.contextPath}/admin/teams/personnel?teamId=${t.id}" style="padding:5px 12px; margin:0;">查看参测人员</a>
            <a class="btn btn-view btn-sm" href="${pageContext.request.contextPath}/admin/teams/edit?id=${t.id}" style="padding:5px 12px; margin:0;">编辑</a>
            <a class="btn btn-light btn-sm" href="${pageContext.request.contextPath}/admin/personnel?teamId=${t.id}" style="padding:5px 12px; margin:0;">添加参测人员</a>
            <a class="btn btn-light btn-sm" href="${pageContext.request.contextPath}/admin/teams/personnel/import?teamId=${t.id}" style="padding:5px 12px; margin:0;">导入参测人员</a>
            <a class="btn btn-danger btn-sm" href="${pageContext.request.contextPath}/admin/teams/delete?id=${t.id}" style="padding:5px 12px; margin:0; white-space:nowrap;">删除</a>
          </div>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
