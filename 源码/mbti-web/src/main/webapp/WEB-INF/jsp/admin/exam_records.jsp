<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="评测记录（全员）" />
<%@ include file="../_layout_top.jspf" %>

<div class="card" style="margin-bottom:14px">
  <c:url var="exportUrl" value="/admin/exam-records">
    <c:param name="export" value="csv" />
    <c:param name="teamId" value="${teamId}" />
    <c:param name="q" value="${q}" />
  </c:url>
  <form method="get" action="${pageContext.request.contextPath}/admin/exam-records" class="grid" style="align-items:end">
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
      <a class="btn btn-light" href="${exportUrl}">导出CSV</a>
      <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/exam-records">清空</a>
    </div>
  </form>
</div>

<c:if test="${not empty stats}">
  <div class="card" style="margin-bottom:14px">
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-k">总人数</div>
        <div class="stat-v">${stats.total}</div>
      </div>
      <div class="stat-card">
        <div class="stat-k">未测</div>
        <div class="stat-v">${stats.notStarted}</div>
      </div>
      <div class="stat-card">
        <div class="stat-k">进行中</div>
        <div class="stat-v">${stats.inProgress}</div>
      </div>
      <div class="stat-card">
        <div class="stat-k">已完成</div>
        <div class="stat-v">
          ${stats.completed}
          <small>完成率 <fmt:formatNumber value="${stats.completionRate}" type="percent" maxFractionDigits="1"/></small>
        </div>
      </div>

      <div class="stat-split"></div>

      <div class="stat-half">
        <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:8px">
          <div style="font-weight:900">MBTI 类型分布</div>
          <div style="color:#6b7280;font-weight:800;font-size:12px">仅统计已完成</div>
        </div>
        <c:choose>
          <c:when test="${empty stats.typeCounts}">
            <div class="muted">暂无数据</div>
          </c:when>
          <c:otherwise>
            <table class="bar-table">
              <thead>
              <tr>
                <th style="width:90px">类型</th>
                <th>占比</th>
                <th style="width:90px">人数</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${stats.typeCounts}" var="t">
                <c:set var="pct" value="${stats.completed > 0 ? (t.count * 100.0 / stats.completed) : 0}"/>
                <fmt:formatNumber value="${pct}" maxFractionDigits="0" var="pctInt"/>
                <tr>
                  <td style="font-weight:900">${t.type}</td>
                  <td>
                    <div class="bar"><i data-w="${pctInt}"></i></div>
                    <div style="margin-top:6px;color:#6b7280;font-size:12px">
                      <fmt:formatNumber value="${pct/100.0}" type="percent" maxFractionDigits="1"/>
                    </div>
                  </td>
                  <td style="font-weight:900">${t.count}</td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </c:otherwise>
        </c:choose>
      </div>

      <div class="stat-half">
        <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:8px">
          <div style="font-weight:900">测评类型分布</div>
          <div style="color:#6b7280;font-weight:800;font-size:12px">当前筛选范围</div>
        </div>
        <c:choose>
          <c:when test="${empty stats.assessmentCounts}">
            <div class="muted">暂无数据</div>
          </c:when>
          <c:otherwise>
            <table class="bar-table">
              <thead>
              <tr>
                <th style="width:180px">测评</th>
                <th>占比</th>
                <th style="width:90px">人数</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${stats.assessmentCounts}" var="a">
                <c:set var="pct2" value="${stats.total > 0 ? (a.count * 100.0 / stats.total) : 0}"/>
                <fmt:formatNumber value="${pct2}" maxFractionDigits="0" var="pct2Int"/>
                <tr>
                  <td style="font-weight:900"><c:out value="${a.type}"/></td>
                  <td>
                    <div class="bar bar-blue"><i data-w="${pct2Int}"></i></div>
                    <div style="margin-top:6px;color:#6b7280;font-size:12px">
                      <fmt:formatNumber value="${pct2/100.0}" type="percent" maxFractionDigits="1"/>
                    </div>
                  </td>
                  <td style="font-weight:900">${a.count}</td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</c:if>

<div class="card">
  <table class="table">
    <tr>
      <th style="width:80px">人员ID</th>
      <th style="width:160px">登录名</th>
      <th style="width:160px">真实姓名</th>
      <th style="width:160px">批次</th>
      <th style="width:120px">测评状态</th>
      <th style="width:140px">最近测评时间</th>
      <th style="width:180px">测评类型</th>
      <th style="width:120px">结果</th>
      <th style="width:120px">操作</th>
    </tr>

    <c:forEach items="${rows}" var="r">
      <tr>
        <td>${r.personnelId}</td>
        <td><c:out value="${r.login}"/></td>
        <td><c:out value="${r.name}"/></td>
        <td><c:out value="${r.teamName}"/></td>
        <td>
          <c:choose>
            <c:when test="${empty r.examId}"><span class="tag tag-gray">未测评</span></c:when>
            <c:when test="${empty r.endTime}"><span class="tag tag-gray">进行中</span></c:when>
            <c:otherwise><span class="tag tag-green">已完成</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${not empty r.endTime}">${r.endTime}</c:when>
            <c:when test="${not empty r.beginTime}">${r.beginTime}</c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${not empty r.assessmentTitle}"><c:out value="${r.assessmentTitle}"/></c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${not empty r.result}"><c:out value="${r.result}"/></c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <c:choose>
            <c:when test="${not empty r.examId && not empty r.endTime && not empty r.result}">
              <a class="btn btn-view" href="${pageContext.request.contextPath}/test/result?examId=${r.examId}">查看</a>
            </c:when>
            <c:when test="${not empty r.examId && empty r.endTime}">
              <span class="muted">未完成</span>
            </c:when>
            <c:otherwise>
              <span class="muted">无</span>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:forEach>
  </table>

  <c:if test="${empty rows}">
    <div class="muted" style="padding:12px 0">暂无记录</div>
  </c:if>
</div>

  <script>
    (function () {
      var els = document.querySelectorAll('.bar > i[data-w]');
      for (var i = 0; i < els.length; i++) {
        var w = els[i].getAttribute('data-w');
        var n = parseFloat(w);
        if (!isFinite(n)) n = 0;
        if (n < 0) n = 0;
        if (n > 100) n = 100;
        els[i].style.width = n + '%';
      }
    })();
  </script>

<%@ include file="../_layout_bottom.jspf" %>
