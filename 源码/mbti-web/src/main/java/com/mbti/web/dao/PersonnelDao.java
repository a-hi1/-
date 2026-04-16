package com.mbti.web.dao;

import com.mbti.web.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

public class PersonnelDao {
  public static class PersonnelRow {
    public int id;
    public String login;
    public String name;
    public String phone;
    public String gender;
    public LocalDate birthdate;
    public String email;
    public Integer teamId;
    public String teamName;
    public int status;
    public LocalDateTime lastLogin;

    public int getId() {
      return id;
    }

    public String getLogin() {
      return login;
    }

    public String getName() {
      return name;
    }

    public String getPhone() {
      return phone;
    }

    public String getGender() {
      return gender;
    }

    public LocalDate getBirthdate() {
      return birthdate;
    }

    public String getEmail() {
      return email;
    }

    public Integer getTeamId() {
      return teamId;
    }

    public String getTeamName() {
      return teamName;
    }

    public int getStatus() {
      return status;
    }

    public LocalDateTime getLastLogin() {
      return lastLogin;
    }
  }

  public Integer findTeamIdByUserId(int userId) throws SQLException {
    String sql = "select team_id from testpersonnel where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        int val = rs.getInt("team_id");
        if (rs.wasNull()) {
          return null;
        }
        return val;
      }
    }
  }

  public List<PersonnelRow> listAll() throws SQLException {
    String sql = "select u.id, u.login, u.name, u.status, u.last_login, tp.phone, tp.gender, tp.birthdate, tp.email, tp.team_id, t.name as team_name " +
        "from users u left join testpersonnel tp on tp.id=u.id " +
        "left join class_teams t on t.id=tp.team_id " +
        "where u.type=4 order by u.id desc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<PersonnelRow> list = new ArrayList<>();
      while (rs.next()) {
        PersonnelRow r = new PersonnelRow();
        r.id = rs.getInt("id");
        r.login = rs.getString("login");
        r.name = rs.getString("name");
        r.status = rs.getInt("status");
        if (rs.getTimestamp("last_login") != null) {
            r.lastLogin = rs.getTimestamp("last_login").toLocalDateTime();
        }
        r.phone = rs.getString("phone");
        r.gender = rs.getString("gender");
        if (rs.getDate("birthdate") != null) {
          r.birthdate = rs.getDate("birthdate").toLocalDate();
        }
        r.email = rs.getString("email");
        int tid = rs.getInt("team_id");
        r.teamId = rs.wasNull() ? null : tid;
        r.teamName = rs.getString("team_name");
        list.add(r);
      }
      return list;
    }
  }

  public List<PersonnelRow> listFiltered(Integer teamId, String keyword) throws SQLException {
    String base = "select u.id, u.login, u.name, u.status, u.last_login, tp.phone, tp.gender, tp.birthdate, tp.email, tp.team_id, t.name as team_name " +
      "from users u left join testpersonnel tp on tp.id=u.id " +
      "left join class_teams t on t.id=tp.team_id " +
      "where u.type=4";

    StringBuilder sql = new StringBuilder(base);
    List<Object> params = new ArrayList<>();

    if (teamId != null) {
      sql.append(" and tp.team_id=?");
      params.add(teamId);
    }
    String kw = keyword == null ? "" : keyword.trim();
    if (!kw.isEmpty()) {
      sql.append(" and (u.login like ? or u.name like ? or tp.phone like ?)");
      String like = "%" + kw + "%";
      params.add(like);
      params.add(like);
      params.add(like);
    }
    sql.append(" order by u.id desc");

    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {
      for (int i = 0; i < params.size(); i++) {
        ps.setObject(i + 1, params.get(i));
      }
      try (ResultSet rs = ps.executeQuery()) {
        List<PersonnelRow> list = new ArrayList<>();
        while (rs.next()) {
          PersonnelRow r = new PersonnelRow();
          r.id = rs.getInt("id");
          r.login = rs.getString("login");
          r.name = rs.getString("name");
          r.status = rs.getInt("status");
          if (rs.getTimestamp("last_login") != null) {
              r.lastLogin = rs.getTimestamp("last_login").toLocalDateTime();
          }
          r.phone = rs.getString("phone");
          r.gender = rs.getString("gender");
          if (rs.getDate("birthdate") != null) {
            r.birthdate = rs.getDate("birthdate").toLocalDate();
          }
          r.email = rs.getString("email");
          int tid = rs.getInt("team_id");
          r.teamId = rs.wasNull() ? null : tid;
          r.teamName = rs.getString("team_name");
          list.add(r);
        }
        return list;
      }
    }
  }

  public PersonnelRow findById(int userId) throws SQLException {
    String sql = "select u.id, u.login, u.name, tp.phone, tp.gender, tp.birthdate, tp.email, tp.team_id, t.name as team_name " +
        "from users u left join testpersonnel tp on tp.id=u.id " +
        "left join class_teams t on t.id=tp.team_id " +
        "where u.id=? and u.type=4";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        PersonnelRow r = new PersonnelRow();
        r.id = rs.getInt("id");
        r.login = rs.getString("login");
        r.name = rs.getString("name");
        r.phone = rs.getString("phone");
        r.gender = rs.getString("gender");
        if (rs.getDate("birthdate") != null) {
          r.birthdate = rs.getDate("birthdate").toLocalDate();
        }
        r.email = rs.getString("email");
        int tid = rs.getInt("team_id");
        r.teamId = rs.wasNull() ? null : tid;
        r.teamName = rs.getString("team_name");
        return r;
      }
    }
  }

  public void upsertPersonnel(int userId, String phone, String gender, LocalDate birthdate, String email, Integer teamId) throws SQLException {
    String sql = "insert into testpersonnel(id, phone, gender, birthdate, email, team_id) values(?,?,?,?,?,?) " +
        "on duplicate key update phone=values(phone), gender=values(gender), birthdate=values(birthdate), email=values(email), team_id=values(team_id)";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.setString(2, phone);
      ps.setString(3, gender);
      if (birthdate == null) {
        ps.setDate(4, null);
      } else {
        ps.setDate(4, java.sql.Date.valueOf(birthdate));
      }
      ps.setString(5, email);
      if (teamId == null) {
        ps.setObject(6, null);
      } else {
        ps.setInt(6, teamId);
      }
      ps.executeUpdate();
    }
  }

  public void deletePersonnel(int userId) throws SQLException {
    String sql = "delete from testpersonnel where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.executeUpdate();
    }
  }
}
