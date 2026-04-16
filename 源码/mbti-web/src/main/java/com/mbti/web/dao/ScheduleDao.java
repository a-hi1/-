package com.mbti.web.dao;

import com.mbti.web.model.Schedule;
import com.mbti.web.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDao {
  public List<Schedule> listAvailableForTeam(int teamId) throws SQLException {
    String sql = "select s.id, s.begin_date, s.end_date, s.duration, s.assessment_id, s.team_id, s.question_number, s.status, " +
      "a.title as assessment_title, t.name as team_name " +
      "from schedules s left join assessments a on a.id=s.assessment_id left join class_teams t on t.id=s.team_id " +
      "where s.team_id=? and t.status=1 and s.status=2 and s.begin_date<=now() and s.end_date>=now() order by s.id desc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, teamId);
      try (ResultSet rs = ps.executeQuery()) {
        List<Schedule> list = new ArrayList<>();
        while (rs.next()) {
          list.add(map(rs));
        }
        return list;
      }
    }
  }

  public Schedule findById(int id) throws SQLException {
    String sql = "select s.id, s.begin_date, s.end_date, s.duration, s.assessment_id, s.team_id, s.question_number, s.status, " +
        "a.title as assessment_title, t.name as team_name " +
        "from schedules s left join assessments a on a.id=s.assessment_id left join class_teams t on t.id=s.team_id where s.id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return map(rs);
      }
    }
  }

  public List<Schedule> listForTeam(Integer teamId) throws SQLException {
    String base = "select s.id, s.begin_date, s.end_date, s.duration, s.assessment_id, s.team_id, s.question_number, s.status, " +
      "a.title as assessment_title, t.name as team_name " +
      "from schedules s left join assessments a on a.id=s.assessment_id left join class_teams t on t.id=s.team_id ";

    String sql;
    if (teamId == null) {
      sql = base + " order by s.id desc";
    } else {
      sql = base + " where s.team_id=? order by s.id desc";
    }

    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      if (teamId != null) {
        ps.setInt(1, teamId);
      }
      try (ResultSet rs = ps.executeQuery()) {
        List<Schedule> list = new ArrayList<>();
        while (rs.next()) {
          list.add(map(rs));
        }
        return list;
      }
    }
  }

  private Schedule map(ResultSet rs) throws SQLException {
    Schedule s = new Schedule();
    s.setId(rs.getInt("id"));
    s.setDuration(rs.getInt("duration"));
    s.setAssessmentId(rs.getInt("assessment_id"));
    s.setTeamId(rs.getInt("team_id"));
    s.setQuestionNumber(rs.getInt("question_number"));
    s.setStatus(rs.getInt("status"));
    s.setAssessmentTitle(rs.getString("assessment_title"));
    s.setTeamName(rs.getString("team_name"));

    Timestamp b = rs.getTimestamp("begin_date");
    Timestamp e = rs.getTimestamp("end_date");
    if (b != null) {
      s.setBeginDate(b.toLocalDateTime());
    }
    if (e != null) {
      s.setEndDate(e.toLocalDateTime());
    }
    return s;
  }

  public void create(int teamId, int assessmentId, String beginDate, String endDate,
                     int duration, int questionNumber, int status, int creatorId) throws SQLException {
    String sql = "insert into schedules(begin_date, end_date, duration, assessment_id, team_id, question_number, status, creator_id, create_date) " +
        "values(?,?,?,?,?,?,?,?,curdate())";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setTimestamp(1, parseTimestamp(beginDate));
      ps.setTimestamp(2, parseTimestamp(endDate));
      ps.setInt(3, duration);
      ps.setInt(4, assessmentId);
      ps.setInt(5, teamId);
      ps.setInt(6, questionNumber);
      ps.setInt(7, status);
      ps.setInt(8, creatorId);
      ps.executeUpdate();
    }
  }

  public void update(int id, int teamId, int assessmentId, String beginDate, String endDate,
                     int duration, int questionNumber, int status) throws SQLException {
    String sql = "update schedules set begin_date=?, end_date=?, duration=?, assessment_id=?, team_id=?, question_number=?, status=? where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setTimestamp(1, parseTimestamp(beginDate));
      ps.setTimestamp(2, parseTimestamp(endDate));
      ps.setInt(3, duration);
      ps.setInt(4, assessmentId);
      ps.setInt(5, teamId);
      ps.setInt(6, questionNumber);
      ps.setInt(7, status);
      ps.setInt(8, id);
      ps.executeUpdate();
    }
  }

  public static String toDatetimeLocal(java.time.LocalDateTime dt) {
    if (dt == null) {
      return "";
    }
    // yyyy-MM-ddTHH:mm
    return dt.toString().substring(0, 16);
  }

  public void delete(int id) throws SQLException {
    String sql = "delete from schedules where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }

  private Timestamp parseTimestamp(String input) {
    if (input == null) {
      return null;
    }
    String s = input.trim();
    // 支持 HTML datetime-local：2021-10-12T10:30
    if (s.contains("T")) {
      s = s.replace('T', ' ');
      if (s.length() == 16) {
        s = s + ":00";
      }
    }
    return Timestamp.valueOf(s);
  }
}
