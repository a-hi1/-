package com.mbti.web;

import com.mbti.web.dao.UserDao;
import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "loginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
  private final UserDao userDao = new UserDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String login = req.getParameter("login");
    String passwd = req.getParameter("passwd");
    String userType = req.getParameter("userType"); // "admin" or "personnel"

    req.setCharacterEncoding("UTF-8");
    req.setAttribute("login", login);

    if (login == null || login.trim().isEmpty() || passwd == null || passwd.trim().isEmpty()) {
      req.setAttribute("error", "请输入账号和密码");
      req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
      return;
    }

    try {
      User user = userDao.findByLoginAndPassword(login.trim(), passwd.trim());
      if (user == null) {
        req.setAttribute("error", "账号或密码错误，或账号已禁用");
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        return;
      }

      // 验证用户类型与登录方式匹配
      boolean isAdmin = (user.getType() == 1 || user.getType() == 2 || user.getType() == 3);
      boolean isPersonnel = (user.getType() == 4);

      if ("admin".equals(userType) && !isAdmin) {
        req.setAttribute("error", "该账号不是管理员账号");
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        return;
      }

      if ("personnel".equals(userType) && !isPersonnel) {
        req.setAttribute("error", "该账号不是参测人员账号");
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        return;
      }

      req.getSession().setAttribute(Web.SESSION_USER, user);
      userDao.updateLastLogin(user.getId());

      if (user.getType() == 1) {
        resp.sendRedirect(req.getContextPath() + "/admin/users");
      } else {
        resp.sendRedirect(req.getContextPath() + "/app/home");
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
