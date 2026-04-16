package com.mbti.web.test;

import com.mbti.web.dao.ExamDao;
import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "examHistoryServlet", urlPatterns = "/test/history")
public class ExamHistoryServlet extends HttpServlet {
  private final ExamDao examDao = new ExamDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
      if (u == null) {
        resp.sendRedirect(req.getContextPath() + "/login");
        return;
      }

      // 管理员查看全员记录走管理端页面
      if (u.getType() != 4) {
        resp.sendRedirect(req.getContextPath() + "/admin/exam-records");
        return;
      }
      req.setAttribute("records", examDao.listByPersonnel(u.getId()));
      req.getRequestDispatcher("/WEB-INF/jsp/test/history.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
