package com.mbti.web.admin;

import com.mbti.web.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "adminUsersServlet", urlPatterns = "/admin/users")
public class AdminUsersServlet extends HttpServlet {
  private final UserDao userDao = new UserDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String idStr = val(req.getParameter("id")); // view
      if (!idStr.isEmpty()) {
        int id = Integer.parseInt(idStr);
        req.setAttribute("u", userDao.findById(id));
        req.getRequestDispatcher("/WEB-INF/jsp/admin/user_view.jsp").forward(req, resp);
        return;
      }

      String editIdStr = val(req.getParameter("editId"));
      if (!editIdStr.isEmpty()) {
        req.setAttribute("edit", userDao.findById(Integer.parseInt(editIdStr)));
      }

      req.setAttribute("users", userDao.listAll());
      req.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(req, resp);
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
        String login = val(req.getParameter("login"));
        String name = val(req.getParameter("name"));
        String passwd = val(req.getParameter("passwd"));
        int type = Integer.parseInt(val(req.getParameter("type")));
        int status = Integer.parseInt(val(req.getParameter("status")));

        if (idStr.isEmpty()) {
          // 新建用户必须设置密码
          if (passwd.isEmpty()) {
            passwd = "123456";
          }
          userDao.create(login, name, passwd, type, status);
        } else {
          int id = Integer.parseInt(idStr);
          userDao.updateBasic(id, login, name, type, status);
          if (!passwd.isEmpty()) {
            userDao.updatePassword(id, passwd);
          }
        }
      } else if ("resetPwd".equals(action)) {
        int id = Integer.parseInt(req.getParameter("id"));
        userDao.updatePassword(id, "123456");
      } else if ("delete".equals(action)) {
        int id = Integer.parseInt(req.getParameter("id"));
        userDao.delete(id);
      }

      resp.sendRedirect(req.getContextPath() + "/admin/users");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
