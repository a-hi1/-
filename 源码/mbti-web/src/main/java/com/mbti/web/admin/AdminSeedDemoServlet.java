package com.mbti.web.admin;

import com.mbti.web.dao.AssessmentDao;
import com.mbti.web.dao.DimensionDao;
import com.mbti.web.dao.QuestionDao;
import com.mbti.web.model.Assessment;
import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "adminSeedDemoServlet", urlPatterns = "/admin/seed-demo")
public class AdminSeedDemoServlet extends HttpServlet {
  private static final String DEMO_ASSESSMENT_TITLE = "MBTI职业性格测试（示例题库）";

  private final AssessmentDao assessmentDao = new AssessmentDao();
  private final DimensionDao dimensionDao = new DimensionDao();
  private final QuestionDao questionDao = new QuestionDao();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");

    User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
    if (u == null || u.getType() != 1) {
      resp.setStatus(403);
      resp.setContentType("text/plain; charset=UTF-8");
      resp.getWriter().write("403 Forbidden");
      return;
    }
    int userId = u == null ? 0 : u.getId();

    String assessmentIdStr = val(req.getParameter("assessmentId"));
    boolean force = "1".equals(val(req.getParameter("force")));

    try {
      int assessmentId;
      if (!assessmentIdStr.isEmpty()) {
        assessmentId = Integer.parseInt(assessmentIdStr);
      } else {
        Assessment exists = assessmentDao.findByTitle(DEMO_ASSESSMENT_TITLE);
        if (exists != null) {
          assessmentId = exists.getId();
        } else {
          assessmentId = assessmentDao.create(DEMO_ASSESSMENT_TITLE, 0, 1);
        }
      }

      int dimEI = ensureDimension(assessmentId, "外向（E）/内向（I）", "精力来源：外向倾向于从外部获得能量；内向倾向于从内部获得能量");
      int dimSN = ensureDimension(assessmentId, "感觉（S）/直觉（N）", "信息获取：感觉关注细节与事实；直觉关注整体与可能性");
      int dimTF = ensureDimension(assessmentId, "思考（T）/情感（F）", "决策方式：思考偏理性客观；情感偏价值与关系");
      int dimJP = ensureDimension(assessmentId, "判断（J）/知觉（P）", "生活方式：判断偏计划与秩序；知觉偏灵活与开放");

      int existingQuestions = questionDao.countByAssessment(assessmentId);
      if (force || existingQuestions == 0) {
        seedQuestions(userId, assessmentId, dimEI, dimSN, dimTF, dimJP);
      }

      resp.sendRedirect(req.getContextPath() + "/admin/questions?assessmentId=" + assessmentId);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private int ensureDimension(int assessmentId, String title, String depict) throws Exception {
    Integer id = dimensionDao.findIdByAssessmentAndTitle(assessmentId, title);
    if (id != null) {
      return id;
    }
    return dimensionDao.create(assessmentId, title, depict);
  }

  private void seedQuestions(int userId, int assessmentId, int dimEI, int dimSN, int dimTF, int dimJP) throws Exception {
    // 约定：选项A为该维度“前者字母”（E/S/T/J），对应 checked=1

    // E / I
    questionDao.createTwoChoiceQuestion(assessmentId, dimEI, 1, 1, 2, userId,
        "在聚会或陌生场合，你更倾向于：",
        "主动与更多人交流，越聊越有劲",
        "更喜欢安静观察，与熟人小范围交流", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimEI, 1, 1, 2, userId,
        "工作中遇到新同事，你通常会：",
        "先主动打招呼并快速熟络",
        "先保持礼貌距离，慢慢熟悉", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimEI, 1, 1, 2, userId,
        "周末恢复精力的方式更像：",
        "约朋友出门或参加活动",
        "独处放松或做自己的事", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimEI, 1, 1, 2, userId,
        "表达想法时，你更常见的状态是：",
        "边说边想，越说越清楚",
        "想清楚再说，更喜欢一次讲完整", 1);

    // S / N
    questionDao.createTwoChoiceQuestion(assessmentId, dimSN, 1, 1, 2, userId,
        "阅读说明或学习新技能时，你更偏向：",
        "按步骤来，先掌握具体做法",
        "先理解原理与整体框架", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimSN, 1, 1, 2, userId,
        "做项目汇报时，你更擅长：",
        "用数据和事实说明细节",
        "用观点和趋势描述方向", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimSN, 1, 1, 2, userId,
        "你更信任的信息来源是：",
        "亲眼看到/亲身验证的结果",
        "从线索推断出的可能性", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimSN, 1, 1, 2, userId,
        "遇到问题时，你更常做：",
        "先处理眼前能落地的事项",
        "先设想多种方案再选择", 1);

    // T / F
    questionDao.createTwoChoiceQuestion(assessmentId, dimTF, 1, 1, 2, userId,
        "同事发生分歧时，你通常会：",
        "优先讨论规则与客观标准",
        "优先考虑感受与关系维护", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimTF, 1, 1, 2, userId,
        "评价一项方案，你更看重：",
        "是否高效、是否合理",
        "是否有温度、是否照顾到人", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimTF, 1, 1, 2, userId,
        "别人向你求助时，你更常见的反应是：",
        "先给出解决步骤和建议",
        "先倾听安慰并共情", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimTF, 1, 1, 2, userId,
        "做决定时，你更依赖：",
        "逻辑推理与利弊分析",
        "价值观与对他人的影响", 1);

    // J / P
    questionDao.createTwoChoiceQuestion(assessmentId, dimJP, 1, 1, 2, userId,
        "出行或做计划时，你更喜欢：",
        "提前安排并按计划执行",
        "大方向确定即可，过程随时调整", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimJP, 1, 1, 2, userId,
        "面对截止日期，你通常会：",
        "尽早完成，避免临时压力",
        "临近时集中冲刺更有状态", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimJP, 1, 1, 2, userId,
        "你的桌面或文件更像：",
        "分类明确、井井有条",
        "随手放置，想到再整理", 1);
    questionDao.createTwoChoiceQuestion(assessmentId, dimJP, 1, 1, 2, userId,
        "处理任务时，你更倾向：",
        "一次专注完成一个再切换",
        "同时推进多个，灵活切换", 1);
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
