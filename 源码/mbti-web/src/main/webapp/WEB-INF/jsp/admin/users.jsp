<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="用户管理" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px">
  <form method="post" action="${pageContext.request.contextPath}/admin/users" class="grid" style="align-items:end">
    <input type="hidden" name="action" value="save" />
    <input type="hidden" name="id" value="${edit.id}" />
    <div class="col-12 muted" style="margin-bottom:4px">
      <c:choose>
        <c:when test="${not empty edit}">编辑用户（留空密码表示不修改）</c:when>
        <c:otherwise>新增用户（默认密码 123456）</c:otherwise>
      </c:choose>
    </div>
    <div class="col-3">
      <label>登录名</label>
      <input name="login" value="${edit.login}" placeholder="例如：admin/teacher/手机号" required />
    </div>
    <div class="col-3">
      <label>真实姓名</label>
      <input name="name" value="${edit.name}" placeholder="请输入姓名" required />
    </div>
    <div class="col-3">
      <label>密码</label>
      <input name="passwd" type="text" placeholder="${empty edit ? '不填默认123456' : '留空不修改'}" />
    </div>
    <div class="col-2">
      <label>类型</label>
      <select name="type">
        <option value="1" <c:if test="${edit.type == 1}">selected</c:if>>管理员</option>
        <option value="2" <c:if test="${edit.type == 2}">selected</c:if>>题库管理员</option>
        <option value="3" <c:if test="${edit.type == 3}">selected</c:if>>老师</option>
        <option value="4" <c:if test="${edit.type == 4}">selected</c:if>>参测人员</option>
      </select>
    </div>
    <div class="col-1">
      <label>状态</label>
      <select name="status">
        <option value="1" <c:if test="${empty edit || edit.status == 1}">selected</c:if>>启用</option>
        <option value="2" <c:if test="${edit.status == 2}">selected</c:if>>禁用</option>
      </select>
    </div>
    <div class="col-12" style="display:flex;gap:10px;justify-content:flex-end">
      <c:if test="${not empty edit}">
        <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/users">取消编辑</a>
      </c:if>
      <button type="submit" class="btn btn-primary">保存</button>
    </div>
  </form>
</div>

<div class="card">
  <table class="table">
    <tr>
      <th style="width:70px">ID</th>
      <th style="width:180px">登录名</th>
      <th style="width:220px">真实姓名</th>
      <th style="width:120px">用户类型</th>
      <th style="width:100px">用户状态</th>
      <th>最后登录</th>
      <th style="width:120px">操作</th>
    </tr>
    <c:forEach items="${users}" var="u">
      <tr>
        <td>${u.id}</td>
        <td><c:out value="${u.login}"/></td>
        <td><c:out value="${u.name}"/></td>
        <td>
          <c:choose>
            <c:when test="${u.type == 1}">管理员</c:when>
            <c:when test="${u.type == 2}">题库管理员</c:when>
            <c:when test="${u.type == 3}">老师</c:when>
            <c:when test="${u.type == 4}">参测人员</c:when>
            <c:otherwise>${u.type}</c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${u.status == 1}"><span class="tag tag-green">启用</span></c:when>
            <c:otherwise><span class="tag tag-gray">禁用</span></c:otherwise>
          </c:choose>
        </td>
        <td>${u.lastLogin}</td>
        <td>
          <div style="display:flex;gap:8px;flex-wrap:wrap">
            <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/users?id=${u.id}">查看</a>
            <a class="btn" href="${pageContext.request.contextPath}/admin/users?editId=${u.id}">编辑</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:inline">
              <input type="hidden" name="action" value="resetPwd" />
              <input type="hidden" name="id" value="${u.id}" />
              <button type="submit" class="btn btn-warn" onclick="return confirm('确认重置密码为 123456？')">重置密码</button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:inline">
              <input type="hidden" name="action" value="delete" />
              <input type="hidden" name="id" value="${u.id}" />
              <button type="submit" class="btn" onclick="return confirm('确认删除该用户？')">删除</button>
            </form>
          </div>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
