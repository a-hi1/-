package com.mbti.web.dao;

import com.mbti.web.model.Choice;
import com.mbti.web.model.Question;
import com.mbti.web.model.QuestionQuery;
import com.mbti.web.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionDao {
  public int countUsage(int questionId) throws SQLException {
    String sqlExam = "select count(*) as c from exam_questions where question_id=?";
    String sqlPaper = "select count(*) as c from paper_questions where question_id=?";
    int total = 0;
    try (Connection conn = Db.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(sqlExam)) {
        ps.setInt(1, questionId);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            total += rs.getInt("c");
          }
        }
      }
      try (PreparedStatement ps = conn.prepareStatement(sqlPaper)) {
        ps.setInt(1, questionId);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            total += rs.getInt("c");
          }
        }
      }
    }
    return total;
  }

  public Question findById(int questionId) throws SQLException {
    String sql = "select id, type, title, hint, status, assessment_id from questions where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, questionId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        Question q = new Question();
        q.setId(rs.getInt("id"));
        q.setType(rs.getInt("type"));
        q.setTitle(rs.getString("title"));
        q.setHint(rs.getString("hint"));
        q.setStatus(rs.getInt("status"));
        q.setAssessmentId(rs.getInt("assessment_id"));

        Map<Integer, Question> byId = new HashMap<>();
        byId.put(q.getId(), q);
        fillDimensions(byId);
        fillChoices(byId);
        return q;
      }
    }
  }

  public int countByAssessment(int assessmentId) throws SQLException {
    String sql = "select count(*) as c from questions where assessment_id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, assessmentId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return 0;
        }
        return rs.getInt("c");
      }
    }
  }

  public int createTwoChoiceQuestion(int assessmentId, int dimensionId, int type, int difficulty, int status, int userId,
                                     String title, String choiceA, String choiceB, int correctIndex) throws SQLException {
    String insertQ = "insert into questions(type, title, difficulty, hint, status, assessment_id, user_id) values(?,?,?,?,?,?,?)";
    String insertQD = "insert into question_dimension(question_id, dimension_id) values(?,?)";
    String insertC = "insert into choices(question_id, title, checked, hint) values(?,?,?,null)";

    try (Connection conn = Db.getConnection()) {
      conn.setAutoCommit(false);
      try {
        int qid;
        try (PreparedStatement ps = conn.prepareStatement(insertQ, java.sql.Statement.RETURN_GENERATED_KEYS)) {
          ps.setInt(1, type);
          ps.setString(2, title);
          ps.setInt(3, difficulty);
          ps.setString(4, "");
          ps.setInt(5, status);
          ps.setInt(6, assessmentId);
          ps.setInt(7, userId);
          ps.executeUpdate();
          try (ResultSet rs = ps.getGeneratedKeys()) {
            if (!rs.next()) {
              throw new SQLException("Failed to create question");
            }
            qid = rs.getInt(1);
          }
        }

        try (PreparedStatement ps = conn.prepareStatement(insertQD)) {
          ps.setInt(1, qid);
          ps.setInt(2, dimensionId);
          ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(insertC)) {
          ps.setInt(1, qid);
          ps.setString(2, choiceA);
          ps.setInt(3, correctIndex == 1 ? 1 : 0);
          ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(insertC)) {
          ps.setInt(1, qid);
          ps.setString(2, choiceB);
          ps.setInt(3, correctIndex == 2 ? 1 : 0);
          ps.executeUpdate();
        }

        conn.commit();
        return qid;
      } catch (SQLException ex) {
        conn.rollback();
        throw ex;
      } finally {
        conn.setAutoCommit(true);
      }
    }
  }

  public void deleteQuestion(int questionId) throws SQLException {
    String delChoices = "delete from choices where question_id=?";
    String delQD = "delete from question_dimension where question_id=?";
    String delQ = "delete from questions where id=?";
    try (Connection conn = Db.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement ps = conn.prepareStatement(delChoices)) {
          ps.setInt(1, questionId);
          ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(delQD)) {
          ps.setInt(1, questionId);
          ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(delQ)) {
          ps.setInt(1, questionId);
          ps.executeUpdate();
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

  public Question findByIdWithChoices(int questionId) throws SQLException {
    String sql = "select id, type, title, hint, status, assessment_id from questions where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, questionId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        Question q = new Question();
        q.setId(rs.getInt("id"));
        q.setType(rs.getInt("type"));
        q.setTitle(rs.getString("title"));
        q.setHint(rs.getString("hint"));
        q.setStatus(rs.getInt("status"));
        q.setAssessmentId(rs.getInt("assessment_id"));

        Map<Integer, Question> byId = new HashMap<>();
        byId.put(q.getId(), q);
        fillDimensions(byId);
        fillChoices(byId);
        return q;
      }
    }
  }

  public void updateTwoChoiceQuestion(int questionId, int assessmentId, int dimensionId, String title,
                                      String choiceA, String choiceB, int correctIndex) throws SQLException {
    String upQ = "update questions set title=?, assessment_id=? where id=?";
    String delQD = "delete from question_dimension where question_id=?";
    String insQD = "insert into question_dimension(question_id, dimension_id) values(?,?)";
    String selChoices = "select id from choices where question_id=? order by id asc";
    String upChoice = "update choices set title=?, checked=? where id=?";
    String delChoices = "delete from choices where question_id=?";
    String insChoice = "insert into choices(question_id, title, checked, hint) values(?,?,?,null)";

    try (Connection conn = Db.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement ps = conn.prepareStatement(upQ)) {
          ps.setString(1, title);
          ps.setInt(2, assessmentId);
          ps.setInt(3, questionId);
          ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(delQD)) {
          ps.setInt(1, questionId);
          ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(insQD)) {
          ps.setInt(1, questionId);
          ps.setInt(2, dimensionId);
          ps.executeUpdate();
        }

        List<Integer> choiceIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(selChoices)) {
          ps.setInt(1, questionId);
          try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
              choiceIds.add(rs.getInt("id"));
            }
          }
        }

        if (choiceIds.size() >= 2) {
          int idA = choiceIds.get(0);
          int idB = choiceIds.get(1);
          try (PreparedStatement ps = conn.prepareStatement(upChoice)) {
            ps.setString(1, choiceA);
            ps.setInt(2, correctIndex == 1 ? 1 : 0);
            ps.setInt(3, idA);
            ps.addBatch();

            ps.setString(1, choiceB);
            ps.setInt(2, correctIndex == 2 ? 1 : 0);
            ps.setInt(3, idB);
            ps.addBatch();

            ps.executeBatch();
          }
          // 若历史数据存在多余选项，保留但置为非正确且不动标题（系统只展示非空选项）
        } else {
          // 选项数量异常：重建两条选项
          try (PreparedStatement ps = conn.prepareStatement(delChoices)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
          }
          try (PreparedStatement ps = conn.prepareStatement(insChoice)) {
            ps.setInt(1, questionId);
            ps.setString(2, choiceA);
            ps.setInt(3, correctIndex == 1 ? 1 : 0);
            ps.addBatch();

            ps.setInt(1, questionId);
            ps.setString(2, choiceB);
            ps.setInt(3, correctIndex == 2 ? 1 : 0);
            ps.addBatch();

            ps.executeBatch();
          }
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

  public List<Question> listByAssessmentWithChoices(int assessmentId) throws SQLException {
    List<Question> questions = listByAssessment(assessmentId);
    if (questions.isEmpty()) {
      return questions;
    }

    Map<Integer, Question> byId = new HashMap<>();
    for (Question q : questions) {
      byId.put(q.getId(), q);
    }

    fillDimensions(byId);
    fillChoices(byId);

    return questions;
  }

  public List<Question> findByCondition(QuestionQuery query) throws SQLException {
    StringBuilder sql = new StringBuilder(
      "select distinct q.id, q.type, q.title, q.hint, q.status, q.assessment_id " +
      "from questions q left join question_dimension qd on q.id=qd.question_id where 1=1");
    List<Object> params = new ArrayList<>();

    if (query.getAssessmentId() != null) {
      sql.append(" and q.assessment_id=?");
      params.add(query.getAssessmentId());
    }
    if (query.getStatus() != null) {
      sql.append(" and q.status=?");
      params.add(query.getStatus());
    }
    if (query.getDimensionId() != null) {
      sql.append(" and qd.dimension_id=?");
      params.add(query.getDimensionId());
    }
    sql.append(" order by q.id asc");

    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {
      for (int i = 0; i < params.size(); i++) {
        Object p = params.get(i);
        if (p instanceof Integer) {
          ps.setInt(i + 1, (Integer) p);
        } else {
          ps.setObject(i + 1, p);
        }
      }

      try (ResultSet rs = ps.executeQuery()) {
        List<Question> list = new ArrayList<>();
        while (rs.next()) {
          Question q = new Question();
          q.setId(rs.getInt("id"));
          q.setType(rs.getInt("type"));
          q.setTitle(rs.getString("title"));
          q.setHint(rs.getString("hint"));
          q.setStatus(rs.getInt("status"));
          q.setAssessmentId(rs.getInt("assessment_id"));
          list.add(q);
        }

        if (list.isEmpty()) {
          return list;
        }

        Map<Integer, Question> byId = new HashMap<>();
        for (Question q : list) {
          byId.put(q.getId(), q);
        }
        fillDimensions(byId);
        fillChoices(byId);
        return list;
      }
    }
  }

  private List<Question> listByAssessment(int assessmentId) throws SQLException {
    String sql = "select id, type, title, hint, status, assessment_id from questions where assessment_id=? and status=2 order by id asc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, assessmentId);
      try (ResultSet rs = ps.executeQuery()) {
        List<Question> list = new ArrayList<>();
        while (rs.next()) {
          Question q = new Question();
          q.setId(rs.getInt("id"));
          q.setType(rs.getInt("type"));
          q.setTitle(rs.getString("title"));
          q.setHint(rs.getString("hint"));
          q.setStatus(rs.getInt("status"));
          q.setAssessmentId(rs.getInt("assessment_id"));
          list.add(q);
        }
        return list;
      }
    }
  }

  private void fillDimensions(Map<Integer, Question> byId) throws SQLException {
    String sql = "select question_id, dimension_id from question_dimension";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        int qid = rs.getInt("question_id");
        Question q = byId.get(qid);
        if (q != null) {
          q.setDimensionId(rs.getInt("dimension_id"));
        }
      }
    }
  }

  private void fillChoices(Map<Integer, Question> byId) throws SQLException {
    String placeholders = String.join(",", Collections.nCopies(byId.size(), "?"));
    String sql = "select id, question_id, title, checked from choices where question_id in (" +
      placeholders + ") order by question_id asc, id asc";

    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      int idx = 1;
      for (Integer qid : byId.keySet()) {
        ps.setInt(idx++, qid);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Choice c = new Choice();
          c.setId(rs.getInt("id"));
          c.setQuestionId(rs.getInt("question_id"));
          c.setTitle(rs.getString("title"));
          c.setChecked(rs.getInt("checked"));
          Question q = byId.get(c.getQuestionId());
          if (q != null) {
            // 过滤空选项
            if (c.getTitle() != null && !c.getTitle().trim().isEmpty()) {
              q.getChoices().add(c);
            }
          }
        }
      }
    }
  }
}
