package com.mbti.web.test;

import com.mbti.web.dao.DimensionDao;
import com.mbti.web.dao.ExamDao;
import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.QuestionDao;
import com.mbti.web.dao.ScheduleDao;
import com.mbti.web.dao.TeamDao;
import com.mbti.web.model.Choice;
import com.mbti.web.model.Dimension;
import com.mbti.web.model.Question;
import com.mbti.web.model.Schedule;
import com.mbti.web.model.Team;
import com.mbti.web.model.User;
import com.mbti.web.util.MbtiScoring;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@WebServlet(name = "takeExamServlet", urlPatterns = "/test/take")
public class TakeExamServlet extends HttpServlet {
  private final ScheduleDao scheduleDao = new ScheduleDao();
  private final QuestionDao questionDao = new QuestionDao();
  private final ExamDao examDao = new ExamDao();
  private final DimensionDao dimensionDao = new DimensionDao();
  private final PersonnelDao personnelDao = new PersonnelDao();
  private final TeamDao teamDao = new TeamDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String scheduleIdStr = req.getParameter("scheduleId");
    if (scheduleIdStr == null) {
      resp.sendError(400, "missing scheduleId");
      return;
    }

    try {
      int scheduleId = Integer.parseInt(scheduleIdStr);
      Schedule schedule = scheduleDao.findById(scheduleId);
      if (schedule == null) {
        resp.sendError(404, "schedule not found");
        return;
      }

      User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
      if (u == null || u.getType() != 4) {
        resp.sendError(403, "forbidden");
        return;
      }

      Integer teamId = personnelDao.findTeamIdByUserId(u.getId());
      if (teamId == null || schedule.getTeamId() != teamId) {
        resp.sendError(403, "schedule not in your team");
        return;
      }

      Team team = teamDao.findById(teamId);
      if (team == null || team.getStatus() == null || team.getStatus() != 1) {
        resp.sendError(403, "team not active");
        return;
      }

      if (schedule.getStatus() != 2) {
        resp.sendError(403, "schedule not active");
        return;
      }

      LocalDateTime now = LocalDateTime.now();
      if (schedule.getBeginDate() == null || schedule.getEndDate() == null
          || now.isBefore(schedule.getBeginDate()) || now.isAfter(schedule.getEndDate())) {
        resp.sendError(403, "schedule not in time window");
        return;
      }

      if (schedule.getAssessmentId() <= 0) {
        req.setAttribute("schedule", schedule);
        req.setAttribute("questions", java.util.Collections.emptyList());
        req.setAttribute("examId", null);
        req.setAttribute("error", "该场次未关联测评类型，无法开始答题。请联系管理员在“测试安排”中为该场次选择测评。");
        req.getRequestDispatcher("/WEB-INF/jsp/test/take.jsp").forward(req, resp);
        return;
      }

      List<Question> questions = questionDao.listByAssessmentWithChoices(schedule.getAssessmentId());

      if (questions.isEmpty()) {
        req.setAttribute("schedule", schedule);
        req.setAttribute("questions", questions);
        req.setAttribute("examId", null);
        req.setAttribute("error", "该测评暂无可用题目。请管理员到“题目管理”中为此测评添加题目，并确保每题至少包含2个选项。");
        req.getRequestDispatcher("/WEB-INF/jsp/test/take.jsp").forward(req, resp);
        return;
      }

      for (Question q : questions) {
        if (q.getChoices() == null || q.getChoices().isEmpty()) {
          req.setAttribute("schedule", schedule);
          req.setAttribute("questions", Collections.emptyList());
          req.setAttribute("examId", null);
          req.setAttribute("error", "题库存在未配置选项的题目，无法开始答题。请管理员检查该测评下的题目选项是否完整。");
          req.getRequestDispatcher("/WEB-INF/jsp/test/take.jsp").forward(req, resp);
          return;
        }
      }

      // 维度映射校验/修复：兼容历史数据维度ID不属于当前测评（会导致结果恒为 ESTJ）
      List<Dimension> dims = dimensionDao.listByAssessment(schedule.getAssessmentId());
      if (dims.size() < 4) {
        req.setAttribute("schedule", schedule);
        req.setAttribute("questions", Collections.emptyList());
        req.setAttribute("examId", null);
        req.setAttribute("error", "该测评维度未完善（需要4个维度：E/I、S/N、T/F、J/P），无法开始答题。请联系管理员补齐维度配置。");
        req.getRequestDispatcher("/WEB-INF/jsp/test/take.jsp").forward(req, resp);
        return;
      }
      Map<String, Integer> dimIdByPairKey = new HashMap<>();
      Set<Integer> dimIds = new HashSet<>();
      for (Dimension d : dims) {
        dimIds.add(d.getId());
        String key = MbtiScoring.pairKey(d.getTitle());
        if (key != null) {
          dimIdByPairKey.put(key, d.getId());
        }
      }

      List<Question> resolvedQuestions = new ArrayList<>(questions.size());
      for (Question q : questions) {
        int dimId = q.getDimensionId();
        if (!dimIds.contains(dimId)) {
          Dimension rawDim = dimensionDao.findById(dimId);
          String key = rawDim == null ? null : MbtiScoring.pairKey(rawDim.getTitle());
          Integer resolved = key == null ? null : dimIdByPairKey.get(key);
          if (resolved == null) {
            req.setAttribute("schedule", schedule);
            req.setAttribute("questions", Collections.emptyList());
            req.setAttribute("examId", null);
            req.setAttribute("error",
              "题库维度关联错误：题目维度不属于当前测评，无法计算结果。请管理员到“性格维度管理/题目管理”检查维度归属与题目关联。\n" +
              "（常见原因：导入的示例数据 question_dimension 维度ID与测评不匹配）");
            req.getRequestDispatcher("/WEB-INF/jsp/test/take.jsp").forward(req, resp);
            return;
          }
          q.setDimensionId(resolved);
        }
        resolvedQuestions.add(q);
      }

      // 防重复：同一人员同一场次，优先复用未完成记录；若已完成则直接查看结果。
      int examId;
      ExamDao.ExamMeta latest = examDao.findLatestByPersonnelAndSchedule(u.getId(), scheduleId);
      if (latest != null) {
        boolean finished = latest.endTime != null && latest.result != null && !latest.result.trim().isEmpty();
        if (finished) {
          resp.sendRedirect(req.getContextPath() + "/test/result?examId=" + latest.id);
          return;
        }
        examId = latest.id;
      } else {
        examId = examDao.createExam(u.getId(), scheduleId);
      }

      // 随机出题并保存到 Session，保证本次测评前后请求使用同一套题。
      resolvedQuestions = resolveExamQuestions(req, examId, dims, resolvedQuestions, schedule.getQuestionNumber(), false);

      req.setAttribute("schedule", schedule);
      req.setAttribute("questions", resolvedQuestions);
      req.setAttribute("examId", examId);
      req.getRequestDispatcher("/WEB-INF/jsp/test/take.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String scheduleIdStr = req.getParameter("scheduleId");
    String examIdStr = req.getParameter("examId");
    String action = req.getParameter("action");
    if (scheduleIdStr == null || examIdStr == null) {
      resp.sendError(400, "missing scheduleId/examId");
      return;
    }

    try {
      int scheduleId = Integer.parseInt(scheduleIdStr);
      int examId = Integer.parseInt(examIdStr);

      User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
      if (u == null || u.getType() != 4) {
        resp.sendError(403, "forbidden");
        return;
      }

      Schedule schedule = scheduleDao.findById(scheduleId);
      if (schedule == null) {
        resp.sendError(404, "schedule not found");
        return;
      }

      Integer teamId = personnelDao.findTeamIdByUserId(u.getId());
      if (teamId == null || schedule.getTeamId() != teamId) {
        resp.sendError(403, "schedule not in your team");
        return;
      }

      Team team = teamDao.findById(teamId);
      if (team == null || team.getStatus() == null || team.getStatus() != 1) {
        resp.sendError(403, "team not active");
        return;
      }

      if (schedule.getStatus() != 2) {
        resp.sendError(403, "schedule not active");
        return;
      }

      LocalDateTime now = LocalDateTime.now();
      if (schedule.getBeginDate() == null || schedule.getEndDate() == null
          || now.isBefore(schedule.getBeginDate()) || now.isAfter(schedule.getEndDate())) {
        resp.sendError(403, "schedule not in time window");
        return;
      }

      ExamDao.ExamMeta meta = examDao.findById(examId);
      if (meta == null) {
        resp.sendError(404, "exam not found");
        return;
      }
      if (meta.personnelId != u.getId() || meta.scheduleId != scheduleId) {
        resp.sendError(403, "exam not owned");
        return;
      }

      boolean finished = meta.endTime != null && meta.result != null && !meta.result.trim().isEmpty();
      if (finished) {
        resp.sendRedirect(req.getContextPath() + "/test/result?examId=" + examId);
        return;
      }

      // 用户选择“不保存退出”：丢弃未完成记录，避免后台统计出现“进行中”残留。
      if ("discard".equalsIgnoreCase(action)) {
        clearExamQuestions(req.getSession(), examId);
        examDao.discardExam(examId, u.getId());
        resp.sendRedirect(req.getContextPath() + "/test/schedules");
        return;
      }

      if (schedule.getAssessmentId() <= 0) {
        resp.sendError(409, "schedule has no assessment");
        return;
      }

      // 交卷必须使用 Session 中固化的题目集合，防止通过构造参数篡改题目范围。
      if (req.getSession().getAttribute(examQuestionKey(examId)) == null) {
        resp.sendRedirect(req.getContextPath() + "/test/take?scheduleId=" + scheduleId);
        return;
      }

      List<Question> questions = questionDao.listByAssessmentWithChoices(schedule.getAssessmentId());

      if (questions.isEmpty()) {
        resp.sendError(409, "no questions");
        return;
      }

      for (Question q : questions) {
        if (q.getChoices() == null || q.getChoices().isEmpty()) {
          resp.sendError(409, "question has no choices");
          return;
        }
      }

      // 维度映射校验/修复：同 doGet
      List<Dimension> dims = dimensionDao.listByAssessment(schedule.getAssessmentId());
      if (dims.size() < 4) {
        resp.sendError(409, "dimensions not complete");
        return;
      }
      Map<String, Integer> dimIdByPairKey = new HashMap<>();
      Set<Integer> dimIds = new HashSet<>();
      for (Dimension d : dims) {
        dimIds.add(d.getId());
        String key = MbtiScoring.pairKey(d.getTitle());
        if (key != null) {
          dimIdByPairKey.put(key, d.getId());
        }
      }

      for (Question q : questions) {
        int dimId = q.getDimensionId();
        if (!dimIds.contains(dimId)) {
          Dimension rawDim = dimensionDao.findById(dimId);
          String key = rawDim == null ? null : MbtiScoring.pairKey(rawDim.getTitle());
          Integer resolved = key == null ? null : dimIdByPairKey.get(key);
          if (resolved == null) {
            resp.sendError(409, "dimension mapping invalid");
            return;
          }
          q.setDimensionId(resolved);
        }
      }

      questions = resolveExamQuestions(req, examId, dims, questions, schedule.getQuestionNumber(), false);

      Map<Integer, Integer> answers = new HashMap<>();
      Map<Integer, Integer> questionChecked = new HashMap<>();
      Map<Integer, Integer> choiceCheckedById = new HashMap<>();
      for (Question q : questions) {
        for (Choice c : q.getChoices()) {
          choiceCheckedById.put(c.getId(), c.getChecked());
        }
      }

      for (int i = 0; i < questions.size(); i++) {
        Question q = questions.get(i);
        String key = "q_" + q.getId();
        String choiceIdStr = req.getParameter(key);
        if (choiceIdStr == null || choiceIdStr.trim().isEmpty()) {
          req.setAttribute("error", "请完成所有题目后再提交");
          req.setAttribute("missingIndex", i);
          req.setAttribute("schedule", schedule);
          req.setAttribute("questions", questions);
          req.setAttribute("examId", examId);
          req.getRequestDispatcher("/WEB-INF/jsp/test/take.jsp").forward(req, resp);
          return;
        }
        int choiceId = Integer.parseInt(choiceIdStr);
        answers.put(q.getId(), choiceId);
        questionChecked.put(q.getId(), choiceCheckedById.getOrDefault(choiceId, 0));
      }

      // 按维度统计
      Map<Integer, Integer> dimTotal = new HashMap<>();
      Map<Integer, Integer> dimScore = new HashMap<>();
      for (Question q : questions) {
        int dimId = q.getDimensionId();
        dimTotal.put(dimId, dimTotal.getOrDefault(dimId, 0) + 1);
        int checked = questionChecked.getOrDefault(q.getId(), 0);
        dimScore.put(dimId, dimScore.getOrDefault(dimId, 0) + checked);
      }

      StringBuilder result = new StringBuilder();
      for (Dimension d : dims) {
        int total = dimTotal.getOrDefault(d.getId(), 0);
        int score = dimScore.getOrDefault(d.getId(), 0);
        result.append(MbtiScoring.chooseLetter(d, score, total));
      }

      examDao.saveAnswersAndResult(examId, u.getId(), answers, questionChecked, result.toString());
      clearExamQuestions(req.getSession(), examId);

      resp.sendRedirect(req.getContextPath() + "/test/result?examId=" + examId);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private List<Question> resolveExamQuestions(HttpServletRequest req, int examId, List<Dimension> dims,
                                              List<Question> sourceQuestions, int targetCount,
                                              boolean allowUseSubmittedIds) {
    HttpSession session = req.getSession();
    Map<Integer, Question> byId = new HashMap<>();
    for (Question q : sourceQuestions) {
      byId.put(q.getId(), q);
    }

    Object saved = session.getAttribute(examQuestionKey(examId));
    if (saved instanceof List) {
      @SuppressWarnings("unchecked")
      List<Integer> ids = (List<Integer>) saved;
      List<Question> restored = restoreQuestionList(ids, byId);
      if (!restored.isEmpty()) {
        return restored;
      }
    }

    if (allowUseSubmittedIds) {
      List<Integer> submittedIds = extractSubmittedQuestionIds(req);
      List<Question> restoredBySubmit = restoreQuestionList(submittedIds, byId);
      if (!restoredBySubmit.isEmpty()) {
        session.setAttribute(examQuestionKey(examId), toIdList(restoredBySubmit));
        return restoredBySubmit;
      }
    }

    List<Question> generated = randomBalanceQuestionsByDimension(dims, sourceQuestions, targetCount);
    session.setAttribute(examQuestionKey(examId), toIdList(generated));
    return generated;
  }

  private String examQuestionKey(int examId) {
    return "test.exam.questions." + examId;
  }

  private void clearExamQuestions(HttpSession session, int examId) {
    session.removeAttribute(examQuestionKey(examId));
  }

  private List<Integer> toIdList(List<Question> questions) {
    List<Integer> ids = new ArrayList<>(questions.size());
    for (Question q : questions) {
      ids.add(q.getId());
    }
    return ids;
  }

  private List<Question> restoreQuestionList(List<Integer> ids, Map<Integer, Question> byId) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    List<Question> out = new ArrayList<>(ids.size());
    for (Integer id : ids) {
      if (id == null) {
        continue;
      }
      Question q = byId.get(id);
      if (q != null) {
        out.add(q);
      }
    }
    return out;
  }

  private List<Integer> extractSubmittedQuestionIds(HttpServletRequest req) {
    List<Integer> ids = new ArrayList<>();

    String idCsv = req.getParameter("questionIds");
    if (idCsv != null && !idCsv.trim().isEmpty()) {
      String[] parts = idCsv.split(",");
      for (String part : parts) {
        String token = part == null ? "" : part.trim();
        if (token.isEmpty()) {
          continue;
        }
        try {
          ids.add(Integer.parseInt(token));
        } catch (NumberFormatException ignore) {
          // Ignore malformed items from untrusted request payload.
        }
      }
      if (!ids.isEmpty()) {
        return ids;
      }
    }

    for (String name : req.getParameterMap().keySet()) {
      if (name == null || !name.startsWith("q_")) {
        continue;
      }
      try {
        ids.add(Integer.parseInt(name.substring(2)));
      } catch (NumberFormatException ignore) {
        // Ignore malformed question parameter names.
      }
    }
    return ids;
  }

  private static List<Question> randomBalanceQuestionsByDimension(List<Dimension> dims, List<Question> questions, int targetCount) {
    List<Question> source = new ArrayList<>(questions);
    if (source.isEmpty()) {
      return source;
    }

    Random random = new Random();
    if (targetCount <= 0 || source.size() <= targetCount) {
      Collections.shuffle(source, random);
      return source;
    }

    // 按维度均衡随机抽题，再随机打散显示顺序。
    Map<Integer, List<Question>> byDim = new HashMap<>();
    for (Dimension d : dims) {
      byDim.put(d.getId(), new ArrayList<>());
    }
    for (Question q : source) {
      List<Question> bucket = byDim.get(q.getDimensionId());
      if (bucket != null) {
        bucket.add(q);
      }
    }
    for (List<Question> bucket : byDim.values()) {
      Collections.shuffle(bucket, random);
    }

    int dimCount = dims.size();
    int base = targetCount / dimCount;
    int extra = targetCount % dimCount;

    List<Question> out = new ArrayList<>(targetCount);
    for (int i = 0; i < dims.size(); i++) {
      Dimension d = dims.get(i);
      List<Question> bucket = byDim.get(d.getId());
      if (bucket == null || bucket.isEmpty()) {
        continue;
      }
      int take = base + (i < extra ? 1 : 0);
      int n = Math.min(take, bucket.size());
      out.addAll(bucket.subList(0, n));
    }

    if (out.size() >= targetCount) {
      List<Question> picked = new ArrayList<>(out.subList(0, targetCount));
      Collections.shuffle(picked, random);
      return picked;
    }

    // 补齐：从剩余题目中随机补到目标数
    Set<Integer> picked = new HashSet<>();
    for (Question q : out) {
      picked.add(q.getId());
    }
    List<Question> remaining = new ArrayList<>();
    for (Question q : source) {
      if (!picked.contains(q.getId())) {
        remaining.add(q);
      }
    }
    Collections.shuffle(remaining, random);
    for (Question q : remaining) {
      if (out.size() >= targetCount) {
        break;
      }
      out.add(q);
      picked.add(q.getId());
    }
    Collections.shuffle(out, random);
    return out;
  }
}
