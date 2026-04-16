package com.mbti.web.admin;

import com.mbti.web.dao.AssessmentDao;
import com.mbti.web.dao.DimensionDao;
import com.mbti.web.dao.QuestionDao;
import com.mbti.web.model.Assessment;
import com.mbti.web.model.Dimension;
import com.mbti.web.model.Question;
import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "adminQuestionsServlet", urlPatterns = "/admin/questions")
public class AdminQuestionsServlet extends HttpServlet {
  private final AssessmentDao assessmentDao = new AssessmentDao();
  private final QuestionDao questionDao = new QuestionDao();
  private final DimensionDao dimensionDao = new DimensionDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String assessmentIdStr = req.getParameter("assessmentId");
    try {
      List<Assessment> assessments = assessmentDao.listAll();
      req.setAttribute("assessments", assessments);

      String qidStr = val(req.getParameter("id"));
      if (!qidStr.isEmpty()) {
        Question edit = questionDao.findByIdWithChoices(Integer.parseInt(qidStr));
        req.setAttribute("edit", edit);
        if (edit != null && edit.getChoices() != null) {
          int idx = 1;
          for (int i = 0; i < edit.getChoices().size(); i++) {
            if (edit.getChoices().get(i).getChecked() == 1) {
              idx = i + 1;
              break;
            }
          }
          req.setAttribute("correctIndex", idx);
          // 优先使用题目自身所属测评
          if (assessmentIdStr == null || assessmentIdStr.trim().isEmpty()) {
            assessmentIdStr = String.valueOf(edit.getAssessmentId());
          }
        }
      }

      Integer assessmentId = null;
      if (assessmentIdStr != null && !assessmentIdStr.trim().isEmpty()) {
        assessmentId = Integer.parseInt(assessmentIdStr);
      } else if (!assessments.isEmpty()) {
        assessmentId = assessments.get(0).getId();
      }

      if (assessmentId != null) {
        List<Question> questions = questionDao.listByAssessmentWithChoices(assessmentId);
        Map<Integer, String> dimTitleById = new HashMap<>();
        List<Dimension> dims = dimensionDao.listByAssessment(assessmentId);
        for (Dimension d : dims) {
          dimTitleById.put(d.getId(), d.getTitle());
        }

        // 兼容历史数据：题目可能错误关联到其他测评的维度（会导致参测端结果异常）
        for (Question q : questions) {
          if (!dimTitleById.containsKey(q.getDimensionId())) {
            Dimension other = dimensionDao.findById(q.getDimensionId());
            if (other != null) {
              dimTitleById.put(other.getId(), other.getTitle() + "（不属于当前测评）");
            }
          }
        }

        req.setAttribute("assessmentId", assessmentId);
        req.setAttribute("questions", questions);
        req.setAttribute("dimTitleById", dimTitleById);
        req.setAttribute("dimensions", dims);
      }
      req.getRequestDispatcher("/WEB-INF/jsp/admin/questions.jsp").forward(req, resp);
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
      if ("save".equals(action) || "create".equals(action)) {
        User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
        int assessmentId = Integer.parseInt(assessmentIdStr);
        String idStr = val(req.getParameter("id"));
        int dimensionId = Integer.parseInt(val(req.getParameter("dimensionId")));
        String title = val(req.getParameter("title"));
        String a = val(req.getParameter("choiceA"));
        String b = val(req.getParameter("choiceB"));
        int correct = Integer.parseInt(val(req.getParameter("correct")));

        if (idStr.isEmpty()) {
          questionDao.createTwoChoiceQuestion(assessmentId, dimensionId, 1, 1, 2, u.getId(), title, a, b, correct);
        } else {
          questionDao.updateTwoChoiceQuestion(Integer.parseInt(idStr), assessmentId, dimensionId, title, a, b, correct);
        }
      } else if ("delete".equals(action)) {
        int qid = Integer.parseInt(req.getParameter("id"));
        questionDao.deleteQuestion(qid);
      }

      resp.sendRedirect(req.getContextPath() + "/admin/questions?assessmentId=" + assessmentIdStr);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
