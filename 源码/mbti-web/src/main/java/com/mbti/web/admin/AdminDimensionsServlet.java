package com.mbti.web.admin;

import com.mbti.web.dao.AssessmentDao;
import com.mbti.web.dao.DimensionDao;
import com.mbti.web.model.Assessment;
import com.mbti.web.model.Dimension;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "adminDimensionsServlet", urlPatterns = "/admin/dimensions")
public class AdminDimensionsServlet extends HttpServlet {
  private final AssessmentDao assessmentDao = new AssessmentDao();
  private final DimensionDao dimensionDao = new DimensionDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      List<Assessment> assessments = assessmentDao.listAll();
      req.setAttribute("assessments", assessments);

      String assessmentIdStr = req.getParameter("assessmentId");
      Integer assessmentId = null;
      if (assessmentIdStr != null && !assessmentIdStr.trim().isEmpty()) {
        assessmentId = Integer.parseInt(assessmentIdStr);
      } else if (!assessments.isEmpty()) {
        assessmentId = assessments.get(0).getId();
      }

      if (assessmentId != null) {
        List<Dimension> dims = dimensionDao.listByAssessment(assessmentId);
        req.setAttribute("assessmentId", assessmentId);
        req.setAttribute("dimensions", dims);
      }

      String idStr = req.getParameter("id");
      if (idStr != null && !idStr.trim().isEmpty()) {
        req.setAttribute("edit", dimensionDao.findById(Integer.parseInt(idStr)));
      }

      req.getRequestDispatcher("/WEB-INF/jsp/admin/dimensions.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String action = val(req.getParameter("action"));
    String assessmentIdStr = val(req.getParameter("assessmentId"));
    try {
      if ("save".equals(action)) {
        int assessmentId = Integer.parseInt(assessmentIdStr);
        String idStr = val(req.getParameter("id"));
        String title = val(req.getParameter("title"));
        String depict = val(req.getParameter("depict"));
        if (idStr.isEmpty()) {
          dimensionDao.create(assessmentId, title, depict);
        } else {
          dimensionDao.update(Integer.parseInt(idStr), title, depict);
        }
      } else if ("delete".equals(action)) {
        int id = Integer.parseInt(req.getParameter("id"));
        dimensionDao.delete(id);
      }

      resp.sendRedirect(req.getContextPath() + "/admin/dimensions?assessmentId=" + assessmentIdStr);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
