package com.mbti.web.admin;

import com.mbti.web.dao.AssessmentDao;
import com.mbti.web.dao.DimensionDao;
import com.mbti.web.dao.QuestionDao;
import com.mbti.web.dao.ScheduleDao;
import com.mbti.web.dao.TeamDao;
import com.mbti.web.model.Schedule;
import com.mbti.web.model.Dimension;
import com.mbti.web.model.Question;
import com.mbti.web.model.User;
import com.mbti.web.util.MbtiScoring;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "adminSchedulesServlet", urlPatterns = "/admin/schedules")
public class AdminSchedulesServlet extends HttpServlet {
  private final ScheduleDao scheduleDao = new ScheduleDao();
  private final TeamDao teamDao = new TeamDao();
  private final AssessmentDao assessmentDao = new AssessmentDao();
  private final QuestionDao questionDao = new QuestionDao();
  private final DimensionDao dimensionDao = new DimensionDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String teamIdStr = val(req.getParameter("teamId"));
      Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);
      req.setAttribute("teamId", teamId);

      req.setAttribute("schedules", scheduleDao.listForTeam(teamId));
      req.setAttribute("teams", teamDao.listAll());
      req.setAttribute("assessments", assessmentDao.listAll());

      String idStr = val(req.getParameter("id"));
      if (!idStr.isEmpty()) {
        req.setAttribute("edit", scheduleDao.findById(Integer.parseInt(idStr)));
      }
      req.getRequestDispatcher("/WEB-INF/jsp/admin/schedules.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    try {
      String action = req.getParameter("action");
      if (action == null || action.trim().isEmpty() || "save".equals(action) || "create".equals(action)) {
        User u = (User) req.getSession().getAttribute(Web.SESSION_USER);

        String idStr = val(req.getParameter("id"));
        int teamId = Integer.parseInt(req.getParameter("teamId"));
        int assessmentId = Integer.parseInt(req.getParameter("assessmentId"));
        String begin = req.getParameter("beginDate");
        String end = req.getParameter("endDate");
        int duration = Integer.parseInt(req.getParameter("duration"));
        int questionNumber = Integer.parseInt(req.getParameter("questionNumber"));
        int status = Integer.parseInt(req.getParameter("status"));

        String err = validateTimeRange(begin, end);
        if (err == null) {
          err = validateAssessment(assessmentId, questionNumber, status);
        }
        if (err != null) {
          req.setAttribute("error", err);
          // 保留用户输入，便于修正后再次提交
          Schedule edit = new Schedule();
          if (!idStr.isEmpty()) {
            edit.setId(Integer.parseInt(idStr));
          }
          edit.setTeamId(teamId);
          edit.setAssessmentId(assessmentId);
          edit.setBeginDate(parseDatetimeLocal(begin));
          edit.setEndDate(parseDatetimeLocal(end));
          edit.setDuration(duration);
          edit.setQuestionNumber(questionNumber);
          edit.setStatus(status);
          req.setAttribute("edit", edit);
          req.setAttribute("schedules", scheduleDao.listForTeam(null));
          req.setAttribute("teams", teamDao.listAll());
          req.setAttribute("assessments", assessmentDao.listAll());
          req.getRequestDispatcher("/WEB-INF/jsp/admin/schedules.jsp").forward(req, resp);
          return;
        }

        if (idStr.isEmpty()) {
          scheduleDao.create(teamId, assessmentId, begin, end, duration, questionNumber, status, u.getId());
        } else {
          scheduleDao.update(Integer.parseInt(idStr), teamId, assessmentId, begin, end, duration, questionNumber, status);
        }
      } else if ("delete".equals(action)) {
        int id = Integer.parseInt(req.getParameter("id"));
        scheduleDao.delete(id);
      }

      resp.sendRedirect(req.getContextPath() + "/admin/schedules");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }

  private String validateAssessment(int assessmentId, int questionNumber, int status) throws Exception {
    List<Dimension> dims = dimensionDao.listByAssessment(assessmentId);
    if (dims.isEmpty()) {
      return "该测评尚未创建维度（E/I、S/N、T/F、J/P），无法创建可用场次。请先到“性格维度管理”补齐。";
    }

    Set<String> pairs = new HashSet<>();
    for (Dimension d : dims) {
      String k = MbtiScoring.pairKey(d.getTitle());
      if (k != null) {
        pairs.add(k);
      }
    }
    if (pairs.size() < 4) {
      return "该测评维度不完整（需要4个字母对：E/I、S/N、T/F、J/P），请先到“性格维度管理”补齐。";
    }

    List<Question> questions = questionDao.listByAssessmentWithChoices(assessmentId);
    if (questions.isEmpty()) {
      return "该测评暂无题目，请先到“题目管理”新增题目后再创建场次。";
    }

    int available = 0;
    for (Question q : questions) {
      if (q.getChoices() != null && q.getChoices().size() >= 2) {
        available++;
      }
    }
    if (available < questionNumber) {
      return "该测评可用题目数不足：当前可用 " + available + " 题，但你设置了 " + questionNumber + " 题。请先补题或降低题数。";
    }

    if (status == 2 && (available <= 0 || pairs.size() < 4)) {
      return "该场次状态不能直接设为“进行中”：题库/维度未完善。";
    }
    return null;
  }

  private String validateTimeRange(String begin, String end) {
    LocalDateTime b = parseDatetimeLocal(begin);
    LocalDateTime e = parseDatetimeLocal(end);
    if (b == null || e == null) {
      return "开始时间/结束时间不能为空";
    }
    if (!e.isAfter(b)) {
      return "结束时间早于开始时间（或相等），请调整后再保存。";
    }
    return null;
  }

  private LocalDateTime parseDatetimeLocal(String s) {
    if (s == null) {
      return null;
    }
    String v = s.trim();
    if (v.isEmpty()) {
      return null;
    }
    // 支持 HTML datetime-local：2021-10-12T10:30
    if (v.length() == 16 && v.charAt(10) == 'T') {
      return LocalDateTime.parse(v, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    // 兜底：允许传入带秒的 ISO
    return LocalDateTime.parse(v);
  }
}
