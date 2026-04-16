<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.mbti.web.dao.ScheduleDao" %>
<c:set var="pageTitle" value="场次管理" />
<%@ include file="../_layout_top.jspf" %>

<c:if test="${not empty error}">
  <div class="card" style="margin-bottom:12px;border-left:4px solid #ef4444;">
    <div style="color:#b91c1c;font-weight:700;">创建失败</div>
    <div style="margin-top:6px;">${error}</div>
  </div>
</c:if>

<div class="grid">
  <div class="col-12">
    <div class="card">
      <form method="post" action="${pageContext.request.contextPath}/admin/schedules">
        <input type="hidden" name="action" value="save" />
        <input type="hidden" name="id" value="${edit.id}" />
        <div class="muted" style="margin-bottom:10px;">
          提示：题目属于“测评”，批次只用于分组参测人员；场次是“批次 + 测评”的绑定，参测人员在自己的批次下选择场次进行答题。
        </div>
        <div class="grid">
          <div class="col-6">
            <div class="form-row">
              <label>批次</label>
              <select name="teamId" required>
                <c:forEach items="${teams}" var="t">
                  <option value="${t.id}" <c:if test="${not empty edit && t.id == edit.teamId}">selected</c:if>>${t.name}</option>
                </c:forEach>
              </select>
            </div>
          </div>
          <div class="col-6">
            <div class="form-row">
              <label>测评类型</label>
              <select name="assessmentId" required>
                <c:forEach items="${assessments}" var="a">
                  <option value="${a.id}" <c:if test="${not empty edit && a.id == edit.assessmentId}">selected</c:if>>${a.title}</option>
                </c:forEach>
              </select>
            </div>
          </div>

          <div class="col-6">
            <div class="form-row">
              <label>开始时间</label>
              <input type="datetime-local" name="beginDate" value="${not empty edit ? ScheduleDao.toDatetimeLocal(edit.beginDate) : ''}" required />
            </div>
          </div>
          <div class="col-6">
            <div class="form-row">
              <label>结束时间</label>
              <input type="datetime-local" name="endDate" value="${not empty edit ? ScheduleDao.toDatetimeLocal(edit.endDate) : ''}" required />
            </div>
          </div>

          <div class="col-6">
            <div class="form-row">
              <label>时长（分钟）</label>
              <input type="number" name="duration" value="${not empty edit ? edit.duration : 60}" min="1" required />
            </div>
          </div>
          <div class="col-6">
            <div class="form-row">
              <label>题数</label>
              <input type="number" name="questionNumber" value="${not empty edit ? edit.questionNumber : 16}" min="1" required />
            </div>
          </div>

          <div class="col-6">
            <div class="form-row">
              <label>状态</label>
              <select name="status">
                <option value="1" <c:if test="${empty edit || edit.status == 1}">selected</c:if>>未开始</option>
                <option value="2" <c:if test="${not empty edit && edit.status == 2}">selected</c:if>>进行中</option>
                <option value="3" <c:if test="${not empty edit && edit.status == 3}">selected</c:if>>已结束</option>
              </select>
            </div>
          </div>
          <div class="col-6">
            <div class="form-row" style="margin-top:29px">
              <div style="display:flex;gap:10px;justify-content:flex-end">
                <c:if test="${not empty edit}">
                  <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/schedules">取消编辑</a>
                </c:if>
                <button class="btn btn-primary" type="submit">保存</button>
              </div>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>

  <div class="col-12">
    <div class="card">
      <div class="grid" style="margin-bottom:10px">
        <div class="col-6">
          <form method="get" action="${pageContext.request.contextPath}/admin/schedules">
            <div class="form-row">
              <label>按批次筛选</label>
              <select name="teamId" onchange="this.form.submit()">
                <option value="">全部批次</option>
                <c:forEach items="${teams}" var="t">
                  <option value="${t.id}" <c:if test="${not empty teamId && t.id == teamId}">selected</c:if>>${t.name}</option>
                </c:forEach>
              </select>
            </div>
          </form>
        </div>
      </div>
      <table class="table">
        <tr>
          <th>ID</th>
          <th>测评</th>
          <th>批次</th>
          <th>开始</th>
          <th>结束</th>
          <th>题数</th>
          <th>时长</th>
          <th>状态</th>
          <th style="width:120px">操作</th>
        </tr>
        <c:forEach items="${schedules}" var="s">
          <tr>
            <td>${s.id}</td>
            <td>${s.assessmentTitle}</td>
            <td>${s.teamName}</td>
            <td>${s.beginDate}</td>
            <td>${s.endDate}</td>
            <td>${s.questionNumber}</td>
            <td>${s.duration}</td>
            <td>
              <c:choose>
                <c:when test="${s.status == 1}">未开始</c:when>
                <c:when test="${s.status == 2}">进行中</c:when>
                <c:when test="${s.status == 3}">已结束</c:when>
                <c:otherwise>${s.status}</c:otherwise>
              </c:choose>
            </td>
            <td>
              <div style="display:flex;gap:8px;flex-wrap:wrap">
                <a class="btn" href="${pageContext.request.contextPath}/admin/schedules?id=${s.id}">编辑</a>
                <form method="post" action="${pageContext.request.contextPath}/admin/schedules" style="display:inline">
                  <input type="hidden" name="action" value="delete" />
                  <input type="hidden" name="id" value="${s.id}" />
                  <button class="btn" type="submit" onclick="return confirm('确认删除该场次？')">删除</button>
                </form>
              </div>
            </td>
          </tr>
        </c:forEach>
      </table>
      <c:if test="${empty schedules}">
        <div class="muted" style="padding:10px 0">暂无场次</div>
      </c:if>
    </div>
  </div>
</div>

<%@ include file="../_layout_bottom.jspf" %>
