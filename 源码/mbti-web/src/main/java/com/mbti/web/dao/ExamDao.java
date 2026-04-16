package com.mbti.web.dao;

import com.mbti.web.model.ExamRecord;
import com.mbti.web.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExamDao {
  public static class ExamMeta {
    public int id;
    public int personnelId;
    public int scheduleId;
    public java.time.LocalDate beginTime;
    public java.time.LocalDate endTime;
    public String result;
  }

  public static class AdminPersonnelExamRow {
    public int personnelId;
    public String login;
    public String name;
    public Integer teamId;
    public String teamName;
    public Integer examId;
    public Integer scheduleId;
    public String assessmentTitle;
    public java.time.LocalDate beginTime;
    public java.time.LocalDate endTime;
    public String result;

    public int getPersonnelId() {
      return personnelId;
    }

    public String getLogin() {
      return login;
    }

    public String getName() {
      return name;
    }

    public Integer getTeamId() {
      return teamId;
    }

    public String getTeamName() {
      return teamName;
    }

    public Integer getExamId() {
      return examId;
    }

    public Integer getScheduleId() {
      return scheduleId;
    }

    public String getAssessmentTitle() {
      return assessmentTitle;
    }

    public java.time.LocalDate getBeginTime() {
      return beginTime;
    }

    public java.time.LocalDate getEndTime() {
      return endTime;
    }

    public String getResult() {
      return result;
    }
  }

  public int createExam(int personnelId, int scheduleId) throws SQLException {
    String sql = "insert into exams(personnel_id, schedule_id, begin_time) values(?,?,curdate())";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, personnelId);
      ps.setInt(2, scheduleId);
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
      throw new SQLException("Failed to create exam");
    }
  }

  public ExamMeta findLatestByPersonnelAndSchedule(int personnelId, int scheduleId) throws SQLException {
    String sql = "select id, personnel_id, schedule_id, begin_time, end_time, result " +
        "from exams where personnel_id=? and schedule_id=? order by id desc limit 1";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, personnelId);
      ps.setInt(2, scheduleId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return mapMeta(rs);
      }
    }
  }

  public ExamMeta findById(int examId) throws SQLException {
    String sql = "select id, personnel_id, schedule_id, begin_time, end_time, result from exams where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, examId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return mapMeta(rs);
      }
    }
  }

  private ExamMeta mapMeta(ResultSet rs) throws SQLException {
    ExamMeta m = new ExamMeta();
    m.id = rs.getInt("id");
    m.personnelId = rs.getInt("personnel_id");
    m.scheduleId = rs.getInt("schedule_id");
    if (rs.getDate("begin_time") != null) {
      m.beginTime = rs.getDate("begin_time").toLocalDate();
    }
    if (rs.getDate("end_time") != null) {
      m.endTime = rs.getDate("end_time").toLocalDate();
    }
    m.result = rs.getString("result");
    return m;
  }

  public void saveAnswersAndResult(int examId, int personnelId, Map<Integer, Integer> answers, Map<Integer, Integer> questionChecked,
                                  String resultText) throws SQLException {
    String upsert = "insert into exam_questions(exam_id, personnel_id, question_id, answer, result, score) values(?,?,?,?,?,?) " +
        "on duplicate key update answer=values(answer), result=values(result), score=values(score)";
    String updateExam = "update exams set end_time=curdate(), result=? where id=?";

    try (Connection conn = Db.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement ps = conn.prepareStatement(upsert)) {
          for (Map.Entry<Integer, Integer> e : answers.entrySet()) {
            int questionId = e.getKey();
            Integer choiceId = e.getValue();
            int checked = questionChecked.getOrDefault(questionId, 0);
            ps.setInt(1, examId);
            ps.setInt(2, personnelId);
            ps.setInt(3, questionId);
            ps.setString(4, String.valueOf(choiceId));
            ps.setInt(5, checked);
            ps.setInt(6, checked);
            ps.addBatch();
          }
          ps.executeBatch();
        }

        try (PreparedStatement ps2 = conn.prepareStatement(updateExam)) {
          ps2.setString(1, resultText);
          ps2.setInt(2, examId);
          ps2.executeUpdate();
        }

        conn.commit();
      } catch (SQLException ex) {
        conn.rollback();
        throw ex;
      } finally {
        conn.setAutoCommit(true);
      }
    }
  }

  /**
   * 参测人员放弃本次作答：删除未完成的测评记录及其答题明细。
   * 为安全起见，仅会删除 end_time/result 为空的记录。
   */
  public void discardExam(int examId, int personnelId) throws SQLException {
    String deleteDetails = "delete from exam_questions where exam_id=? and personnel_id=?";
    String deleteExam = "delete from exams where id=? and personnel_id=? and (end_time is null) and (result is null or result='')";

    try (Connection conn = Db.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement ps = conn.prepareStatement(deleteDetails)) {
          ps.setInt(1, examId);
          ps.setInt(2, personnelId);
          ps.executeUpdate();
        }
        try (PreparedStatement ps2 = conn.prepareStatement(deleteExam)) {
          ps2.setInt(1, examId);
          ps2.setInt(2, personnelId);
          ps2.executeUpdate();
        }
        conn.commit();
      } catch (SQLException ex) {
        conn.rollback();
        throw ex;
      } finally {
        conn.setAutoCommit(true);
      }
    }
  }

  public String findResultText(int examId) throws SQLException {
    String sql = "select result from exams where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, examId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return rs.getString("result");
      }
    }
  }

  public Integer findAssessmentIdByExam(int examId) throws SQLException {
    String sql = "select s.assessment_id from exams e join schedules s on s.id=e.schedule_id where e.id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, examId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return rs.getInt("assessment_id");
      }
    }
  }

  public Integer findPersonnelIdByExam(int examId) throws SQLException {
    String sql = "select personnel_id from exams where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, examId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        int pid = rs.getInt("personnel_id");
        return rs.wasNull() ? null : pid;
      }
    }
  }

  /**
   * 管理端：按参测人员展示“最新一次测评记录”（包含未测评人员）。
   */
  public List<AdminPersonnelExamRow> listLatestByPersonnel(Integer teamId, String keyword) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("select u.id as personnel_id, u.login, u.name, tp.team_id, t.name as team_name, ");
    sql.append("e.id as exam_id, e.schedule_id, e.begin_time, e.end_time, e.result, a.title as assessment_title ");
    sql.append("from users u ");
    sql.append("left join testpersonnel tp on tp.id=u.id ");
    sql.append("left join class_teams t on t.id=tp.team_id ");
    sql.append("left join ( ");
    sql.append("  select e1.* from exams e1 ");
    sql.append("  join (select personnel_id, max(id) as max_id from exams group by personnel_id) m ");
    sql.append("    on m.personnel_id=e1.personnel_id and m.max_id=e1.id ");
    sql.append(") e on e.personnel_id=u.id ");
    sql.append("left join schedules s on s.id=e.schedule_id ");
    sql.append("left join assessments a on a.id=s.assessment_id ");
    sql.append("where u.type=4 ");

    List<Object> params = new ArrayList<>();
    if (teamId != null) {
      sql.append("and tp.team_id=? ");
      params.add(teamId);
    }
    String kw = keyword == null ? "" : keyword.trim();
    if (!kw.isEmpty()) {
      sql.append("and (u.login like ? or u.name like ? or tp.phone like ?) ");
      String like = "%" + kw + "%";
      params.add(like);
      params.add(like);
      params.add(like);
    }
    sql.append("order by u.id desc");

    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {
      for (int i = 0; i < params.size(); i++) {
        ps.setObject(i + 1, params.get(i));
      }
      try (ResultSet rs = ps.executeQuery()) {
        List<AdminPersonnelExamRow> list = new ArrayList<>();
        while (rs.next()) {
          AdminPersonnelExamRow r = new AdminPersonnelExamRow();
          r.personnelId = rs.getInt("personnel_id");
          r.login = rs.getString("login");
          r.name = rs.getString("name");
          int tid = rs.getInt("team_id");
          r.teamId = rs.wasNull() ? null : tid;
          r.teamName = rs.getString("team_name");

          int eid = rs.getInt("exam_id");
          r.examId = rs.wasNull() ? null : eid;
          int sid = rs.getInt("schedule_id");
          r.scheduleId = rs.wasNull() ? null : sid;
          r.assessmentTitle = rs.getString("assessment_title");
          if (rs.getDate("begin_time") != null) {
            r.beginTime = rs.getDate("begin_time").toLocalDate();
          }
          if (rs.getDate("end_time") != null) {
            r.endTime = rs.getDate("end_time").toLocalDate();
          }
          r.result = rs.getString("result");
          list.add(r);
        }
        return list;
      }
    }
  }

  public List<DimAgg> listDimensionAgg(int examId, int personnelId) throws SQLException {
    String sql = "select qd.dimension_id as dimension_id, sum(eq.score) as score, count(*) as total " +
        "from exam_questions eq join question_dimension qd on qd.question_id=eq.question_id " +
        "where eq.exam_id=? and eq.personnel_id=? group by qd.dimension_id order by qd.dimension_id asc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, examId);
      ps.setInt(2, personnelId);
      try (ResultSet rs = ps.executeQuery()) {
        List<DimAgg> list = new ArrayList<>();
        while (rs.next()) {
          DimAgg a = new DimAgg();
          a.dimensionId = rs.getInt("dimension_id");
          a.score = rs.getInt("score");
          a.total = rs.getInt("total");
          list.add(a);
        }
        return list;
      }
    }
  }

  public List<QuestionScore> listQuestionScores(int examId, int personnelId) throws SQLException {
    String sql = "select question_id, score from exam_questions where exam_id=? and personnel_id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, examId);
      ps.setInt(2, personnelId);
      try (ResultSet rs = ps.executeQuery()) {
        List<QuestionScore> list = new ArrayList<>();
        while (rs.next()) {
          QuestionScore qs = new QuestionScore();
          qs.questionId = rs.getInt("question_id");
          qs.score = rs.getInt("score");
          list.add(qs);
        }
        return list;
      }
    }
  }

  public List<ExamRecord> listByPersonnel(int personnelId) throws SQLException {
    String sql = "select e.id, e.schedule_id, e.begin_time, e.end_time, e.result, a.title as assessment_title " +
        "from exams e left join schedules s on s.id=e.schedule_id left join assessments a on a.id=s.assessment_id " +
        "where e.personnel_id=? order by e.id desc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, personnelId);
      try (ResultSet rs = ps.executeQuery()) {
        List<ExamRecord> list = new ArrayList<>();
        while (rs.next()) {
          ExamRecord r = new ExamRecord();
          r.setId(rs.getInt("id"));
          r.setScheduleId(rs.getInt("schedule_id"));
          r.setAssessmentTitle(rs.getString("assessment_title"));
          if (rs.getDate("begin_time") != null) {
            r.setBeginTime(rs.getDate("begin_time").toLocalDate());
          }
          if (rs.getDate("end_time") != null) {
            r.setEndTime(rs.getDate("end_time").toLocalDate());
          }
          r.setResult(rs.getString("result"));
          list.add(r);
        }
        return list;
      }
    }
  }

  public static class DimAgg {
    public int dimensionId;
    public int score;
    public int total;
  }

  public static class QuestionScore {
    public int questionId;
    public int score;
  }
}
