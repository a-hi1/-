package com.mbti.web.admin;

import com.mbti.web.dao.TeamDao;
import com.mbti.web.model.Team;
import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "adminTeamsServlet", urlPatterns = "/admin/teams")
public class AdminTeamsServlet extends HttpServlet {
  private final TeamDao teamDao = new TeamDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String idStr = req.getParameter("id");
      if (idStr != null && !idStr.trim().isEmpty()) {
        req.setAttribute("edit", teamDao.findById(Integer.parseInt(idStr)));
      }
      List<Team> teams = teamDao.listAll();
      req.setAttribute("teams", teams);
      req.getRequestDispatcher("/WEB-INF/jsp/admin/teams.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String action = val(req.getParameter("action"));
    try {
      if ("save".equals(action)) {
        String idStr = val(req.getParameter("id"));
        String name = val(req.getParameter("name"));
        String beginYearStr = val(req.getParameter("beginYear"));
        String statusStr = val(req.getParameter("status"));

        LocalDate beginYear = beginYearStr.isEmpty() ? null : LocalDate.parse(beginYearStr);
        Integer status = statusStr.isEmpty() ? null : Integer.parseInt(statusStr);

        if (idStr.isEmpty()) {
          User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
          Integer creatorId = u == null ? null : u.getId();
          teamDao.create(name, beginYear, status, creatorId);
        } else {
          int id = Integer.parseInt(idStr);
          teamDao.update(id, name, beginYear, status);
        }
      } else if ("delete".equals(action)) {
        int id = Integer.parseInt(req.getParameter("id"));
        teamDao.delete(id);
      }

      resp.sendRedirect(req.getContextPath() + "/admin/teams");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
