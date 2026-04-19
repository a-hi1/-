package com.mbti.web.admin;

import com.mbti.web.dao.AssessmentDao;
import com.mbti.web.dao.DimensionDao;
import com.mbti.web.dao.QuestionDao;
import com.mbti.web.model.Assessment;
import com.mbti.web.model.Dimension;
import com.mbti.web.model.Question;
import com.mbti.web.model.QuestionQuery;
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
    String statusStr = val(req.getParameter("status"));
    String dimensionIdStr = val(req.getParameter("dimensionId"));
    String viewStr = val(req.getParameter("view"));
    try {
      List<Assessment> assessments = assessmentDao.listAll();
      req.setAttribute("assessments", assessments);

      String qidStr = val(req.getParameter("id"));
      if ("1".equals(viewStr) && !qidStr.isEmpty()) {
        Question question = questionDao.findById(Integer.parseInt(qidStr));
        if (question == null) {
          resp.sendRedirect(req.getContextPath() + "/admin/questions");
          return;
        }
        Dimension d = question.getDimensionId() > 0 ? dimensionDao.findById(question.getDimensionId()) : null;
        req.setAttribute("q", question);
        req.setAttribute("dimension", d);
        req.setAttribute("assessmentId", question.getAssessmentId());
        req.setAttribute("status", statusStr.isEmpty() ? 2 : Integer.parseInt(statusStr));
        req.setAttribute("dimensionId", dimensionIdStr.isEmpty() ? null : Integer.parseInt(dimensionIdStr));
        req.getRequestDispatcher("/WEB-INF/jsp/admin/question_view.jsp").forward(req, resp);
        return;
      }

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
        List<Dimension> dims = dimensionDao.listByAssessment(assessmentId);
        Integer selectedDimensionId = null;
        if (!dimensionIdStr.isEmpty()) {
          selectedDimensionId = Integer.parseInt(dimensionIdStr);
        }
        Integer selectedStatus = statusStr.isEmpty() ? 2 : Integer.parseInt(statusStr);

        QuestionQuery query = new QuestionQuery();
        query.setAssessmentId(assessmentId);
        query.setStatus(selectedStatus);
        query.setDimensionId(selectedDimensionId);

        List<Question> questions = questionDao.findByCondition(query);
        Map<Integer, String> dimTitleById = new HashMap<>();
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
        req.setAttribute("status", selectedStatus);
        req.setAttribute("dimensionId", selectedDimensionId);
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
    String statusStr = val(req.getParameter("status"));
    String dimensionIdStr = val(req.getParameter("dimensionId"));
    String viewStr = val(req.getParameter("view"));
    try {
      if ("save".equals(action) || "create".equals(action)) {
        if (assessmentIdStr.isEmpty()) {
          req.setAttribute("error", "请选择考核类型");
          doGet(req, resp);
          return;
        }
        Integer assessmentIdObj = parseInt(assessmentIdStr);
        if (assessmentIdObj == null) {
          req.setAttribute("error", "考核类型参数不合法");
          doGet(req, resp);
          return;
        }
        int assessmentId = assessmentIdObj;
        Assessment assessment = assessmentDao.findById(assessmentId);
        if (assessment == null) {
          req.setAttribute("error", "考核类型不存在");
          doGet(req, resp);
          return;
        }

        User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
        if (u == null) {
          resp.sendRedirect(req.getContextPath() + "/login");
          return;
        }

        String idStr = val(req.getParameter("id"));
        Integer dimensionIdObj = parseInt(val(req.getParameter("dimensionId")));
        if (dimensionIdObj == null) {
          req.setAttribute("error", "请选择性格维度");
          doGet(req, resp);
          return;
        }
        int dimensionId = dimensionIdObj;
        Dimension dim = dimensionDao.findById(dimensionId);
        if (dim == null || dim.getAssessmentId() != assessmentId) {
          req.setAttribute("error", "所选性格维度不属于当前考核类型");
          doGet(req, resp);
          return;
        }

        Integer qid = null;
        if (!idStr.isEmpty()) {
          qid = parseInt(idStr);
          if (qid == null) {
            req.setAttribute("error", "题目ID参数不合法");
            doGet(req, resp);
            return;
          }
          Question current = questionDao.findById(qid);
          if (current == null) {
            req.setAttribute("error", "要修改的题目不存在");
            doGet(req, resp);
            return;
          }
          if (current.getAssessmentId() != assessmentId) {
            req.setAttribute("error", "题目不属于当前考核类型，不能跨类型修改");
            doGet(req, resp);
            return;
          }
        }

        String title = val(req.getParameter("title"));
        String a = val(req.getParameter("choiceA"));
        String b = val(req.getParameter("choiceB"));
        if (title.isEmpty() || a.isEmpty() || b.isEmpty()) {
          req.setAttribute("error", "题目和选项不能为空");
          doGet(req, resp);
          return;
        }
        if (a.equals(b)) {
          req.setAttribute("error", "选项A和选项B不能相同");
          doGet(req, resp);
          return;
        }

        Integer correctObj = parseInt(val(req.getParameter("correct")));
        if (correctObj == null) {
          req.setAttribute("error", "正确项参数不合法");
          doGet(req, resp);
          return;
        }
        int correct = correctObj;
        if (correct != 1 && correct != 2) {
          req.setAttribute("error", "正确项参数不合法");
          doGet(req, resp);
          return;
        }

        if (qid == null) {
          questionDao.createTwoChoiceQuestion(assessmentId, dimensionId, 1, 1, 2, u.getId(), title, a, b, correct);
        } else {
          questionDao.updateTwoChoiceQuestion(qid, assessmentId, dimensionId, title, a, b, correct);
        }
      } else if ("delete".equals(action)) {
        Integer qid = parseInt(req.getParameter("id"));
        if (qid == null) {
          req.setAttribute("error", "题目ID参数不合法");
          doGet(req, resp);
          return;
        }

        Question current = questionDao.findById(qid);
        if (current == null) {
          req.setAttribute("error", "要删除的题目不存在");
          doGet(req, resp);
          return;
        }

        int usage = questionDao.countUsage(qid);
        if (usage > 0) {
          req.setAttribute("error", "该题目已被使用，不能删除");
          doGet(req, resp);
          return;
        }

        questionDao.deleteQuestion(qid);
      }

      StringBuilder redirect = new StringBuilder(req.getContextPath())
        .append("/admin/questions?assessmentId=")
        .append(assessmentIdStr);
      if (!statusStr.isEmpty()) {
        redirect.append("&status=").append(statusStr);
      }
      if (!dimensionIdStr.isEmpty()) {
        redirect.append("&dimensionId=").append(dimensionIdStr);
      }
      if (!viewStr.isEmpty()) {
        redirect.append("&view=").append(viewStr);
      }
      resp.sendRedirect(redirect.toString());
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }

  private Integer parseInt(String s) {
    try {
      return Integer.parseInt(val(s));
    } catch (Exception e) {
      return null;
    }
  }
}
