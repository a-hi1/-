package com.mbti.web.admin;

import com.mbti.web.dao.TeamDao;
import com.mbti.web.model.Team;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "adminTeamDeleteServlet", urlPatterns = "/admin/teams/delete")
public class AdminTeamDeleteServlet extends HttpServlet {
  private final TeamDao teamDao = new TeamDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String idStr = req.getParameter("id");
      if (idStr == null || idStr.trim().isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams");
        return;
      }

      Team team = teamDao.findById(Integer.parseInt(idStr));
      if (team == null) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams");
        return;
      }

      req.setAttribute("team", team);
      req.getRequestDispatcher("/WEB-INF/jsp/admin/team_delete.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String idStr = req.getParameter("id");
      if (idStr == null || idStr.trim().isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams");
        return;
      }

      int id = Integer.parseInt(idStr);
      Team team = teamDao.findById(id);
      if (team == null) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams");
        return;
      }

      int refs = teamDao.countPersonnelRefs(id);
      if (refs > 0) {
        req.setAttribute("team", team);
        req.setAttribute("error", "该批次下存在 " + refs + " 个参测人员，不能删除");
        req.getRequestDispatcher("/WEB-INF/jsp/admin/team_delete.jsp").forward(req, resp);
        return;
      }

      teamDao.delete(id);
      resp.sendRedirect(req.getContextPath() + "/admin/teams");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
