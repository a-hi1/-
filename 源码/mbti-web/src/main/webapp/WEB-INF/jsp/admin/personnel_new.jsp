<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="参测人员管理" />
<%@ include file="../_layout_top.jspf" %>

<style>
  .search-bar { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
  .search-bar form { display: flex; gap: 10px; align-items: center; flex: 1; }
  .action-buttons { display: flex; gap: 6px; }
  .form-details[open] summary ~ * { animation: slideDown 0.2s ease-in-out; }
  @keyframes slideDown { from { opacity: 0; transform: translateY(-5px); } to { opacity: 1; transform: translateY(0); } }
  .table td { vertical-align: middle; }
</style>

<c:if test="${not empty sessionScope.flash}">
  <div class="alert" style="margin-bottom:14px">
    <c:out value="${sessionScope.flash}"/>
  </div>
  <c:remove var="flash" scope="session" />
</c:if>

<!-- Top Action Bar with Search and Add Toggle -->
<div class="card" style="margin-bottom:14px; padding: 12px 16px;">
  <div class="search-bar">
    <form method="get" action="${pageContext.request.contextPath}/admin/personnel">
      <div style="width: 200px;">
        <select name="teamId">
          <option value="">全部批次</option>
          <c:forEach items="${teams}" var="t">
            <option value="${t.id}" <c:if test="${not empty teamId && teamId == t.id}">selected</c:if>><c:out value="${t.name}"/></option>
          </c:forEach>
        </select>
      </div>
      <div style="width: 250px;">
        <input name="q" value="${q}" placeholder="登录名/姓名/手机号" />
      </div>
      <button class="btn btn-primary" type="submit">查询</button>
      <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/personnel">重置</a>
    </form>
    
    <div style="margin-left:auto; display:flex; gap:10px">
      <!-- Add New Toggle -->
       <button class="btn btn-primary" onclick="window.location.href='#addForm';document.getElementById('addDetails').open=true;">+ 新增参测人员</button>
    </div>
  </div>
</div>

<!-- Add/Edit Form Section (Collapsible) -->
<details id="addDetails" class="card form-details" style="margin-bottom:14px" <c:if test="${not empty edit}">open</c:if>>
  <summary style="cursor:pointer;font-weight:700;padding-bottom:10px;outline:none;list-style:none" id="addForm">
    <span style="font-size:16px"><c:choose><c:when test="${not empty edit}">编辑参测人员</c:when><c:otherwise>新增参测人员</c:otherwise></c:choose></span>
    <span class="muted" style="font-weight:400;font-size:14px;margin-left:10px">（点击展开/收起）</span>
  </summary>
  
  <div class="muted" style="margin-bottom:10px">默认密码：123456</div>
  <form method="post" action="${pageContext.request.contextPath}/admin/personnel" class="grid" style="align-items:end">
    <input type="hidden" name="action" value="${empty edit ? 'create' : 'update'}" />
    <input type="hidden" name="id" value="${edit.id}" />
    <div class="col-3">
      <label>登录名 <span style="color:red">*</span></label>
      <input name="login" value="${edit.login}" placeholder="手机号/学号" required />
    </div>
    <div class="col-3">
      <label>姓名 <span style="color:red">*</span></label>
      <input name="name" value="${edit.name}" required />
    </div>
    <div class="col-3">
      <label>密码</label>
      <c:choose>
        <c:when test="${empty edit}">
          <input name="passwd" value="123456" required />
        </c:when>
        <c:otherwise>
          <input name="passwd" value="******" disabled placeholder="如需重置请用列表按钮" />
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
    <div class="col-12" style="display:flex;justify-content:flex-end;gap:10px;margin-top:10px;border-top:1px solid #eee;padding-top:10px">
      <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/personnel">取消 / 清空</a>
      <button class="btn btn-primary" type="submit">保存提交</button>
    </div>
  </form>
  
  <!-- Batch Import -->
  <div style="margin-top:20px;border-top:1px dashed #ddd;padding-top:14px">
    <div style="font-weight:700;margin-bottom:8px">批量导入</div>
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
        <div class="muted" style="margin-top:8px;font-size:12px">
          格式: login,name,phone,gender,birthdate,email,teamId<br>
          (支持 .txt/.csv 文件上传或文本粘贴)
        </div>
      </div>
      <div class="col-8">
        <div style="display:flex;gap:10px;margin-bottom:8px">
             <input type="file" name="file" accept=".txt,.csv" style="flex:1" />
        </div>
        <textarea name="lines" rows="3" style="width:100%;padding:8px;border:1px solid #d1d5db;border-radius:6px" placeholder="粘贴内容..."></textarea>
        <div style="margin-top:8px;text-align:right">
          <button class="btn btn-light" type="submit" onclick="return confirm('确认批量导入？')">开始导入</button>
        </div>
      </div>
    </form>
  </div>
</details>

<div class="card">
  <table class="table">
    <thead>
      <tr>
        <th style="width:60px">ID</th>
        <th style="width:140px">登录名</th>
        <th style="width:120px">真实姓名</th>
        <th style="width:100px">用户类型</th>
        <th style="width:100px">用户状态</th>
        <th style="width:160px">最后登录</th>
        <th>操作</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach items="${personnel}" var="p">
        <tr>
          <td>${p.id}</td>
          <td><c:out value="${p.login}"/></td>
          <td><c:out value="${p.name}"/></td>
          <td>参测人员</td>
          <td>
            <c:choose>
                <c:when test="${p.status == 1}"><span class="tag tag-green">启用</span></c:when>
                <c:otherwise><span class="tag tag-gray">禁用</span></c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:choose>
                <c:when test="${not empty p.lastLogin}">
                    <c:out value="${p.lastLogin}"/>
                </c:when>
                <c:otherwise><span class="muted">-</span></c:otherwise>
            </c:choose>
          </td>
          <td class="action-buttons">
            <a class="btn btn-view" href="${pageContext.request.contextPath}/admin/personnel?id=${p.id}&view=true">查看</a>
            <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/personnel?id=${p.id}">编辑</a>
            
            <form method="post" action="${pageContext.request.contextPath}/admin/personnel" style="margin:0">
              <input type="hidden" name="id" value="${p.id}" />
              <input type="hidden" name="action" value="resetPwd" />
              <button class="btn btn-warn" type="submit" onclick="return confirm('确认重置密码为 123456？')">重置密码</button>
            </form>
            
            <form method="post" action="${pageContext.request.contextPath}/admin/personnel" style="margin:0">
              <input type="hidden" name="id" value="${p.id}" />
              <input type="hidden" name="action" value="delete" />
              <button class="btn btn-danger" type="submit" onclick="return confirm('确认删除该参测人员？')">删除</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <c:if test="${empty personnel}">
    <div style="text-align:center;padding:40px;color:#9ca3af">暂无数据</div>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
