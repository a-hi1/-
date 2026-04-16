package com.mbti.web.admin;

import com.mbti.web.dao.ExamDao;
import com.mbti.web.dao.TeamDao;
import com.mbti.web.util.MbtiProfiles;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "adminExamRecordsServlet", urlPatterns = "/admin/exam-records")
public class AdminExamRecordsServlet extends HttpServlet {
  private final ExamDao examDao = new ExamDao();
  private final TeamDao teamDao = new TeamDao();

  public static class ExamStats {
    public int totalPersonnel;
    public int notStarted;
    public int inProgress;
    public int completed;
    public double completionRate;
    public List<TypeCount> typeDist = new ArrayList<>();
    public List<TypeCount> assessmentDist = new ArrayList<>();

    // JSP EL 通过 JavaBean getter 取值：${stats.total} / ${stats.typeCounts} ...
    public int getTotal() {
      return totalPersonnel;
    }

    public int getNotStarted() {
      return notStarted;
    }

    public int getInProgress() {
      return inProgress;
    }

    public int getCompleted() {
      return completed;
    }

    public double getCompletionRate() {
      return completionRate;
    }

    public List<TypeCount> getTypeCounts() {
      return typeDist;
    }

    public List<TypeCount> getAssessmentCounts() {
      return assessmentDist;
    }
  }

  public static class TypeCount {
    public String key;
    public int count;
    public double pct;

    public TypeCount(String key, int count, double pct) {
      this.key = key;
      this.count = count;
      this.pct = pct;
    }

    // JSP EL 使用 ${t.type} / ${t.count}
    public String getType() {
      return key;
    }

    public int getCount() {
      return count;
    }

    public double getPct() {
      return pct;
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      req.setAttribute("teams", teamDao.listAll());

      String teamIdStr = val(req.getParameter("teamId"));
      String keyword = val(req.getParameter("q"));
      Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);

      String export = val(req.getParameter("export"));
      if ("csv".equalsIgnoreCase(export)) {
        java.util.List<ExamDao.AdminPersonnelExamRow> rows = examDao.listLatestByPersonnel(teamId, keyword);
        writeCsv(resp, rows);
        return;
      }

      req.setAttribute("teamId", teamId);
      req.setAttribute("q", keyword);
      List<ExamDao.AdminPersonnelExamRow> rows = examDao.listLatestByPersonnel(teamId, keyword);
      req.setAttribute("rows", rows);
      req.setAttribute("stats", buildStats(rows));

      req.getRequestDispatcher("/WEB-INF/jsp/admin/exam_records.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private ExamStats buildStats(List<ExamDao.AdminPersonnelExamRow> rows) {
    ExamStats s = new ExamStats();
    s.totalPersonnel = rows == null ? 0 : rows.size();

    Map<String, Integer> typeCounts = new HashMap<>();
    Map<String, Integer> assessmentCounts = new HashMap<>();

    if (rows != null) {
      for (ExamDao.AdminPersonnelExamRow r : rows) {
        if (r.examId == null) {
          s.notStarted++;
          continue;
        }
        if (r.endTime == null) {
          s.inProgress++;
          continue;
        }
        s.completed++;

        if (r.assessmentTitle != null && !r.assessmentTitle.trim().isEmpty()) {
          assessmentCounts.merge(r.assessmentTitle.trim(), 1, Integer::sum);
        }

        String type = MbtiProfiles.extractType(r.result);
        if (type == null || type.trim().isEmpty()) {
          type = "未知";
        }
        typeCounts.merge(type, 1, Integer::sum);
      }
    }

    if (s.totalPersonnel > 0) {
      s.completionRate = (double) s.completed / (double) s.totalPersonnel;
    }

    s.typeDist = toSortedCounts(typeCounts, s.completed);
    // 测评类型分布通常按总人数/筛选范围统计，这里用 totalPersonnel 作为 pct 的分母更合理
    s.assessmentDist = toSortedCounts(assessmentCounts, s.totalPersonnel);
    return s;
  }

  private List<TypeCount> toSortedCounts(Map<String, Integer> counts, int completedTotal) {
    List<Map.Entry<String, Integer>> list = new ArrayList<>(counts.entrySet());
    list.sort(Comparator.comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed()
        .thenComparing(Map.Entry::getKey));

    List<TypeCount> out = new ArrayList<>();
    for (Map.Entry<String, Integer> e : list) {
      int c = e.getValue() == null ? 0 : e.getValue();
      double pct = completedTotal <= 0 ? 0.0 : (double) c / (double) completedTotal;
      out.add(new TypeCount(e.getKey(), c, pct));
    }
    return out;
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }

  private void writeCsv(HttpServletResponse resp, java.util.List<ExamDao.AdminPersonnelExamRow> rows) throws IOException {
    resp.setCharacterEncoding("UTF-8");
    resp.setContentType("text/csv; charset=UTF-8");
    resp.setHeader("Content-Disposition", "attachment; filename=exam_records.csv");

    try (java.io.PrintWriter w = resp.getWriter()) {
      // Excel 兼容 UTF-8
      w.write("\uFEFF");
      w.println("人员ID,登录名,姓名,批次,测评类型,开始日期,结束日期,结果");
      for (ExamDao.AdminPersonnelExamRow r : rows) {
        w.print(csv(r.personnelId));
        w.print(',');
        w.print(csv(r.login));
        w.print(',');
        w.print(csv(r.name));
        w.print(',');
        w.print(csv(r.teamName));
        w.print(',');
        w.print(csv(r.assessmentTitle));
        w.print(',');
        w.print(csv(r.beginTime == null ? "" : r.beginTime.toString()));
        w.print(',');
        w.print(csv(r.endTime == null ? "" : r.endTime.toString()));
        w.print(',');
        w.print(csv(r.result));
        w.println();
      }
    }
  }

  private String csv(Object v) {
    String s = v == null ? "" : String.valueOf(v);
    String escaped = s.replace("\"", "\"\"");
    return "\"" + escaped + "\"";
  }
}
