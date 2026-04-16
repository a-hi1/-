<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="测评记录" />
<%@ include file="../_layout_top.jspf" %>

<div class="card">
  <table class="table">
    <tr>
      <th>记录ID</th>
      <th>测评</th>
      <th>开始</th>
      <th>结束</th>
      <th>结果</th>
      <th>操作</th>
    </tr>
    <c:forEach items="${records}" var="r">
      <tr>
        <td>${r.id}</td>
        <td>${r.assessmentTitle}</td>
        <td>${r.beginTime}</td>
        <td>${r.endTime}</td>
        <td>
          <c:choose>
            <c:when test="${not empty r.result}"><c:out value="${r.result}"/></c:when>
            <c:when test="${empty r.endTime}"><span class="muted">未完成</span></c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${not empty r.result && not empty r.endTime}">
              <a class="btn btn-light" href="${pageContext.request.contextPath}/test/result?examId=${r.id}">查看</a>
            </c:when>
            <c:otherwise>
              <span class="muted">无</span>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:forEach>
  </table>
  <c:if test="${empty records}">
    <div class="muted" style="padding:10px 0">暂无记录</div>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
