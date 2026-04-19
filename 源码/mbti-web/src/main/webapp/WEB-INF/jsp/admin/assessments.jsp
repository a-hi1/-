<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="测评管理" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:14px">
  <div style="display:flex;justify-content:flex-end;margin-bottom:12px">
    <form method="post" action="${pageContext.request.contextPath}/admin/seed-demo" style="display:inline">
      <button type="submit" class="btn btn-warn" onclick="return confirm('将一键创建示例测评（含维度+题目）。若已存在则直接跳转到题库页。继续？')">一键创建示例测评+题库</button>
    </form>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/admin/assessments" class="grid" style="align-items:end">
    <input type="hidden" name="action" value="save" />
    <input type="hidden" name="id" value="${edit.id}" />
    <div class="col-6">
      <label>标题</label>
      <input name="title" value="${edit.title}" placeholder="例如：mbti职业性格测试20题版" required />
    </div>
    <div class="col-3">
      <label>费用</label>
      <input name="cost" type="number" step="0.01" value="${empty edit ? '0' : edit.cost}" required />
    </div>
    <div class="col-3">
      <label>状态</label>
      <select name="status">
        <option value="1" <c:if test="${edit.status == 1}">selected</c:if>>启用</option>
        <option value="2" <c:if test="${empty edit || edit.status == 2}">selected</c:if>>停用</option>
      </select>
    </div>
    <div class="col-12" style="display:flex;justify-content:flex-end;gap:10px;flex-wrap:wrap">
      <c:choose>
        <c:when test="${not empty edit}">
          <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/assessments">取消</a>
          <button type="submit" class="btn btn-primary">保存修改</button>
        </c:when>
        <c:otherwise>
          <button type="submit" class="btn btn-primary">新增测评</button>
        </c:otherwise>
      </c:choose>
    </div>
  </form>
</div>

<div class="card">
<table class="table">
  <tr>
    <th style="width:60px; text-align:center;">ID</th>
    <th>标题</th>
    <th>费用</th>
    <th style="width:80px; text-align:center;">状态</th>
    <th style="width:160px; text-align:center;">题库</th>
    <th style="min-width:160px; text-align:right; padding-right:24px; white-space:nowrap;">操作</th>
  </tr>
  <c:forEach items="${assessments}" var="a">
    <tr>
      <td style="text-align:center; color:#666;">${a.id}</td>
      <td style="font-weight:500;">${a.title}</td>
      <td style="color:#555;">${a.cost}</td>
      <td style="text-align:center;">
        <c:choose>
          <c:when test="${a.status == 1}"><span class="tag tag-green">启用</span></c:when>
          <c:otherwise><span class="tag tag-gray">停用</span></c:otherwise>
        </c:choose>
      </td>
      <td style="text-align:center;">
        <a class="btn btn-light btn-sm" href="<c:url value='/admin/questions' />?assessmentId=${a.id}" style="padding:5px 12px; margin:0;">管理题库</a>
      </td>
      <td style="text-align:right; padding-right:24px; white-space:nowrap;">
        <div style="display:flex; justify-content:flex-end; align-items:center; gap:8px;">
          <a class="btn btn-view btn-sm" href="${pageContext.request.contextPath}/admin/assessments?id=${a.id}" style="padding:5px 12px; margin:0;">编辑</a>
          <form method="post" action="${pageContext.request.contextPath}/admin/assessments" style="margin:0;">
            <input type="hidden" name="action" value="delete" />
            <input type="hidden" name="id" value="${a.id}" />
            <button type="submit" class="btn btn-danger btn-sm" style="padding:5px 12px; margin:0; white-space:nowrap;" onclick="return confirm('确认删除该测评？')">删除</button>
          </form>
        </div>
      </td>
    </tr>
  </c:forEach>
</table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
