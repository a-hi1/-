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

      String idStr = req.getParameter("id");
      if (idStr != null && !idStr.trim().isEmpty()) {
        Dimension edit = dimensionDao.findById(Integer.parseInt(idStr));
        if (edit != null) {
          // 编辑时以记录所属的考核类型为准，确保“关联考核类型”一致。
          assessmentId = edit.getAssessmentId();
          req.setAttribute("edit", edit);
        }
      }

      if (assessmentId != null) {
        List<Dimension> dims = dimensionDao.listByAssessment(assessmentId);
        req.setAttribute("assessmentId", assessmentId);
        req.setAttribute("dimensions", dims);
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
        if (assessmentIdStr.isEmpty()) {
          req.setAttribute("error", "请先选择考核类型");
          doGet(req, resp);
          return;
        }
        int assessmentId = Integer.parseInt(assessmentIdStr);
        String idStr = val(req.getParameter("id"));
        String title = val(req.getParameter("title"));
        String depict = val(req.getParameter("depict"));
        if (title.isEmpty()) {
          req.setAttribute("error", "维度名称不能为空");
          doGet(req, resp);
          return;
        }

        Integer currentId = idStr.isEmpty() ? null : Integer.parseInt(idStr);
        if (currentId != null) {
          Dimension current = dimensionDao.findById(currentId);
          if (current == null) {
            req.setAttribute("error", "要修改的维度不存在");
            doGet(req, resp);
            return;
          }
        }

        // 校验性格维度是否存在（对应任务难点）
        Integer existingId = dimensionDao.findIdByAssessmentAndTitle(assessmentId, title);
        if (existingId != null && (currentId == null || existingId.intValue() != currentId.intValue())) {
          req.setAttribute("error", "该性格维度已存在，不可重复添加！");
          doGet(req, resp);
          return;
        }

        if (currentId == null) {
          dimensionDao.create(assessmentId, title, depict);
        } else {
          dimensionDao.update(currentId, assessmentId, title, depict);
        }
      } else if ("delete".equals(action)) {
        int id = Integer.parseInt(req.getParameter("id"));

        Dimension current = dimensionDao.findById(id);
        if (current == null) {
          req.setAttribute("error", "要删除的维度不存在");
          doGet(req, resp);
          return;
        }

        int refCount = dimensionDao.countQuestionRefs(id);
        if (refCount > 0) {
          req.setAttribute("error", "该维度已被题目引用，不能删除");
          doGet(req, resp);
          return;
        }

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
