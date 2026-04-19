<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="测试安排详情" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/schedules?teamId=${s.teamId}">返回列表</a>
</div>

<div class="card">
  <table class="table">
    <tr>
      <th style="width:180px">批次名称</th>
      <td><c:out value="${s.teamName}"/></td>
    </tr>
    <tr>
      <th>科目名称</th>
      <td><c:out value="${s.assessmentTitle}"/></td>
    </tr>
    <tr>
      <th>开始日期</th>
      <td><c:out value="${s.beginDate}"/></td>
    </tr>
    <tr>
      <th>结束日期</th>
      <td><c:out value="${s.endDate}"/></td>
    </tr>
    <tr>
      <th>考试时长</th>
      <td><c:out value="${s.duration}"/></td>
    </tr>
    <tr>
      <th>试题数量</th>
      <td><c:out value="${s.questionNumber}"/></td>
    </tr>
    <tr>
      <th>创建时间</th>
      <td><c:out value="${s.createDate}"/></td>
    </tr>
    <tr>
      <th>状态</th>
      <td>
        <c:choose>
          <c:when test="${s.status == 1}">未开始</c:when>
          <c:when test="${s.status == 2}">考试中</c:when>
          <c:when test="${s.status == 3}">已结束</c:when>
          <c:otherwise><c:out value="${s.status}"/></c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
