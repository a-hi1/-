package com.mbti.web.dao;

import com.mbti.web.model.Dimension;
import com.mbti.web.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DimensionDao {
  public Integer findIdByAssessmentAndTitle(int assessmentId, String title) throws SQLException {
    String sql = "select id from personality_dimension where assessment_id=? and title=? limit 1";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, assessmentId);
      ps.setString(2, title);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        int id = rs.getInt("id");
        return rs.wasNull() ? null : id;
      }
    }
  }

  public Dimension findById(int id) throws SQLException {
    String sql = "select id, title, depict, assessment_id from personality_dimension where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        Dimension d = new Dimension();
        d.setId(rs.getInt("id"));
        d.setTitle(rs.getString("title"));
        d.setDepict(rs.getString("depict"));
        d.setAssessmentId(rs.getInt("assessment_id"));
        return d;
      }
    }
  }

  public List<Dimension> listByAssessment(int assessmentId) throws SQLException {
    String sql = "select id, title, depict, assessment_id from personality_dimension where assessment_id=? order by id asc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, assessmentId);
      try (ResultSet rs = ps.executeQuery()) {
        List<Dimension> list = new ArrayList<>();
        while (rs.next()) {
          Dimension d = new Dimension();
          d.setId(rs.getInt("id"));
          d.setTitle(rs.getString("title"));
          d.setDepict(rs.getString("depict"));
          d.setAssessmentId(rs.getInt("assessment_id"));
          list.add(d);
        }
        return list;
      }
    }
  }

  public int create(int assessmentId, String title, String depict) throws SQLException {
    String sql = "insert into personality_dimension(title, depict, assessment_id) values(?,?,?)";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, title);
      ps.setString(2, depict);
      ps.setInt(3, assessmentId);
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
      throw new SQLException("Failed to create dimension");
    }
  }

  public void update(int id, int assessmentId, String title, String depict) throws SQLException {
    String sql = "update personality_dimension set title=?, depict=?, assessment_id=? where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, title);
      ps.setString(2, depict);
      ps.setInt(3, assessmentId);
      ps.setInt(4, id);
      ps.executeUpdate();
    }
  }

  public int countQuestionRefs(int dimensionId) throws SQLException {
    String sql = "select count(*) as c from question_dimension where dimension_id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, dimensionId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return 0;
        }
        return rs.getInt("c");
      }
    }
  }

  public void delete(int id) throws SQLException {
    String sql = "delete from personality_dimension where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }
}
