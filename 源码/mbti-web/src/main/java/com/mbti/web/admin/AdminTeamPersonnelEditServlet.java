package com.mbti.web.admin;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.UserDao;
import com.mbti.web.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "adminTeamPersonnelEditServlet", urlPatterns = "/admin/teams/personnel/edit")
public class AdminTeamPersonnelEditServlet extends HttpServlet {
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
      req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_edit.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    try {
      String idStr = val(req.getParameter("id"));
      String teamIdStr = val(req.getParameter("teamId"));
      String name = val(req.getParameter("name"));
      String phone = val(req.getParameter("phone"));
      String gender = val(req.getParameter("gender"));
      String birthdateStr = val(req.getParameter("birthdate"));
      String statusStr = val(req.getParameter("status"));

      String err = null;
      if (idStr.isEmpty() || teamIdStr.isEmpty()) {
        err = "参数不完整";
      } else if (name.isEmpty()) {
        err = "姓名不能为空";
      } else if (phone.isEmpty()) {
        err = "手机号不能为空";
      } else if (!("M".equals(gender) || "F".equals(gender))) {
        err = "性别参数不合法";
      } else if (!("1".equals(statusStr) || "2".equals(statusStr))) {
        err = "状态参数不合法";
      }

      int id = idStr.isEmpty() ? 0 : Integer.parseInt(idStr);
      int teamId = teamIdStr.isEmpty() ? 0 : Integer.parseInt(teamIdStr);
      PersonnelDao.PersonnelRow existing = (id > 0 && teamId > 0) ? personnelDao.findByIdAndTeamId(id, teamId) : null;
      if (err == null && existing == null) {
        err = "参测人员不存在";
      }

      LocalDate birthdate = null;
      if (err == null && !birthdateStr.isEmpty()) {
        birthdate = LocalDate.parse(birthdateStr);
      }

      if (err != null) {
        PersonnelDao.PersonnelRow p = new PersonnelDao.PersonnelRow();
        p.id = id;
        p.teamId = teamId;
        p.name = name;
        p.phone = phone;
        p.gender = gender;
        if (!birthdateStr.isEmpty()) {
          p.birthdate = LocalDate.parse(birthdateStr);
        }
        if (!statusStr.isEmpty()) {
          p.status = Integer.parseInt(statusStr);
        }
        req.setAttribute("p", p);
        req.setAttribute("teamId", teamId);
        req.setAttribute("error", err);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_edit.jsp").forward(req, resp);
        return;
      }

      User u = userDao.findById(id);
      if (u != null) {
        userDao.updateBasic(id, phone, name, 4, Integer.parseInt(statusStr));
      }
      personnelDao.updateByUserIdAndTeamId(id, teamId, phone, gender, birthdate);
      resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel?teamId=" + teamId);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
