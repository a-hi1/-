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

@WebServlet(name = "changePasswordServlet", urlPatterns = "/app/password")
public class ChangePasswordServlet extends HttpServlet {
  private final UserDao userDao = new UserDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.getRequestDispatcher("/WEB-INF/jsp/password.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    try {
      User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
      if (u == null) {
        resp.sendRedirect(req.getContextPath() + "/login");
        return;
      }

      String oldPwd = val(req.getParameter("oldPwd"));
      String newPwd = val(req.getParameter("newPwd"));
      String newPwd2 = val(req.getParameter("newPwd2"));

        // 重新查库校验旧密码（兼容旧明文与新哈希），避免 session 中密码不同步
        if (userDao.findByLoginAndPassword(u.getLogin(), oldPwd) == null) {
        req.setAttribute("error", "原密码不正确");
        req.getRequestDispatcher("/WEB-INF/jsp/password.jsp").forward(req, resp);
        return;
      }
      if (newPwd.isEmpty() || newPwd.length() < 4) {
        req.setAttribute("error", "新密码长度至少 4 位");
        req.getRequestDispatcher("/WEB-INF/jsp/password.jsp").forward(req, resp);
        return;
      }
      if (!newPwd.equals(newPwd2)) {
        req.setAttribute("error", "两次输入的新密码不一致");
        req.getRequestDispatcher("/WEB-INF/jsp/password.jsp").forward(req, resp);
        return;
      }

      userDao.updatePassword(u.getId(), newPwd);
      // 刷新 session 内用户信息（避免后续页面显示旧数据）
      User refreshed = userDao.findById(u.getId());
      if (refreshed != null) {
        req.getSession().setAttribute(Web.SESSION_USER, refreshed);
      }
      req.setAttribute("ok", "密码修改成功");
      req.getRequestDispatcher("/WEB-INF/jsp/password.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
