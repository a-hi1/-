package com.mbti.web.test;

import com.mbti.web.dao.DimensionDao;
import com.mbti.web.dao.ExamDao;
import com.mbti.web.dao.QuestionDao;
import com.mbti.web.model.Dimension;
import com.mbti.web.model.DimensionScore;
import com.mbti.web.model.Question;
import com.mbti.web.model.User;
import com.mbti.web.util.MbtiProfiles;
import com.mbti.web.util.MbtiScoring;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "examResultServlet", urlPatterns = "/test/result")
public class ExamResultServlet extends HttpServlet {
  private final ExamDao examDao = new ExamDao();
  private final DimensionDao dimensionDao = new DimensionDao();
  private final QuestionDao questionDao = new QuestionDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String examIdStr = req.getParameter("examId");
    if (examIdStr == null) {
      resp.sendError(400, "missing examId");
      return;
    }

    try {
      int examId = Integer.parseInt(examIdStr);
      User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
      Integer ownerPersonnelId = examDao.findPersonnelIdByExam(examId);
      if (ownerPersonnelId == null) {
        resp.sendError(404, "exam not found");
        return;
      }

      int personnelIdForView;
      if (u != null && u.getType() != 4) {
        // 管理端角色可查看任意参测人员的结果
        personnelIdForView = ownerPersonnelId;
      } else {
        // 非管理员仅允许查看自己的结果
        if (u == null || ownerPersonnelId != u.getId()) {
          resp.sendError(403, "forbidden");
          return;
        }
        personnelIdForView = u.getId();
      }

      String resultText = examDao.findResultText(examId);
      if (resultText == null || resultText.trim().isEmpty()) {
        req.setAttribute("pending", true);
        req.setAttribute("pendingMessage", "本次评测尚未完成或未提交，暂无结果可查看。");
        req.setAttribute("resultText", "");
        req.setAttribute("mbtiProfile", null);
        req.setAttribute("dimensionScores", new ArrayList<>());
        req.getRequestDispatcher("/WEB-INF/jsp/test/result.jsp").forward(req, resp);
        return;
      }

      Integer assessmentId = examDao.findAssessmentIdByExam(examId);
      List<DimensionScore> scores = new ArrayList<>();

      if (assessmentId != null) {
        List<Dimension> dims = dimensionDao.listByAssessment(assessmentId);
        Map<String, Integer> dimIdByPairKey = new HashMap<>();
        Set<Integer> dimIds = new HashSet<>();
        for (Dimension d : dims) {
          dimIds.add(d.getId());
          String key = MbtiScoring.pairKey(d.getTitle());
          if (key != null) {
            dimIdByPairKey.put(key, d.getId());
          }
        }

        Map<Integer, Question> questionById = new HashMap<>();
        for (Question q : questionDao.listByAssessmentWithChoices(assessmentId)) {
          questionById.put(q.getId(), q);
        }

        Map<Integer, Integer> dimTotal = new HashMap<>();
        Map<Integer, Integer> dimScore = new HashMap<>();
        boolean mappingWarning = false;

        for (ExamDao.QuestionScore qs : examDao.listQuestionScores(examId, personnelIdForView)) {
          Question q = questionById.get(qs.questionId);
          if (q == null) {
            continue;
          }

          int dimId = q.getDimensionId();
          if (!dimIds.contains(dimId)) {
            Dimension rawDim = dimensionDao.findById(dimId);
            String key = rawDim == null ? null : MbtiScoring.pairKey(rawDim.getTitle());
            Integer resolved = key == null ? null : dimIdByPairKey.get(key);
            if (resolved == null) {
              mappingWarning = true;
              continue;
            }
            dimId = resolved;
          }

          dimTotal.put(dimId, dimTotal.getOrDefault(dimId, 0) + 1);
          dimScore.put(dimId, dimScore.getOrDefault(dimId, 0) + qs.score);
        }

        for (Dimension d : dims) {
          DimensionScore ds = new DimensionScore();
          ds.setDimension(d);
          ds.setScore(dimScore.getOrDefault(d.getId(), 0));
          ds.setTotal(dimTotal.getOrDefault(d.getId(), 0));
          ds.setLetter(MbtiScoring.chooseLetter(d, ds.getScore(), ds.getTotal()));
          if (ds.getTotal() == 0) {
            mappingWarning = true;
          }
          scores.add(ds);
        }

        req.setAttribute("mappingWarning", mappingWarning);
      }

      req.setAttribute("resultText", resultText);
      req.setAttribute("mbtiProfile", MbtiProfiles.fromResultText(resultText));
      req.setAttribute("dimensionScores", scores);
      req.getRequestDispatcher("/WEB-INF/jsp/test/result.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
