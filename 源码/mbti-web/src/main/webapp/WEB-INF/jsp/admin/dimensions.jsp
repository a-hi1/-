<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="性格维度管理" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:14px">
  <form method="get" action="${pageContext.request.contextPath}/admin/dimensions" class="grid" style="align-items:end">
    <div class="col-6">
      <label>选择考核类型</label>
      <select name="assessmentId" onchange="this.form.submit()">
        <c:forEach items="${assessments}" var="a">
          <option value="${a.id}" <c:if test="${a.id == assessmentId}">selected</c:if>><c:out value="${a.title}"/></option>
        </c:forEach>
      </select>
    </div>
  </form>
</div>

<div class="card" style="margin-bottom:14px">
  <c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px; width:100%;"><c:out value="${error}"/></div>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/admin/dimensions" class="grid" style="align-items:end">
    <input type="hidden" name="action" value="save" />
    <input type="hidden" name="assessmentId" value="${assessmentId}" />
    <input type="hidden" name="id" value="${edit.id}" />
    <div class="col-4">
      <label>维度名称</label>
      <input name="title" value="${edit.title}" placeholder="例如：外向/内向" required />
    </div>
    <div class="col-8">
      <label>描述</label>
      <input name="depict" value="${edit.depict}" placeholder="用于解释该维度" />
    </div>
    <div class="col-12" style="display:flex;justify-content:flex-end;gap:10px">
      <c:choose>
        <c:when test="${not empty edit}">
          <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/dimensions?assessmentId=${assessmentId}">取消</a>
          <button type="submit" class="btn btn-primary">保存修改</button>
        </c:when>
        <c:otherwise>
          <button type="submit" class="btn btn-primary">新增维度</button>
        </c:otherwise>
      </c:choose>
    </div>
  </form>
</div>

<div class="card">
  <table class="table">
    <tr>
      <th style="width:70px; text-align:center;">ID</th>
      <th style="width:220px">维度</th>
      <th>描述</th>
      <th style="min-width:160px; text-align:right; padding-right:24px; white-space:nowrap;">操作</th>
    </tr>
    <c:forEach items="${dimensions}" var="d">
      <tr>
        <td style="text-align:center; color:#666;">${d.id}</td>
        <td style="font-weight:500;"><c:out value="${d.title}"/></td>
        <td style="color:#555;"><c:out value="${d.depict}"/></td>
        <td style="text-align:right; padding-right:24px; white-space:nowrap;">
          <div style="display:flex; justify-content:flex-end; align-items:center; gap:8px;">
            <a class="btn btn-view btn-sm" href="${pageContext.request.contextPath}/admin/dimensions?assessmentId=${assessmentId}&id=${d.id}" style="padding:5px 12px; margin:0;">编辑</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/dimensions" style="margin:0;">
              <input type="hidden" name="assessmentId" value="${assessmentId}" />
              <input type="hidden" name="action" value="delete" />
              <input type="hidden" name="id" value="${d.id}" />
              <button type="submit" class="btn btn-danger btn-sm" style="padding:5px 12px; margin:0; white-space:nowrap;" onclick="return confirm('确认删除该维度？')">删除</button>
            </form>
          </div>
        </td>
      </tr>
    </c:forEach>
  </table>

  <c:if test="${empty dimensions}">
    <div class="muted" style="padding:12px 0">当前考核类型暂无维度</div>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
