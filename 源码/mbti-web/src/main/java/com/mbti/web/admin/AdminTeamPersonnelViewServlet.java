package com.mbti.web.admin;

import com.mbti.web.dao.PersonnelDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "adminTeamPersonnelViewServlet", urlPatterns = "/admin/teams/personnel/view")
public class AdminTeamPersonnelViewServlet extends HttpServlet {
  private final PersonnelDao personnelDao = new PersonnelDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String idStr = val(req.getParameter("id"));
      String teamIdStr = val(req.getParameter("teamId"));
      if (idStr.isEmpty() || teamIdStr.isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel");
        return;
      }

      int id = Integer.parseInt(idStr);
      int teamId = Integer.parseInt(teamIdStr);
      PersonnelDao.PersonnelRow p = personnelDao.findByUserIdAndTeamId(id, teamId);
      if (p == null) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel?teamId=" + teamId);
        return;
      }

      req.setAttribute("p", p);
      req.setAttribute("teamId", teamId);
      req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_view.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
