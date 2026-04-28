<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="首页" />
<%@ include file="_layout_top.jspf" %>

<style>
  .home-wrap {
    display: grid;
    gap: 18px;
  }
  .home-card {
    background: linear-gradient(135deg, #fff 0%, #fffaf2 100%);
    border: 1px solid #f3e3b2;
    border-radius: 16px;
    padding: 28px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  }
  .home-title {
    font-size: 28px;
    font-weight: 900;
    color: #1f2937;
    margin: 0 0 10px 0;
  }
  .home-text {
    color: #4b5563;
    line-height: 1.8;
    font-size: 15px;
    margin: 0;
  }
  .home-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    margin-top: 18px;
  }
  .home-link {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 140px;
    padding: 11px 18px;
    border-radius: 10px;
    background: linear-gradient(135deg, #eda526, #f59e0b);
    color: #fff;
    font-weight: 700;
    text-decoration: none;
    box-shadow: 0 6px 16px rgba(237, 165, 38, 0.22);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }
  .home-link:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 22px rgba(237, 165, 38, 0.28);
    color: #fff;
  }
  .home-note {
    padding: 14px 16px;
    border-radius: 12px;
    background: #f8fafc;
    border: 1px solid #e5e7eb;
    color: #64748b;
    font-size: 14px;
  }
</style>

<div class="home-wrap">
  <div class="home-card">
    <h1 class="home-title">欢迎使用 MBTI 职业性格测试系统</h1>
    <p class="home-text">
      当前页面只保留最核心的功能入口，避免无效内容影响使用。你可以直接开始测评、查看历史记录，或者前往帮助页面了解系统使用方式。
    </p>
    <div class="home-actions">
      <a class="home-link" href="<c:url value='/test/schedules'/>">开始测评</a>
      <a class="home-link" href="<c:url value='/test/history'/>">查看历史</a>
      <a class="home-link" href="<c:url value='/help'/>">帮助说明</a>
    </div>
  </div>

  <div class="home-note">
    系统已精简到最核心功能，移除所有不稳定的装饰内容。
  </div>
</div>

<%@ include file="_layout_bottom.jspf" %>
  }
  
  .stat-num {
    font-size: 28px;
    font-weight: 900;
    color: #eda526;
  }
  
  .stat-label {
    font-size: 13px;
    color: #6b7280;
    margin-top: 8px;
    font-weight: 600;
  }
</style>

<div class="welcome-hero">
  <h1>👋 欢迎使用 MBTI 职业性格测试系统</h1>
  <p>发现自己的性格特点，探索职业发展方向。通过专业的性格测评帮助你更好地了解自己、规划未来。</p>
  <div class="welcome-btns">
    <a href="<c:url value='/test/schedules'/>" class="welcome-btn welcome-btn-primary">📋 开始测评</a>
    <a href="<c:url value='/test/history'/>" class="welcome-btn">📊 查看历史记录</a>
  </div>
</div>

<h3 style="margin: 0 0 20px; font-size: 20px; font-weight: 800; color: #1f2937;">快速导航</h3>
<div class="quick-actions">
  <div class="action-card">
    <div class="action-icon">📝</div>
    <div class="action-title">参加测评</div>
    <div class="action-desc">进入测评系统，完成 MBTI 性格测试问卷</div>
  </div>
  <div class="action-card">
    <div class="action-icon">📚</div>
    <div class="action-title">学习资料</div>
    <div class="action-desc">了解 MBTI 理论与解读</div>
  </div>
  <div class="action-card">
    <div class="action-icon">💼</div>
    <div class="action-title">职业推荐</div>
    <div class="action-desc">根据你的性格特点推荐适合的职业</div>
  </div>
</div>

<h3 style="margin: 32px 0 20px; font-size: 20px; font-weight: 800; color: #1f2937;">你的成果</h3>
<div class="stats-grid">
  <div class="stat-item">
    <div class="stat-num">5</div>
    <div class="stat-label">已完成测评</div>
  </div>
  <div class="stat-item">
    <div class="stat-num">1</div>
    <div class="stat-label">匹配的职业类型</div>
  </div>
  <div class="stat-item">
    <div class="stat-num">ISFJ</div>
    <div class="stat-label">你的性格类型</div>
  </div>
</div>

<%@ include file="_layout_bottom.jspf" %>
