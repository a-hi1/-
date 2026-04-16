package com.mbti.web;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.TeamDao;
import com.mbti.web.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "registerServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {
  private final UserDao userDao = new UserDao();
  private final PersonnelDao personnelDao = new PersonnelDao();
  private final TeamDao teamDao = new TeamDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      req.setAttribute("teams", teamDao.listAll());
      req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String login = val(req.getParameter("login"));
    String name = val(req.getParameter("name"));
    String passwd = val(req.getParameter("passwd"));
    String passwd2 = val(req.getParameter("passwd2"));
    String phone = val(req.getParameter("phone"));
    String gender = val(req.getParameter("gender"));
    String birthdateStr = val(req.getParameter("birthdate"));
    String email = val(req.getParameter("email"));
    String teamIdStr = val(req.getParameter("teamId"));

    try {
      req.setAttribute("login", login);
      req.setAttribute("name", name);
      req.setAttribute("gender", gender);
      req.setAttribute("teamId", teamIdStr);

      if (login.isEmpty() || name.isEmpty() || passwd.isEmpty() || passwd2.isEmpty()) {
        req.setAttribute("error", "请填写手机号、密码、确认密码、姓名");
        req.setAttribute("teams", teamDao.listAll());
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        return;
      }

      if (!passwd.equals(passwd2)) {
        req.setAttribute("error", "两次输入的密码不一致");
        req.setAttribute("teams", teamDao.listAll());
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        return;
      }

      if (teamIdStr.isEmpty()) {
        req.setAttribute("error", "请选择批次");
        req.setAttribute("teams", teamDao.listAll());
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        return;
      }

      if (userDao.findByLogin(login) != null) {
        req.setAttribute("error", "该登录名已存在，请更换");
        req.setAttribute("teams", teamDao.listAll());
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        return;
      }

      Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);
      LocalDate birthdate = birthdateStr.isEmpty() ? null : LocalDate.parse(birthdateStr);

      int userId = userDao.create(login, name, passwd, 4, 1);
      String finalPhone = phone.isEmpty() ? login : phone;
      personnelDao.upsertPersonnel(userId, finalPhone, gender, birthdate, email, teamId);

      resp.sendRedirect(req.getContextPath() + "/login?login=" + java.net.URLEncoder.encode(login, "UTF-8"));
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
