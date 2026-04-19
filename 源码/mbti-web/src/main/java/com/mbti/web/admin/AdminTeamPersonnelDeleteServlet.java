package com.mbti.web.admin;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "adminTeamPersonnelDeleteServlet", urlPatterns = "/admin/teams/personnel/delete")
public class AdminTeamPersonnelDeleteServlet extends HttpServlet {
  private final PersonnelDao personnelDao = new PersonnelDao();
  private final UserDao userDao = new UserDao();

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
      PersonnelDao.PersonnelRow p = personnelDao.findByIdAndTeamId(id, teamId);
      if (p == null) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel?teamId=" + teamId);
        return;
      }

      req.setAttribute("p", p);
      req.setAttribute("teamId", teamId);
      req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_delete.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String idStr = val(req.getParameter("id"));
      String teamIdStr = val(req.getParameter("teamId"));
      if (idStr.isEmpty() || teamIdStr.isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel");
        return;
      }
      int id = Integer.parseInt(idStr);
      int teamId = Integer.parseInt(teamIdStr);
      PersonnelDao.PersonnelRow p = personnelDao.findByIdAndTeamId(id, teamId);
      if (p == null) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel?teamId=" + teamId);
        return;
      }

      personnelDao.deleteByUserIdAndTeamId(id, teamId);
      userDao.delete(id);
      resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel?teamId=" + teamId);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
