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
      <th style="width:70px">ID</th>
      <th style="width:220px">维度</th>
      <th>描述</th>
      <th style="width:220px">操作</th>
    </tr>
    <c:forEach items="${dimensions}" var="d">
      <tr>
        <td>${d.id}</td>
        <td><c:out value="${d.title}"/></td>
        <td><c:out value="${d.depict}"/></td>
        <td>
          <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/dimensions?assessmentId=${assessmentId}&id=${d.id}">编辑</a>
          <form method="post" action="${pageContext.request.contextPath}/admin/dimensions" style="display:inline-block;margin-left:8px">
            <input type="hidden" name="assessmentId" value="${assessmentId}" />
            <input type="hidden" name="action" value="delete" />
            <input type="hidden" name="id" value="${d.id}" />
            <button type="submit" class="btn btn-warn" onclick="return confirm('确认删除该维度？')">删除</button>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>

  <c:if test="${empty dimensions}">
    <div class="muted" style="padding:12px 0">当前考核类型暂无维度</div>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
