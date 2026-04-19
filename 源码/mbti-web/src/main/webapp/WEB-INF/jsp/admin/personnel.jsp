<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="参测人员管理" />
<%@ include file="../_layout_top.jspf" %>

<c:if test="${not empty sessionScope.flash}">
  <div class="alert" style="margin-bottom:14px">
    <c:out value="${sessionScope.flash}"/>
  </div>
  <c:remove var="flash" scope="session" />
</c:if>

<div class="card" style="margin-bottom:14px">
  <div style="font-size:16px; font-weight:800; color:#1e293b; margin-bottom:16px; display:flex; align-items:center; gap:8px;">
    <span style="width:4px; height:16px; background:var(--primary); border-radius:2px;"></span>
    <c:choose>
      <c:when test="${not empty edit}">编辑参测人员</c:when>
      <c:otherwise>添加参测人员</c:otherwise>
    </c:choose>
    <div class="muted" style="margin-left:auto; font-weight:normal; font-size:13px;">默认密码：123456（可由此页面重置）</div>
  </div>
  <form method="post" action="${pageContext.request.contextPath}/admin/personnel" class="grid" style="align-items:end">
    <input type="hidden" name="action" value="${empty edit ? 'create' : 'update'}" />
    <input type="hidden" name="id" value="${edit.id}" />
    <div class="col-3">
      <label>登录名</label>
      <input name="login" value="${edit.login}" placeholder="手机号/学号/账号名" required />
    </div>
    <div class="col-3">
      <label>姓名</label>
      <input name="name" value="${edit.name}" placeholder="请输入真实姓名" required />
    </div>
    <div class="col-2">
      <label>密码</label>
      <c:choose>
        <c:when test="${empty edit}">
          <input name="passwd" value="123456" required />
        </c:when>
        <c:otherwise>
          <input name="passwd" value="******" disabled style="background:#f1f5f9; color:#94a3b8;" />
        </c:otherwise>
      </c:choose>
    </div>
    <div class="col-4">
      <label>分配批次</label>
      <select name="teamId">
        <option value="">-- 未分配 (暂不加入任何测试) --</option>
        <c:forEach items="${teams}" var="t">
          <option value="${t.id}" <c:if test="${not empty edit && edit.teamId == t.id}">selected</c:if>>[ ${t.id} ] <c:out value="${t.name}"/></option>
        </c:forEach>
      </select>
    </div>
    
    <div class="col-12" style="height:1px; background:var(--border); margin:4px 0;"></div>

    <div class="col-3">
      <label>手机号 <span class="muted" style="font-weight:normal">(可选)</span></label>
      <input name="phone" value="${edit.phone}" placeholder="联系电话" />
    </div>
    <div class="col-2">
      <label>性别</label>
      <select name="gender">
        <option value="">未知</option>
        <option value="M" <c:if test="${not empty edit && edit.gender == 'M'}">selected</c:if>>男</option>
        <option value="F" <c:if test="${not empty edit && edit.gender == 'F'}">selected</c:if>>女</option>
      </select>
    </div>
    <div class="col-3">
      <label>出生日期 <span class="muted" style="font-weight:normal">(可选)</span></label>
      <input name="birthdate" type="date" value="${edit.birthdate}" />
    </div>
    <div class="col-4">
      <label>电子邮箱 <span class="muted" style="font-weight:normal">(可选)</span></label>
      <input name="email" value="${edit.email}" placeholder="example@email.com" />
    </div>
    <div class="col-12" style="display:flex;justify-content:flex-end;gap:12px; margin-top:8px;">
      <c:choose>
        <c:when test="${not empty edit}">
          <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/personnel">取消编辑</a>
          <button class="btn btn-primary" type="submit">保存修改</button>
        </c:when>
        <c:otherwise>
          <button class="btn btn-primary" type="submit">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            添加参测人员
          </button>
        </c:otherwise>
      </c:choose>
    </div>
  </form>
</div>

<div class="card" style="margin-bottom:14px">
  <div style="font-size:16px; font-weight:800; color:#1e293b; margin-bottom:16px; display:flex; align-items:center; gap:8px;">
    <span style="width:4px; height:16px; background:#3b82f6; border-radius:2px;"></span>人员筛选与检索
  </div>
  <form method="get" action="${pageContext.request.contextPath}/admin/personnel" class="grid" style="align-items:end; margin-bottom: 24px;">
    <div class="col-4">
      <label>批次筛选</label>
      <select name="teamId">
        <option value="">全部批次</option>
        <c:forEach items="${teams}" var="t">
          <option value="${t.id}" <c:if test="${not empty teamId && teamId == t.id}">selected</c:if>>[ ${t.id} ] <c:out value="${t.name}"/></option>
        </c:forEach>
      </select>
    </div>
    <div class="col-3">
      <label>姓名</label>
      <input name="name" value="${name}" placeholder="按姓名或登录名检索" />
    </div>
    <div class="col-3">
      <label>手机号</label>
      <input name="phone" value="${phone}" placeholder="按手机号检索" />
    </div>
    <div class="col-2" style="display:flex;justify-content:flex-end;gap:10px">
      <a class="btn btn-light" href="${pageContext.request.contextPath}/admin/personnel">重置</a>
      <button class="btn btn-view" type="submit">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        查询匹配
      </button>
    </div>
  </form>

  <div style="height:1px; background:var(--border); margin: 0 -24px 20px -24px;"></div>

  <div style="font-size:16px; font-weight:800; color:#1e293b; margin-bottom:16px; display:flex; align-items:center; gap:8px;">
    <span style="width:4px; height:16px; background:#10b981; border-radius:2px;"></span>快捷批量导入
    <div class="muted" style="margin-left:auto; font-weight:normal; font-size:13px; display:flex; align-items:center; gap:6px;">
      <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
      支持文本框直接粘贴或上传文件
    </div>
  </div>
  
  <form id="batch-import" method="post" action="${pageContext.request.contextPath}/admin/personnel" class="grid" enctype="multipart/form-data" style="align-items:start;">
    <input type="hidden" name="action" value="batchImport" />
    
    <div class="col-4">
      <div style="background:#f8fafc; padding:16px; border-radius:8px; border:1px dashed #cbd5e1; height: 100%;">
        <label>未指定批次时的默认落入目标</label>
        <select name="defaultTeamId" style="margin-bottom:16px;">
          <option value="">-- 未分配 --</option>
          <c:forEach items="${teams}" var="t">
            <option value="${t.id}" <c:if test="${not empty teamId && teamId == t.id}">selected</c:if>>[ ${t.id} ] <c:out value="${t.name}"/></option>
          </c:forEach>
        </select>
        
        <label>导入文件 <span class="muted" style="font-weight:normal;">(.txt / .csv)</span></label>
        <div style="background:#fff; border:1px solid var(--border); border-radius:6px; padding:10px; margin-bottom:12px;">
          <input type="file" name="file" accept=".txt,.csv" style="border:none; padding:0;" />
        </div>
        <div class="muted" style="font-size:12px; line-height:1.5;">若选择上传文件，优先解析文件内容忽略右侧文本。</div>
      </div>
    </div>
    
    <div class="col-8" style="display:flex; flex-direction:column; height: 100%;">
      <label>文本内容 <span class="muted" style="font-weight:normal;">(多条记录请换行)</span></label>
      <textarea name="lines" style="flex:1; width:100%; min-height: 160px; padding:12px 14px; border:1px solid #cbd5e1; border-radius:8px; font-family:monospace; line-height:1.6;" placeholder="格式要求（英文逗号或制表符分隔）：&#10;登录名, 姓名, [手机号, 性别, 生日, 邮箱, 指定批次ID]&#10;&#10;示例：&#10;user01, 张三&#10;user02, 李四, 13800000000, M, 2000-01-01, li@qq.com, 1"></textarea>
      
      <div style="display:flex;justify-content:flex-end; margin-top:16px;">
        <button class="btn" style="background:#10b981; color:#fff; border-color:#059669;" type="submit" onclick="return confirm('确认执行批量导入？规则：\n1. 已存在的登录名将更新信息\n2. 空白列将保留原值/默认值')">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
          确认开始导入
        </button>
      </div>
    </div>
  </form>
</div>

<div class="card" style="padding:0; overflow:hidden; box-shadow:0 2px 10px rgba(0,0,0,0.05); border:1px solid var(--border);">
  <table class="table">
    <tr>
      <th style="width:70px; text-align:center;">ID</th>
      <th style="width:160px">登录名</th>
      <th style="width:140px">姓名</th>
      <th style="width:160px">批次</th>
      <th style="width:130px">手机号</th>
      <th style="width:80px; text-align:center;">性别</th>
      <th style="width:120px">生日</th>
      <th>邮箱</th>
      <th style="min-width:240px; text-align:right; padding-right:24px; white-space:nowrap;">操作</th>
    </tr>
    <c:forEach items="${personnel}" var="p">
      <tr>
        <td style="text-align:center; color:#666;">${p.id}</td>
        <td style="font-weight:500;"><c:out value="${p.login}"/></td>
        <td><c:out value="${p.name}"/></td>
        <td>
          <c:choose>
            <c:when test="${not empty p.teamName}"><span class="tag tag-gray"><c:out value="${p.teamName}"/></span></c:when>
            <c:otherwise><span class="muted">未分配</span></c:otherwise>
          </c:choose>
        </td>
        <td style="color:#555;"><c:out value="${p.phone}"/></td>
        <td style="text-align:center;">
          <c:choose>
            <c:when test="${p.gender == 'M'}">男</c:when>
            <c:when test="${p.gender == 'F'}">女</c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td style="color:#777;"><c:out value="${p.birthdate}"/></td>
        <td style="color:#555;"><c:out value="${p.email}"/></td>
        <td style="text-align:right; padding-right:24px; white-space:nowrap;">
          <div style="display:flex; justify-content:flex-end; align-items:center; gap:8px;">
            <a class="btn btn-view btn-sm" href="${pageContext.request.contextPath}/admin/personnel?id=${p.id}" style="padding:5px 12px; margin:0;">编辑</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/personnel" style="margin:0;">
              <input type="hidden" name="id" value="${p.id}" />
              <input type="hidden" name="action" value="resetPwd" />
              <button class="btn btn-light btn-sm" type="submit" style="padding:5px 12px; margin:0; white-space:nowrap;" onclick="return confirm('确认重置密码为 123456？')">重置密码</button>
            </form>
            <form method="post" action="${pageContext.request.contextPath}/admin/personnel" style="margin:0;">
              <input type="hidden" name="id" value="${p.id}" />
              <input type="hidden" name="action" value="delete" />
              <button class="btn btn-danger btn-sm" type="submit" style="padding:5px 12px; margin:0; white-space:nowrap;" onclick="return confirm('确认删除该参测人员？')">删除</button>
            </form>
          </div>
        </td>
      </tr>
    </c:forEach>
  </table>

  <c:if test="${empty personnel}">
    <div class="muted" style="padding:24px; text-align:center; background:#fafbfc;">暂无参测人员 / 搜索结果为空</div>
  </c:if>
</div>

<%@ include file="../_layout_bottom.jspf" %>
