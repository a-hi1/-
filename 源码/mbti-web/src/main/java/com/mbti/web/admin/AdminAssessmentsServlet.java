package com.mbti.web.admin;

import com.mbti.web.dao.AssessmentDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "adminAssessmentsServlet", urlPatterns = "/admin/assessments")
public class AdminAssessmentsServlet extends HttpServlet {
  private final AssessmentDao assessmentDao = new AssessmentDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String idStr = req.getParameter("id");
      if (idStr != null && !idStr.trim().isEmpty()) {
        req.setAttribute("edit", assessmentDao.findById(Integer.parseInt(idStr)));
      }
      req.setAttribute("assessments", assessmentDao.listAll());
      req.getRequestDispatcher("/WEB-INF/jsp/admin/assessments.jsp").forward(req, resp);
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
        String title = val(req.getParameter("title"));
        double cost = Double.parseDouble(val(req.getParameter("cost")));
        int status = Integer.parseInt(val(req.getParameter("status")));

        if (idStr.isEmpty()) {
          assessmentDao.create(title, cost, status);
        } else {
          assessmentDao.update(Integer.parseInt(idStr), title, cost, status);
        }
      } else if ("delete".equals(action)) {
        int id = Integer.parseInt(req.getParameter("id"));
        assessmentDao.delete(id);
      }

      resp.sendRedirect(req.getContextPath() + "/admin/assessments");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
