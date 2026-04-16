<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="使用说明" />
<%@ include file="_layout_top.jspf" %>

<div class="grid">
  <div class="col-12">
    <div class="card">
      <h3 style="margin:0 0 8px">参测人员怎么操作</h3>
      <div class="muted" style="line-height:1.9">
        1）登录后进入“开始测评” → 选择状态为“进行中”的场次 → 点击“开始”。<br/>
        2）完成全部题目后提交 → 自动生成类型 → 在“测评记录”查看历史结果。<br/>
        3）如果提示“暂无题目/维度关联错误”，说明题库配置不完整，需要管理员处理。
      </div>
    </div>
  </div>

  <div class="col-12">
    <div class="card">
      <h3 style="margin:0 0 8px">管理员怎么配置（建议按顺序）</h3>
      <div class="muted" style="line-height:1.9">
        1）考核类型管理：创建测评（例如 16题版/20题版）。<br/>
        2）性格维度管理：为该测评创建 4 个维度标题，标题里必须包含字母对：E/I、S/N、T/F、J/P（系统通过标题里的字母判定维度）。<br/>
        3）题目管理：选择测评 → 新增题目并选择所属维度；每题至少 2 个选项。<br/>
        4）批次管理：创建批次（班级/批次）。<br/>
        5）参测人员管理：为批次创建参测账号（默认密码通常为 123456）。<br/>
        6）测试安排：创建场次并选择测评与批次；将状态设为“进行中”后参测人员才可开始。
      </div>
    </div>
  </div>

  <div class="col-12">
    <div class="card">
      <h3 style="margin:0 0 8px">常见问题</h3>
      <div class="muted" style="line-height:1.9">
        <b>Q：不管怎么选都是 ESTJ？</b><br/>
        A：通常是题目关联的“维度ID”与当前测评的维度不匹配（常见于导入示例数据），导致维度题数为 0，明细无法统计。管理员可在“题目管理”检查维度列是否提示“不属于当前测评”。<br/>
        <br/>
        <b>Q：创建了场次但没有题目怎么办？</b><br/>
        A：到“题目管理”选中对应测评添加题目；再把场次状态设为“进行中”。系统会在参测端给出明确提示，避免空白问卷。
      </div>
    </div>
  </div>
</div>

<%@ include file="_layout_bottom.jspf" %>
