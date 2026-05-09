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
      <div style="display:flex; gap:20px; align-items:center; background: linear-gradient(135deg, #f0fdf4 0%, #e0f2fe 100%); padding: 24px; border-radius: 16px; margin-bottom: 20px; box-shadow: 0 4px 16px rgba(0,0,0,0.05);">
        <div style="flex:1;">
          <div style="font-size: 16px; color: #4b5563; margin-bottom: 8px;">经过综合测算，你的 MBTI 性格类型是：</div>
          <div style="font-size: 64px; font-weight: 900; color: #1d4ed8; line-height: 1.1; letter-spacing: 4px; text-shadow: 0 4px 12px rgba(29,78,216,0.2); font-family: 'Arial Black', Impact, sans-serif;">${resultText}</div>
          <c:if test="${not empty mbtiProfile}">
            <div style="font-size: 22px; color: #059669; font-weight: bold; margin-top: 12px;">🏆 ${mbtiProfile.title}</div>
          </c:if>
        </div>
        <div style="flex-shrink:0; width:200px; text-align:center;">
          <img src="${pageContext.request.contextPath}/static/img/${resultText}.jpg" alt="${resultText}" style="width:100%; max-width:200px; border-radius:12px; box-shadow:0 8px 24px rgba(0,0,0,0.15); background:#fff;" onerror="this.style.display='none'" />
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <c:if test="${!pending && not empty mbtiProfile}">
    <div style="margin-top:10px">
      <div style="line-height:1.8; font-size:15px;">${mbtiProfile.summary}</div>

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
  <h3 style="margin:0 0 10px">维度得分雷达图及明细</h3>
  
  <!-- 图表容器 -->
  <div id="radarChart" style="width: 100%; height: 350px; margin-bottom: 20px;"></div>
  
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
        <td><span style="font-weight:bold; color:#1d4ed8;">${ds.letter}</span></td>
      </tr>
    </c:forEach>
  </table>
  </div>
  
  <!-- 引入 ECharts 库 -->
  <script src="https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js"></script>
  <script>
    (function(){
      var chartDom = document.getElementById('radarChart');
      if (chartDom && typeof echarts !== 'undefined') {
        var myChart = echarts.init(chartDom);
        var indicator = [];
        var data = [];

        <c:forEach items="${dimensionScores}" var="ds">
          indicator.push({ name: '${ds.dimension.title}', max: ${ds.total} });
          data.push(${ds.score});
        </c:forEach>

        var option = {
          tooltip: {
            trigger: 'item'
          },
          radar: {
            indicator: indicator,
            radius: '65%',
            splitNumber: 5,
            axisName: {
              color: '#fff',
              backgroundColor: '#4b5563',
              borderRadius: 4,
              padding: [4, 8],
              fontSize: 13
            }
          },
          series: [
            {
              name: '维度得分',
              type: 'radar',
              data: [
                {
                  value: data,
                  name: '你的维度偏好',
                  areaStyle: {
                    color: 'rgba(29, 78, 216, 0.2)'
                  },
                  lineStyle: {
                    color: '#1d4ed8',
                    width: 2
                  },
                  itemStyle: {
                    color: '#1d4ed8'
                  }
                }
              ]
            }
          ]
        };
        myChart.setOption(option);
        window.addEventListener('resize', function() {
          myChart.resize();
        });
      }
    })();
  </script>
</c:if>

<%@ include file="../_layout_bottom.jspf" %>
