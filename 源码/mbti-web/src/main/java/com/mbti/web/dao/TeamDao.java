package com.mbti.web.dao;

import com.mbti.web.model.Team;
import com.mbti.web.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeamDao {
  public Team findById(int id) throws SQLException {
    String sql = "select id, name, begin_year, status, creator_id from class_teams where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        Team t = new Team();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        if (rs.getDate("begin_year") != null) {
          t.setBeginYear(rs.getDate("begin_year").toLocalDate());
        }
        int status = rs.getInt("status");
        t.setStatus(rs.wasNull() ? null : status);
        int creatorId = rs.getInt("creator_id");
        t.setCreatorId(rs.wasNull() ? null : creatorId);
        return t;
      }
    }
  }

  public List<Team> listAll() throws SQLException {
    String sql = "select id, name, begin_year, status, creator_id from class_teams order by id desc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<Team> list = new ArrayList<>();
      while (rs.next()) {
        Team t = new Team();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        if (rs.getDate("begin_year") != null) {
          t.setBeginYear(rs.getDate("begin_year").toLocalDate());
        }
        int status = rs.getInt("status");
        t.setStatus(rs.wasNull() ? null : status);
        int creatorId = rs.getInt("creator_id");
        t.setCreatorId(rs.wasNull() ? null : creatorId);
        list.add(t);
      }
      return list;
    }
  }

  public int create(String name, LocalDate beginYear, Integer status, Integer creatorId) throws SQLException {
    String sql = "insert into class_teams(name, begin_year, status, creator_id) values(?,?,?,?)";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, name);
      if (beginYear == null) {
        ps.setDate(2, null);
      } else {
        ps.setDate(2, java.sql.Date.valueOf(beginYear));
      }
      if (status == null) {
        ps.setObject(3, null);
      } else {
        ps.setInt(3, status);
      }
      if (creatorId == null) {
        ps.setObject(4, null);
      } else {
        ps.setInt(4, creatorId);
      }
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
      throw new SQLException("Failed to create team");
    }
  }

  public void update(int id, String name, LocalDate beginYear, Integer status) throws SQLException {
    String sql = "update class_teams set name=?, begin_year=?, status=? where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      if (beginYear == null) {
        ps.setDate(2, null);
      } else {
        ps.setDate(2, java.sql.Date.valueOf(beginYear));
      }
      if (status == null) {
        ps.setObject(3, null);
      } else {
        ps.setInt(3, status);
      }
      ps.setInt(4, id);
      ps.executeUpdate();
    }
  }

  public void delete(int id) throws SQLException {
    String sql = "delete from class_teams where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }
}
