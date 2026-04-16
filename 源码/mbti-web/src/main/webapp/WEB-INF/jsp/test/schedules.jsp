<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="选择测评场次" />
<%@ include file="../_layout_top.jspf" %>

<div class="card">
<c:if test="${not empty warning}">
  <div class="alert alert-error" style="margin-bottom:10px">${warning}</div>
</c:if>
<div class="muted" style="margin-bottom:10px">仅“进行中”的场次可以开始测评</div>
<table class="table">
  <tr>
    <th>场次ID</th>
    <th>测评</th>
    <th>开始时间</th>
    <th>结束时间</th>
    <th>题数</th>
    <th>状态</th>
    <th>操作</th>
  </tr>
  <c:forEach items="${schedules}" var="s">
    <tr>
      <td>${s.id}</td>
      <td>${s.assessmentTitle}</td>
      <td>${s.beginDate}</td>
      <td>${s.endDate}</td>
      <td>${s.questionNumber}</td>
      <td>
        <c:choose>
          <c:when test="${s.status == 1}"><span class="tag tag-gray">未开始</span></c:when>
          <c:when test="${s.status == 2}"><span class="tag tag-green">进行中</span></c:when>
          <c:when test="${s.status == 3}"><span class="tag tag-gray">已结束</span></c:when>
          <c:otherwise>${s.status}</c:otherwise>
        </c:choose>
      </td>
      <td>
        <c:choose>
          <c:when test="${s.status == 2}">
            <a class="btn btn-primary" href="<c:url value='/test/take' />?scheduleId=${s.id}">开始测评</a>
          </c:when>
          <c:otherwise>
            <span class="muted">不可开始</span>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </c:forEach>
</table>

<c:if test="${empty schedules}">
  <div class="muted" style="padding:10px 0">当前批次暂无可选场次</div>
</c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
