<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="选择测评场次" />
<%@ include file="../_layout_top.jspf" %>

<div class="card">
<c:if test="${not empty warning}">
  <div class="alert alert-error" style="margin-bottom:10px">${warning}</div>
</c:if>
<div class="muted" style="margin-bottom:10px">仅满足开始条件的场次显示“开始测试”按钮（JSTL控制）</div>
<table class="table">
  <tr>
    <th>序号</th>
    <th>考核类型</th>
    <th>开始时间</th>
    <th>结束时间</th>
    <th>考试时长</th>
    <th>结果</th>
    <th>操作</th>
  </tr>
  <c:forEach items="${rows}" var="r" varStatus="st">
    <tr>
      <td>${st.index + 1}</td>
      <td>${r.assessmentTitle}</td>
      <td>${fn:replace(r.beginDate, 'T', ' ')}</td>
      <td>${fn:replace(r.endDate, 'T', ' ')}</td>
      <td>${r.duration}</td>
      <td>${r.resultText}</td>
      <td>
        <c:choose>
          <c:when test="${r.canStart}">
            <a class="btn btn-primary" href="<c:url value='/test/take' />?scheduleId=${r.scheduleId}">
              <c:choose>
                <c:when test="${r.inProgress}">继续测试</c:when>
                <c:otherwise>开始测试</c:otherwise>
              </c:choose>
            </a>
          </c:when>
          <c:otherwise>
            <span class="muted">--</span>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </c:forEach>
</table>

<c:if test="${empty rows}">
  <div class="muted" style="padding:10px 0">当前批次暂无可选场次</div>
</c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
