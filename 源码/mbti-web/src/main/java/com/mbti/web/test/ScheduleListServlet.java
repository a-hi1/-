package com.mbti.web.test;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.ScheduleDao;
import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "scheduleListServlet", urlPatterns = "/test/schedules")
public class ScheduleListServlet extends HttpServlet {
  private final ScheduleDao scheduleDao = new ScheduleDao();
  private final PersonnelDao personnelDao = new PersonnelDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
      Integer teamId = personnelDao.findTeamIdByUserId(u.getId());
      if (teamId == null) {
        req.setAttribute("schedules", java.util.Collections.emptyList());
        req.setAttribute("warning", "你的账号尚未分配到批次，无法参加测评。请联系管理员在“参测人员管理”中分配批次。");
      } else {
        req.setAttribute("schedules", scheduleDao.listAvailableForTeam(teamId));
      }
      req.getRequestDispatcher("/WEB-INF/jsp/test/schedules.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
