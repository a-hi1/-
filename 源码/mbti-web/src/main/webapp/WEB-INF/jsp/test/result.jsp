<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="测评结果" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:12px">
  <c:choose>
    <c:when test="${pending}">
      <h3 style="margin:0 0 6px">暂无结果</h3>
      <div class="muted" style="line-height:1.8">${pendingMessage}</div>
    </c:when>
    <c:otherwise>
      <div style="display:flex; gap:20px; align-items:flex-start;">
        <div style="flex:1;">
          <h3 style="margin:0 0 6px">你的类型：${resultText}</h3>
        </div>
        <div style="flex-shrink:0; width:200px; text-align:center;">
          <img src="${pageContext.request.contextPath}/static/img/${resultText}.jpg" alt="${resultText}" style="width:100%; max-width:200px; border-radius:12px; box-shadow:0 4px 12px rgba(0,0,0,0.1);" onerror="this.style.display='none'" />
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <c:if test="${!pending && not empty mbtiProfile}">
    <div style="margin-top:10px">
      <div class="muted" style="margin-bottom:6px">${mbtiProfile.title}</div>
      <div style="line-height:1.8">${mbtiProfile.summary}</div>

      <c:if test="${not empty mbtiProfile.strengths}">
        <div style="margin-top:10px;font-weight:700">典型优势</div>
        <ul style="margin:6px 0 0 18px">
          <c:forEach items="${mbtiProfile.strengths}" var="s">
            <li style="margin:4px 0">${s}</li>
          </c:forEach>
        </ul>
      </c:if>

      <c:if test="${not empty mbtiProfile.tips}">
        <div style="margin-top:10px;font-weight:700">成长建议</div>
        <ul style="margin:6px 0 0 18px">
          <c:forEach items="${mbtiProfile.tips}" var="t">
            <li style="margin:4px 0">${t}</li>
          </c:forEach>
        </ul>
      </c:if>

      <c:if test="${not empty mbtiProfile.careers}">
        <div style="margin-top:10px;font-weight:700">职业方向（参考）</div>
        <div class="muted" style="margin-top:6px;line-height:1.8">
          <c:forEach items="${mbtiProfile.careers}" var="c" varStatus="st">
            ${c}<c:if test="${!st.last}">、</c:if>
          </c:forEach>
        </div>
      </c:if>
    </div>
  </c:if>
  <div class="muted">结果已保存，可在“测评记录”中查看</div>
</div>

<c:if test="${mappingWarning}">
  <div class="alert alert-error" style="margin-bottom:12px">
    维度明细存在未统计的情况（题数为0或无法匹配维度）。通常是题库未完善或维度/题目关联有误；请管理员检查：
    1）该测评是否已创建4个维度（E/I、S/N、T/F、J/P）；2）题目是否都关联到当前测评的维度；3）每题是否有2个选项。
  </div>
</c:if>

<c:if test="${not empty dimensionScores}">
  <div class="card">
  <h3 style="margin:0 0 10px">维度明细</h3>
  <table class="table">
    <tr>
      <th>维度</th>
      <th>得分</th>
      <th>题数</th>
      <th>判定</th>
    </tr>
    <c:forEach items="${dimensionScores}" var="ds">
      <tr>
        <td>${ds.dimension.title}</td>
        <td>${ds.score}</td>
        <td>${ds.total}</td>
        <td>${ds.letter}</td>
      </tr>
    </c:forEach>
  </table>
  </div>
</c:if>

<%@ include file="../_layout_bottom.jspf" %>
