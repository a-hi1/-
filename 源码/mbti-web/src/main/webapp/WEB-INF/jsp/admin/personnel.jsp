<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="参测人员管理" />
<%@ include file="../_layout_top.jspf" %>

<c:if test="${not empty sessionScope.flash}">
  <div class="alert" style="margin-bottom:14px">
    <c:out value="${sessionScope.flash}"/>
  </div>
  <c:remove var="flash" scope="session" />
</c:if>

<div class="card" style="margin-bottom:14px">
  <div class="muted" style="margin-bottom:10px">默认密码：123456（可在此页面重置）</div>
  <form method="post" action="${pageContext.request.contextPath}/admin/personnel" class="grid" style="align-items:end">
    <input type="hidden" name="action" value="${empty edit ? 'create' : 'update'}" />
    <input type="hidden" name="id" value="${edit.id}" />
    <div class="col-3">
      <label>登录名</label>
      <input name="login" value="${edit.login}" placeholder="手机号/学号" required />
    </div>
    <div class="col-3">
      <label>姓名</label>
      <input name="name" value="${edit.name}" required />
    </div>
    <div class="col-3">
      <label>密码</label>
      <c:choose>
        <c:when test="${empty edit}">
          <input name="passwd" value="123456" required />
        </c:when>
        <c:otherwise>
          <input name="passwd" value="******" disabled />
        </c:otherwise>
      </c:choose>
    </div>
    <div class="col-3">
      <label>批次</label>
      <select name="teamId">
        <option value="">未分配</option>
        <c:forEach items="${teams}" var="t">
          <option value="${t.id}" <c:if test="${not empty edit && edit.teamId == t.id}">selected</c:if>><c:out value="${t.name}"/></option>
        </c:forEach>
      </select>
    </div>
    <div class="col-3">
      <label>手机号</label>
      <input name="phone" value="${edit.phone}" />
    </div>
    <div class="col-2">
      <label>性别</label>
      <select name="gender">
        <option value="">未知</option>
        <option value="M" <c:if test="${not empty edit && edit.gender == 'M'}">selected</c:if>>男</option>
        <option value="F" <c:if test="${not empty edit && edit.gender == 'F'}">selected</c:if>>女</option>
      </select>
    </div>
    <div class="col-3">
      <label>生日</label>
      <input name="birthdate" type="date" value="${edit.birthdate}" />
    </div>
    <div class="col-4">
      <label>邮箱</label>
      <input name="email" value="${edit.email}" />
    </div>
    <div class="col-12" style="display:flex;justify-content:flex-end;gap:10px">
      <c:choose>
        <c:when test="${not empty edit}">
          <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/personnel">取消</a>
          <button class="btn btn-primary" type="submit">保存修改</button>
        </c:when>
        <c:otherwise>
          <button class="btn btn-primary" type="submit">添加参测人员</button>
        </c:otherwise>
      </c:choose>
    </div>
  </form>
</div>

<div class="card" style="margin-bottom:14px">
  <form method="get" action="${pageContext.request.contextPath}/admin/personnel" class="grid" style="align-items:end">
    <div class="col-4">
      <label>批次筛选</label>
      <select name="teamId">
        <option value="">全部批次</option>
        <c:forEach items="${teams}" var="t">
          <option value="${t.id}" <c:if test="${not empty teamId && teamId == t.id}">selected</c:if>><c:out value="${t.name}"/></option>
        </c:forEach>
      </select>
    </div>
    <div class="col-4">
      <label>关键字</label>
      <input name="q" value="${q}" placeholder="登录名/姓名/手机号" />
    </div>
    <div class="col-4" style="display:flex;justify-content:flex-end;gap:10px">
      <button class="btn btn-primary" type="submit">筛选</button>
      <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/personnel">清空</a>
    </div>
  </form>

  <div style="height:12px"></div>

  <div class="muted" style="margin-bottom:8px">批量导入：每行一条，支持逗号/制表符分隔</div>
  <form method="post" action="${pageContext.request.contextPath}/admin/personnel" class="grid" enctype="multipart/form-data">
    <input type="hidden" name="action" value="batchImport" />
    <div class="col-4">
      <label>默认批次</label>
      <select name="defaultTeamId">
        <option value="">未分配</option>
        <c:forEach items="${teams}" var="t">
          <option value="${t.id}" <c:if test="${not empty teamId && teamId == t.id}">selected</c:if>><c:out value="${t.name}"/></option>
        </c:forEach>
      </select>
      <div class="muted" style="margin-top:8px">字段：login,name,phone,gender,birthdate,email,teamId(可选)</div>
      <div class="muted" style="margin-top:6px">示例：20230001,张三,13800000000,M,2000-01-01,zhangsan@qq.com,1</div>
      <div style="height:10px"></div>
      <label>上传文件（可选）</label>
      <input type="file" name="file" accept=".txt,.csv" />
      <div class="muted" style="margin-top:6px">上传后将优先使用文件内容导入；未上传则使用右侧文本框。</div>
    </div>
    <div class="col-8">
      <label>导入内容</label>
      <textarea name="lines" rows="7" style="width:100%;padding:10px 12px;border:1px solid #d1d5db;border-radius:10px" placeholder="每行一条：login,name,phone,gender,birthdate,email,teamId(可选)"></textarea>
      <div style="display:flex;justify-content:flex-end;margin-top:10px">
        <button class="btn btn-primary" type="submit" onclick="return confirm('确认开始批量导入？重复登录名将更新信息。')">开始导入</button>
      </div>
    </div>
  </form>
</div>

<div class="card">
  <table class="table">
    <tr>
      <th style="width:70px">ID</th>
      <th style="width:160px">登录名</th>
      <th style="width:160px">姓名</th>
      <th style="width:140px">批次</th>
      <th style="width:140px">手机号</th>
      <th style="width:80px">性别</th>
      <th style="width:120px">生日</th>
      <th>邮箱</th>
      <th style="width:260px">操作</th>
    </tr>
    <c:forEach items="${personnel}" var="p">
      <tr>
        <td>${p.id}</td>
        <td><c:out value="${p.login}"/></td>
        <td><c:out value="${p.name}"/></td>
        <td><c:out value="${p.teamName}"/></td>
        <td><c:out value="${p.phone}"/></td>
        <td>
          <c:choose>
            <c:when test="${p.gender == 'M'}">男</c:when>
            <c:when test="${p.gender == 'F'}">女</c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td><c:out value="${p.birthdate}"/></td>
        <td><c:out value="${p.email}"/></td>
        <td>
          <div class="btn-group">
            <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/personnel?id=${p.id}">编辑</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/personnel" style="display:inline">
              <input type="hidden" name="id" value="${p.id}" />
              <input type="hidden" name="action" value="resetPwd" />
              <button class="btn btn-light" type="submit" onclick="return confirm('确认重置密码为 123456？')">重置密码</button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/admin/personnel" style="display:inline">
              <input type="hidden" name="id" value="${p.id}" />
              <input type="hidden" name="action" value="delete" />
              <button class="btn btn-warn" type="submit" onclick="return confirm('确认删除该参测人员？')">删除</button>
            </form>
          </div>
        </td>
      </tr>
    </c:forEach>
  </table>

  <c:if test="${empty personnel}">
    <div class="muted" style="padding:12px 0">暂无参测人员</div>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
