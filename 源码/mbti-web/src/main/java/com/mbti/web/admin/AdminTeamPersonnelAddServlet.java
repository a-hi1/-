package com.mbti.web.admin;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.TeamDao;
import com.mbti.web.model.Team;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "adminTeamPersonnelAddServlet", urlPatterns = "/admin/teams/personnel/add")
public class AdminTeamPersonnelAddServlet extends HttpServlet {
  private final TeamDao teamDao = new TeamDao();
  private final PersonnelDao personnelDao = new PersonnelDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String teamIdStr = val(req.getParameter("teamId"));
      if (teamIdStr.isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel");
        return;
      }
      int teamId = Integer.parseInt(teamIdStr);
      Team team = teamDao.findById(teamId);
      if (team == null) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel");
        return;
      }

      req.setAttribute("team", team);
      req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_add.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    try {
      String teamIdStr = val(req.getParameter("teamId"));
      String name = val(req.getParameter("name"));
      String phone = val(req.getParameter("phone"));
      String gender = val(req.getParameter("gender"));
      String birthdateStr = val(req.getParameter("birthdate"));

      String err = null;
      if (teamIdStr.isEmpty()) {
        err = "考核类型不能为空";
      } else if (name.isEmpty()) {
        err = "姓名不能为空";
      } else if (phone.isEmpty()) {
        err = "手机号不能为空";
      } else if (!("M".equals(gender) || "F".equals(gender))) {
        err = "性别参数不合法";
      }

      int teamId = teamIdStr.isEmpty() ? 0 : Integer.parseInt(teamIdStr);
      Team team = teamId > 0 ? teamDao.findById(teamId) : null;
      if (err == null && team == null) {
        err = "考核类型不存在";
      }

      LocalDate birthdate = null;
      if (err == null && !birthdateStr.isEmpty()) {
        birthdate = LocalDate.parse(birthdateStr);
      }

      if (err != null) {
        req.setAttribute("error", err);
        req.setAttribute("team", team);
        req.setAttribute("name", name);
        req.setAttribute("phone", phone);
        req.setAttribute("gender", gender);
        req.setAttribute("birthdate", birthdateStr);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_add.jsp").forward(req, resp);
        return;
      }

      personnelDao.addTestPersonnel(name, teamId, phone, gender, birthdate);
      resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel?teamId=" + teamId);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
