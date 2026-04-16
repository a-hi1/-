package com.mbti.web.dao;

import com.mbti.web.model.Assessment;
import com.mbti.web.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssessmentDao {
  public Assessment findByTitle(String title) throws SQLException {
    String sql = "select id, title, cost, status from assessments where title=? limit 1";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, title);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return map(rs);
      }
    }
  }

  public Assessment findById(int id) throws SQLException {
    String sql = "select id, title, cost, status from assessments where id=?";
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

  public List<Assessment> listAll() throws SQLException {
    String sql = "select id, title, cost, status from assessments order by id desc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<Assessment> list = new ArrayList<>();
      while (rs.next()) {
        list.add(map(rs));
      }
      return list;
    }
  }

  public int create(String title, double cost, int status) throws SQLException {
    String sql = "insert into assessments(title, cost, status) values(?,?,?)";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, title);
      ps.setDouble(2, cost);
      ps.setInt(3, status);
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
      throw new SQLException("Failed to create assessment");
    }
  }

  public void update(int id, String title, double cost, int status) throws SQLException {
    String sql = "update assessments set title=?, cost=?, status=? where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, title);
      ps.setDouble(2, cost);
      ps.setInt(3, status);
      ps.setInt(4, id);
      ps.executeUpdate();
    }
  }

  public void delete(int id) throws SQLException {
    String sql = "delete from assessments where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }

  private Assessment map(ResultSet rs) throws SQLException {
    Assessment a = new Assessment();
    a.setId(rs.getInt("id"));
    a.setTitle(rs.getString("title"));
    a.setCost(rs.getDouble("cost"));
    a.setStatus(rs.getInt("status"));
    return a;
  }
}
