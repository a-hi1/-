<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.mbti.web.dao.ScheduleDao" %>
<c:set var="pageTitle" value="场次管理" />
<%@ include file="../_layout_top.jspf" %>

<c:if test="${not empty error}">
  <div class="card" style="margin-bottom:12px;border-left:4px solid #ef4444;">
    <div style="color:#b91c1c;font-weight:700;">操作失败</div>
    <div style="margin-top:6px;">${error}</div>
  </div>
</c:if>

<div class="grid">
  <div class="col-12">
    <div class="card">
      <form method="post" action="${pageContext.request.contextPath}/admin/schedules" class="grid" style="align-items:end">
        <input type="hidden" name="action" value="save" />
        <input type="hidden" name="id" value="${edit.id}" />
        <div class="col-12 muted" style="margin-bottom:10px;">
          提示：题目属于“测评”，批次只用于分组参测人员；场次是“批次 + 测评”的绑定，参测人员在自己的批次下选择场次进行答题。
        </div>
        <div class="col-6">
          <label>批次</label>
          <select name="teamId" required>
            <c:forEach items="${teams}" var="t">
              <option value="${t.id}" <c:if test="${not empty edit && t.id == edit.teamId}">selected</c:if>>${t.name}</option>
            </c:forEach>
          </select>
        </div>
        <div class="col-6">
          <label>测评类型</label>
          <select name="assessmentId" required>
            <c:forEach items="${assessments}" var="a">
              <option value="${a.id}" <c:if test="${not empty edit && a.id == edit.assessmentId}">selected</c:if>>${a.title}</option>
            </c:forEach>
          </select>
        </div>
        <div class="col-6">
          <label>开始时间</label>
          <input type="datetime-local" name="beginDate" value="${not empty edit ? ScheduleDao.toDatetimeLocal(edit.beginDate) : ''}" required />
        </div>
        <div class="col-6">
          <label>结束时间</label>
          <input type="datetime-local" name="endDate" value="${not empty edit ? ScheduleDao.toDatetimeLocal(edit.endDate) : ''}" required />
        </div>
        <div class="col-6">
          <label>时长（分钟）</label>
          <input type="number" name="duration" value="${not empty edit ? edit.duration : 60}" min="1" required />
        </div>
        <div class="col-6">
          <label>创建时间</label>
          <input type="date" name="createDate" value="${not empty edit ? ScheduleDao.toDate(edit.createDate) : ''}" required />
        </div>
        <div class="col-3">
          <label>题数</label>
          <input type="number" name="questionNumber" value="${not empty edit ? edit.questionNumber : 16}" min="4" step="4" placeholder="试题数量必须为4的倍数" required />
        </div>
        <div class="col-3">
          <label>状态</label>
          <select name="status">
            <option value="1" <c:if test="${empty edit || edit.status == 1}">selected</c:if>>未开始</option>
            <option value="2" <c:if test="${not empty edit && edit.status == 2}">selected</c:if>>进行中</option>
            <option value="3" <c:if test="${not empty edit && edit.status == 3}">selected</c:if>>已结束</option>
          </select>
        </div>
        <div class="col-12" style="display:flex;gap:10px;justify-content:flex-end">
          <c:if test="${not empty edit}">
            <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/schedules">取消编辑</a>
          </c:if>
          <button class="btn btn-primary" type="submit">保存</button>
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
          <th style="width:60px; text-align:center;">序号</th>
          <th>考核批次</th>
          <th>考核类型</th>
          <th style="width:140px; text-align:center;">开始日期</th>
          <th style="width:140px; text-align:center;">结束日期</th>
          <th style="width:80px; text-align:center;">测试时长</th>
          <th style="width:80px; text-align:center;">试题数量</th>
          <th style="width:120px; text-align:center;">创建时间</th>
          <th style="width:80px; text-align:center;">状态</th>
          <th style="min-width:160px; text-align:right; padding-right:24px; white-space:nowrap;">操作</th>
        </tr>
        <c:forEach items="${schedules}" var="s" varStatus="st">
          <tr>
            <td style="text-align:center; color:#666;">${st.index + 1}</td>
            <td>${s.teamName}</td>
            <td style="font-weight:500;">${s.assessmentTitle}</td>
            <td style="text-align:center; color:#777;">${s.beginDate}</td>
            <td style="text-align:center; color:#777;">${s.endDate}</td>
            <td style="text-align:center;">${s.duration}</td>
            <td style="text-align:center;">${s.questionNumber}</td>
            <td style="text-align:center; color:#777;">${s.createDate}</td>
            <td style="text-align:center;">
              <c:choose>
                <c:when test="${s.status == 1}"><span class="tag tag-gray">未开始</span></c:when>
                <c:when test="${s.status == 2}"><span class="tag tag-green">进行中</span></c:when>
                <c:when test="${s.status == 3}"><span class="tag tag-blue">已结束</span></c:when>
                <c:otherwise>${s.status}</c:otherwise>
              </c:choose>
            </td>
            <td style="text-align:right; padding-right:24px; white-space:nowrap;">
              <div style="display:flex; justify-content:flex-end; align-items:center; gap:8px;">
                <a class="btn btn-light btn-sm" href="${pageContext.request.contextPath}/admin/schedules?view=1&id=${s.id}" style="padding:5px 12px; margin:0;">查看</a>
                <a class="btn btn-view btn-sm" href="${pageContext.request.contextPath}/admin/schedules?id=${s.id}" style="padding:5px 12px; margin:0;">编辑</a>
                <form method="post" action="${pageContext.request.contextPath}/admin/schedules" style="margin:0;">
                  <input type="hidden" name="action" value="delete" />
                  <input type="hidden" name="id" value="${s.id}" />
                  <button class="btn btn-danger btn-sm" type="submit" style="padding:5px 12px; margin:0; white-space:nowrap;" onclick="return confirm('确认删除该场次？')">删除</button>
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
