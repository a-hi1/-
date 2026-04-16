<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="在线测评" />
<%@ include file="../_layout_top.jspf" %>

<div class="exam-banner">
  <div class="exam-top">
    <div class="exam-tip">
      <div class="exam-tip-top timer"><div class="exam-tip-ico">⏱</div></div>
      <div class="exam-tip-body">
        在${schedule.duration}分钟内完成。
        <small>请合理安排作答节奏</small>
      </div>
    </div>
    <div class="exam-tip">
      <div class="exam-tip-top honest"><div class="exam-tip-ico">✓</div></div>
      <div class="exam-tip-body">
        诚实回答（即使你不喜欢这个答案）。
        <small>按第一感觉选择更准确</small>
      </div>
    </div>
    <div class="exam-tip">
      <div class="exam-tip-top neutral"><div class="exam-tip-ico">⛔</div></div>
      <div class="exam-tip-body">
        尽量不要选择“中立”的答案。
        <small>二选一更能区分偏好</small>
      </div>
    </div>
  </div>
</div>

<div class="exam-wrap">
  <div class="exam-head">
    <div>
      <div class="exam-title">${schedule.assessmentTitle}</div>
      <div class="exam-sub">共${fn:length(questions)}题 · 单题作答支持上一题/下一题与题号跳转</div>
    </div>
    <div class="exam-mode">
      <button class="exam-mode-link" type="button" id="modeSingleBtn">单题作答</button>
      <button class="exam-mode-link" type="button" id="modeAllBtn">整卷预览</button>
      <a class="btn btn-light" href="<c:url value='/test/schedules' />">返回选择场次</a>
    </div>
  </div>

<c:if test="${not empty error}">
  <div class="alert alert-error" style="margin-bottom:12px">${error}</div>
</c:if>

<c:choose>
  <c:when test="${empty questions}">
    <div class="card" style="margin:16px">
      <div class="muted" style="line-height:1.8">
        当前测评暂无法开始作答（题库/场次配置不完整）。
      </div>
      <div style="margin-top:12px">
        <a class="btn btn-light" href="<c:url value='/test/schedules' />">返回选择场次</a>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <form method="post" action="<c:url value='/test/take' />" id="examForm">
      <input type="hidden" name="scheduleId" value="${schedule.id}" />
      <input type="hidden" name="examId" value="${examId}" />
      <input type="hidden" name="action" value="submit" id="examAction" />

      <div class="exam-body" id="examBody">
        <c:forEach items="${questions}" var="q" varStatus="st">
          <div class="exam-question" data-index="${st.index}" data-qid="${q.id}">
            <div class="exam-qtitle">第${st.index + 1}题</div>
            <table class="exam-kv">
              <tr>
                <td>题目内容：</td>
                <td>${q.title}</td>
              </tr>
              <c:forEach items="${q.choices}" var="c" varStatus="cs">
                <tr>
                  <td>选项${cs.index + 1}：</td>
                  <td>
                    <label class="exam-choice">
                      <input type="radio" name="q_${q.id}" value="${c.id}" <c:if test="${param['q_'.concat(q.id)] == c.id}">checked</c:if> />
                      <span>${c.title}</span>
                    </label>
                  </td>
                </tr>
              </c:forEach>
            </table>
          </div>
        </c:forEach>

        <div class="exam-actions" id="singleModeActions">
          <button class="btn btn-warn" type="button" id="prevBtn">上一题</button>
          <button class="btn btn-warn" type="button" id="nextBtn">下一题</button>
        </div>

        <div class="exam-navigator">
          <div class="exam-nav-grid" id="navGrid"></div>
          <div class="exam-submit-row">
            <button class="btn btn-warn" type="submit">交卷</button>
          </div>
        </div>
      </div>
    </form>

    <form method="post" action="<c:url value='/test/take' />" id="discardForm" style="display:none">
      <input type="hidden" name="scheduleId" value="${schedule.id}" />
      <input type="hidden" name="examId" value="${examId}" />
      <input type="hidden" name="action" value="discard" />
    </form>

    <div class="exam-modal-mask" id="saveModal" style="display:none" aria-hidden="true">
      <div class="exam-modal" role="dialog" aria-modal="true" aria-labelledby="saveModalTitle">
        <div class="exam-modal-title" id="saveModalTitle">是否保存本次作答？</div>
        <div class="exam-modal-desc">选择“保存并提交”会提交答案并生成结果；选择“不保存退出”将丢弃本次作答。</div>
        <div class="exam-modal-actions">
          <button type="button" class="btn btn-light" id="discardBtn">不保存退出</button>
          <button type="button" class="btn btn-primary" id="saveBtn">保存并提交</button>
          <button type="button" class="btn btn-light" id="cancelBtn">继续作答</button>
        </div>
      </div>
    </div>

    <script>
      (function(){
        var questions = Array.prototype.slice.call(document.querySelectorAll('.exam-question'));
        var navGrid = document.getElementById('navGrid');
        var prevBtn = document.getElementById('prevBtn');
        var nextBtn = document.getElementById('nextBtn');
        var modeSingleBtn = document.getElementById('modeSingleBtn');
        var modeAllBtn = document.getElementById('modeAllBtn');
        var singleModeActions = document.getElementById('singleModeActions');

        if (!questions.length || !navGrid) return;

        // 交卷时提示“是否保存”
        var examForm = document.getElementById('examForm');
        var discardForm = document.getElementById('discardForm');
        var saveModal = document.getElementById('saveModal');
        var saveBtn = document.getElementById('saveBtn');
        var discardBtn = document.getElementById('discardBtn');
        var cancelBtn = document.getElementById('cancelBtn');
        var confirmedSubmit = false;

        function openModal(){
          if (!saveModal) return;
          saveModal.style.display = 'flex';
          saveModal.setAttribute('aria-hidden', 'false');
        }
        function closeModal(){
          if (!saveModal) return;
          saveModal.style.display = 'none';
          saveModal.setAttribute('aria-hidden', 'true');
        }

        if (examForm) {
          examForm.addEventListener('submit', function(e){
            if (confirmedSubmit) return;
            e.preventDefault();
            openModal();
          });
        }
        if (saveBtn) {
          saveBtn.addEventListener('click', function(){
            confirmedSubmit = true;
            closeModal();
            if (examForm) examForm.submit();
          });
        }
        if (discardBtn) {
          discardBtn.addEventListener('click', function(){
            closeModal();
            if (discardForm) discardForm.submit();
          });
        }
        if (cancelBtn) {
          cancelBtn.addEventListener('click', function(){
            closeModal();
          });
        }
        if (saveModal) {
          saveModal.addEventListener('click', function(ev){
            if (ev.target === saveModal) closeModal();
          });
        }

        var params = new URLSearchParams(location.search);
        var scheduleId = String(${schedule.id});
        var modeKey = 'exam.mode.' + scheduleId;
        var qKey = 'exam.q.' + scheduleId;
        var modeParam = params.get('mode');
        var qParam = params.get('q');

        // 后端校验未完成时，定位到第一道未答题（并保留已选答案）
        var missingIndex = ${missingIndex != null ? missingIndex : -1};
        var hasError = ${not empty error ? 'true' : 'false'};

        // 默认“单题作答”，只在明确传参或本场次已保存偏好时才切换
        var mode = (modeParam === 'all' || modeParam === 'single')
          ? modeParam
          : (localStorage.getItem(modeKey) || 'single');
        var currentIndex = 0;

        if (hasError && missingIndex >= 0) {
          mode = 'single';
          currentIndex = Math.max(0, Math.min(questions.length - 1, missingIndex));
          localStorage.setItem(modeKey, mode);
          localStorage.setItem(qKey, String(currentIndex));
        } else if (qParam && !isNaN(parseInt(qParam, 10))) {
          currentIndex = Math.max(0, Math.min(questions.length - 1, parseInt(qParam, 10) - 1));
        } else {
          var savedQ = localStorage.getItem(qKey);
          if (savedQ && !isNaN(parseInt(savedQ, 10))) {
            currentIndex = Math.max(0, Math.min(questions.length - 1, parseInt(savedQ, 10)));
          }
        }

        function updateUrl(){
          try{
            var p = new URLSearchParams(location.search);
            p.set('mode', mode);
            p.set('q', String(currentIndex + 1));
            var next = location.pathname + '?' + p.toString();
            history.replaceState(null, '', next);
          }catch(e){}
        }

        function isAnswered(qEl){
          var qid = qEl.getAttribute('data-qid');
          if (!qid) return false;
          return !!document.querySelector('input[name="q_' + qid + '"]:checked');
        }

        function renderNav(){
          navGrid.innerHTML = '';
          for (var i = 0; i < questions.length; i++){
            (function(idx){
              var btn = document.createElement('button');
              btn.type = 'button';
              btn.className = 'exam-nav-btn';
              btn.textContent = String(idx + 1);
              btn.addEventListener('click', function(){
                currentIndex = idx;
                localStorage.setItem(qKey, String(currentIndex));
                sync();
                updateUrl();
                if (mode === 'all') {
                  questions[idx].scrollIntoView({behavior:'smooth', block:'start'});
                }
              });
              navGrid.appendChild(btn);
            })(i);
          }
        }

        function setMode(nextMode){
          mode = nextMode === 'all' ? 'all' : 'single';
          localStorage.setItem(modeKey, mode);
          sync();
          updateUrl();
        }

        function sync(){
          for (var i = 0; i < questions.length; i++){
            var qEl = questions[i];
            if (mode === 'all'){
              qEl.classList.add('is-active');
            } else {
              qEl.classList.toggle('is-active', i === currentIndex);
            }
          }

          if (singleModeActions){
            singleModeActions.style.display = (mode === 'all') ? 'none' : 'flex';
          }

          var navBtns = navGrid.querySelectorAll('.exam-nav-btn');
          for (var j = 0; j < navBtns.length; j++){
            var b = navBtns[j];
            b.classList.toggle('is-active', j === currentIndex && mode !== 'all');
            b.classList.toggle('is-answered', isAnswered(questions[j]));
          }

          if (prevBtn) prevBtn.disabled = (currentIndex <= 0);
          if (nextBtn) nextBtn.disabled = (currentIndex >= questions.length - 1);

          if (modeSingleBtn) modeSingleBtn.classList.toggle('is-active', mode === 'single');
          if (modeAllBtn) modeAllBtn.classList.toggle('is-active', mode === 'all');
        }

        function bindAnswerChange(){
          document.getElementById('examForm').addEventListener('change', function(e){
            var t = e.target;
            if (t && t.tagName === 'INPUT' && t.type === 'radio'){
              sync();
            }
          });
        }

        renderNav();
        bindAnswerChange();

        if (prevBtn) prevBtn.addEventListener('click', function(){
          if (currentIndex > 0){ currentIndex--; sync(); }
          localStorage.setItem(qKey, String(currentIndex));
          updateUrl();
        });
        if (nextBtn) nextBtn.addEventListener('click', function(){
          if (currentIndex < questions.length - 1){ currentIndex++; sync(); }
          localStorage.setItem(qKey, String(currentIndex));
          updateUrl();
        });
        if (modeSingleBtn) modeSingleBtn.addEventListener('click', function(){ setMode('single'); });
        if (modeAllBtn) modeAllBtn.addEventListener('click', function(){ setMode('all'); });

        // 初始化：不要强制覆盖 modeParam，只同步界面
        sync();
        updateUrl();
      })();
    </script>
  </c:otherwise>
</c:choose>

</div>

<%@ include file="../_layout_bottom.jspf" %>
