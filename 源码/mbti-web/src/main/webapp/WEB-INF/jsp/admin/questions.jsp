<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="题库查看" />
<%@ include file="../_layout_top.jspf" %>

<c:if test="${not empty assessmentId}">
  <div class="card" style="margin-bottom:14px">
    <form method="post" action="${pageContext.request.contextPath}/admin/questions" class="grid" style="align-items:end">
      <input type="hidden" name="action" value="save" />
      <input type="hidden" name="assessmentId" value="${assessmentId}" />
      <input type="hidden" name="id" value="${edit.id}" />
      <div class="col-12 muted" style="margin-bottom:4px">
        <c:choose>
          <c:when test="${not empty edit}">编辑题目（双选项）</c:when>
          <c:otherwise>新增题目（双选项）</c:otherwise>
        </c:choose>
      </div>
      <div class="col-4">
        <label>所属维度</label>
        <select name="dimensionId" required>
          <c:forEach items="${dimensions}" var="d">
            <option value="${d.id}" <c:if test="${not empty edit && d.id == edit.dimensionId}">selected</c:if>><c:out value="${d.title}"/></option>
          </c:forEach>
        </select>
      </div>
      <div class="col-8">
        <label>题目</label>
        <input name="title" value="${edit.title}" placeholder="请输入题目内容" required />
      </div>
      <div class="col-4">
        <label>选项A</label>
        <input name="choiceA" value="${not empty edit && edit.choices.size() >= 1 ? edit.choices[0].title : ''}" required />
      </div>
      <div class="col-4">
        <label>选项B</label>
        <input name="choiceB" value="${not empty edit && edit.choices.size() >= 2 ? edit.choices[1].title : ''}" required />
      </div>
      <div class="col-2">
        <label>正确项</label>
        <select name="correct">
          <option value="1" <c:if test="${empty correctIndex || correctIndex == 1}">selected</c:if>>A</option>
          <option value="2" <c:if test="${correctIndex == 2}">selected</c:if>>B</option>
        </select>
      </div>
      <div class="col-2" style="display:flex;justify-content:flex-end;gap:10px">
        <c:if test="${not empty edit}">
          <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/questions?assessmentId=${assessmentId}">取消</a>
        </c:if>
        <button class="btn btn-primary" type="submit">保存</button>
      </div>
    </form>
  </div>
</c:if>

<div class="card" style="margin-bottom:12px">
  <div class="grid">
    <div class="col-6">
      <form method="get" action="${pageContext.request.contextPath}/admin/questions">
        <div class="form-row">
          <label>选择测评</label>
          <select name="assessmentId" onchange="this.form.submit()">
            <c:forEach items="${assessments}" var="a">
              <option value="${a.id}" <c:if test="${a.id == assessmentId}">selected</c:if>>${a.title}</option>
            </c:forEach>
          </select>
        </div>
      </form>
    </div>
    <div class="col-6" style="display:flex;align-items:flex-end;justify-content:flex-end">
      <c:if test="${not empty assessmentId}">
        <form method="post" action="${pageContext.request.contextPath}/admin/seed-demo" style="display:inline-block;margin-right:10px">
          <input type="hidden" name="assessmentId" value="${assessmentId}" />
          <button class="btn btn-warn" type="submit" onclick="return confirm('将为当前测评补齐维度并生成示例题目（仅在题库为空时生成）。继续？')">一键生成示例题目</button>
        </form>
      </c:if>
      <a class="btn btn-light" href="<c:url value='/admin/assessments' />">返回列表</a>
    </div>
  </div>
  <div class="muted" style="font-size:12px">当前测评ID：${assessmentId}</div>
</div>

<div class="card">
  <table class="table">
    <tr>
      <th style="width:56px">序号</th>
      <th>题目</th>
      <th style="width:220px">维度</th>
      <th>选项（标注正确项）</th>
      <th style="width:120px">操作</th>
    </tr>
    <c:forEach items="${questions}" var="q" varStatus="st">
      <tr>
        <td>${st.index + 1}</td>
        <td><c:out value="${q.title}"/></td>
        <td><c:out value="${dimTitleById[q.dimensionId]}"/></td>
        <td>
          <div style="display:flex;flex-direction:column;gap:8px">
            <c:forEach items="${q.choices}" var="c">
              <div style="display:flex;align-items:center;gap:8px">
                <c:if test="${c.checked == 1}">
                  <span class="tag tag-green">正确</span>
                  <span style="font-weight:500;color:#111827"><c:out value="${c.title}"/></span>
                </c:if>
                <c:if test="${c.checked != 1}">
                  <span class="tag tag-gray">选项</span>
                  <span style="color:#4b5563"><c:out value="${c.title}"/></span>
                </c:if>
              </div>
            </c:forEach>
          </div>
        </td>
        <td>
          <div style="display:flex;gap:8px;flex-wrap:wrap">
            <a class="btn" href="${pageContext.request.contextPath}/admin/questions?assessmentId=${assessmentId}&id=${q.id}">编辑</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/questions" style="display:inline">
              <input type="hidden" name="action" value="delete" />
              <input type="hidden" name="assessmentId" value="${assessmentId}" />
              <input type="hidden" name="id" value="${q.id}" />
              <button class="btn" type="submit" onclick="return confirm('确认删除该题目？')">删除</button>
            </form>
          </div>
        </td>
      </tr>
    </c:forEach>
  </table>

  <c:if test="${empty questions}">
    <div style="text-align:center;padding:40px 0;color:#6b7280">
      <div style="font-size:48px;margin-bottom:16px">📭</div>
      <p>当前测评暂无题目，或未选择测评</p>
    </div>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
