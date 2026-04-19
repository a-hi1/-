package com.mbti.web.admin;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.TeamDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "adminTeamPersonnelListServlet", urlPatterns = "/admin/teams/personnel")
public class AdminTeamPersonnelListServlet extends HttpServlet {
  private final TeamDao teamDao = new TeamDao();
  private final PersonnelDao personnelDao = new PersonnelDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      req.setAttribute("teams", teamDao.listAll());

      String teamIdStr = val(req.getParameter("teamId"));
      String nameKeyword = val(req.getParameter("name"));
      String phoneKeyword = val(req.getParameter("phone"));
      Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);

      req.setAttribute("teamId", teamId);
      req.setAttribute("name", nameKeyword);
      req.setAttribute("phone", phoneKeyword);

      if (teamId != null && nameKeyword.isEmpty() && phoneKeyword.isEmpty()) {
        req.setAttribute("personnel", personnelDao.findByTeamId(teamId));
      } else {
        req.setAttribute("personnel", personnelDao.listFiltered(teamId, nameKeyword, phoneKeyword));
      }

      req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_list.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
