<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="参测人员批量导入" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px;display:flex;justify-content:flex-start;align-items:center;gap:10px;">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/teams">返回列表</a>
</div>

<c:if test="${not empty error}">
  <div class="card" style="margin-bottom:12px;border-left:4px solid #ef4444;">
    <div style="color:#b91c1c;font-weight:700;">导入失败</div>
    <div style="margin-top:6px;">${error}</div>
  </div>
</c:if>

<c:if test="${ok != null}">
  <div class="card" style="margin-bottom:12px;border-left:4px solid #10b981;">
    <div style="color:#065f46;font-weight:700;">导入完成</div>
    <div style="margin-top:6px;">成功 ${ok} 条，已存在跳过 ${skipped} 条，失败 ${fail} 条</div>
    <c:if test="${not empty detail}">
      <pre style="white-space:pre-wrap;background:#f8fafc;border:1px solid #e2e8f0;padding:10px;margin-top:8px;max-height:220px;overflow:auto;"><c:out value="${detail}"/></pre>
    </c:if>
  </div>
</c:if>

<div class="card" style="padding:0;overflow:hidden;">
  <form method="post" action="${pageContext.request.contextPath}/admin/teams/personnel/import" enctype="multipart/form-data">
    <input type="hidden" name="teamId" value="${team.id}" />
    <table class="table" style="margin:0;">
      <tr>
        <th style="width:220px;">批次</th>
        <td><c:out value="${team.name}"/></td>
      </tr>
      <tr>
        <th>学生文件格式demo</th>
        <td class="muted">手机号,姓名,性别,生日</td>
      </tr>
      <tr>
        <th>上传文件</th>
        <td><input type="file" name="file" accept=".txt,.csv" /></td>
      </tr>
      <tr>
        <th>或粘贴内容</th>
        <td>
          <textarea name="lines" rows="6" style="width:100%;max-width:560px;" placeholder="13021565608, 张三, F, 1990-12-21&#10;15552722615, 李四, M, 1991-12-21&#10;15564600881, 王五, F, 1990-03-21"></textarea>
        </td>
      </tr>
      <tr>
        <th></th>
        <td><button type="submit" class="btn btn-primary">导入</button></td>
      </tr>
    </table>
  </form>
</div>

<%@ include file="../_layout_bottom.jspf" %>
