<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="题目查看" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px">
  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/questions?assessmentId=${assessmentId}&status=${status}&dimensionId=${dimensionId}">返回列表</a>
</div>

<c:if test="${not empty error}">
  <div class="card" style="margin-bottom:12px;color:#b42318;">
    <c:out value="${error}"/>
  </div>
</c:if>

<div class="card" style="margin-bottom:12px">
  <div style="font-weight:600;margin-bottom:10px">题目信息</div>
  <table class="table">
    <tr>
      <th style="width:140px">题目内容</th>
      <td><c:out value="${q.title}"/></td>
    </tr>
    <tr>
      <th>答案提示</th>
      <td>
        <c:choose>
          <c:when test="${not empty q.hint}"><c:out value="${q.hint}"/></c:when>
          <c:otherwise>-</c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</div>

<div class="card" style="margin-bottom:12px">
  <div style="font-weight:600;margin-bottom:10px">选项信息</div>
  <table class="table">
    <tr>
      <th style="width:120px">选项序号</th>
      <th>选项内容</th>
      <th style="width:120px">是否正确</th>
    </tr>
    <c:forEach items="${q.choices}" var="c" varStatus="st">
      <tr>
        <td>选项${st.index + 1}</td>
        <td><c:out value="${c.title}"/></td>
        <td>
          <c:choose>
            <c:when test="${c.checked == 1}">是</c:when>
            <c:otherwise></c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>

<div class="card">
  <div style="font-weight:600;margin-bottom:10px">性格维度信息</div>
  <table class="table">
    <tr>
      <th style="width:140px">性格维度名称</th>
      <th>性格维度说明</th>
    </tr>
    <tr>
      <td><c:out value="${dimension.title}"/></td>
      <td><c:out value="${dimension.depict}"/></td>
    </tr>
    <tr>
      <td colspan="2" style="text-align:center;">
        <form method="post" action="${pageContext.request.contextPath}/admin/questions" style="display:inline-block;margin:0;">
          <input type="hidden" name="action" value="delete" />
          <input type="hidden" name="id" value="${q.id}" />
          <input type="hidden" name="assessmentId" value="${assessmentId}" />
          <input type="hidden" name="status" value="${status}" />
          <input type="hidden" name="dimensionId" value="${dimensionId}" />
          <input type="hidden" name="view" value="1" />
          <button class="btn btn-danger btn-sm" type="submit" onclick="return confirm('确认删除该题目？')">确认删除</button>
        </form>
      </td>
    </tr>
  </table>
</div>

<%@ include file="../_layout_bottom.jspf" %>
