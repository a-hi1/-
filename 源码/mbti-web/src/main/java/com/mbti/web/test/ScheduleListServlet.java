package com.mbti.web.test;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.ExamDao;
import com.mbti.web.dao.ScheduleDao;
import com.mbti.web.model.Schedule;
import com.mbti.web.model.TestScheduleRow;
import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "scheduleListServlet", urlPatterns = "/test/schedules")
public class ScheduleListServlet extends HttpServlet {
  private final ScheduleDao scheduleDao = new ScheduleDao();
  private final PersonnelDao personnelDao = new PersonnelDao();
  private final ExamDao examDao = new ExamDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
      Integer teamId = personnelDao.findTeamIdByUserId(u.getId());
      if (teamId == null) {
        req.setAttribute("rows", Collections.emptyList());
        req.setAttribute("warning", "你的账号尚未分配到批次，无法参加测评。请联系管理员在“参测人员管理”中分配批次。");
      } else {
        List<Schedule> schedules = scheduleDao.listForTeam(teamId);
        LocalDateTime now = LocalDateTime.now();
        List<TestScheduleRow> rows = new ArrayList<>();
        for (Schedule s : schedules) {
          TestScheduleRow row = new TestScheduleRow();
          row.setScheduleId(s.getId());
          row.setAssessmentTitle(s.getAssessmentTitle());
          row.setBeginDate(s.getBeginDate());
          row.setEndDate(s.getEndDate());
          row.setDuration(s.getDuration());

          ExamDao.ExamMeta latest = examDao.findLatestByPersonnelAndSchedule(u.getId(), s.getId());
          boolean finished = latest != null && latest.endTime != null && latest.result != null && !latest.result.trim().isEmpty();
          boolean inProgress = latest != null && !finished;

          if (finished) {
            row.setResultText(latest.result);
          } else if (inProgress) {
            row.setResultText("进行中");
          } else {
            row.setResultText("未考");
          }
          row.setInProgress(inProgress);

          boolean inTimeWindow = s.getBeginDate() != null && s.getEndDate() != null
              && !now.isBefore(s.getBeginDate()) && !now.isAfter(s.getEndDate());
          row.setCanStart(s.getStatus() == 2 && inTimeWindow && !finished);
          rows.add(row);
        }
        req.setAttribute("rows", rows);
      }
      req.getRequestDispatcher("/WEB-INF/jsp/test/schedules.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
