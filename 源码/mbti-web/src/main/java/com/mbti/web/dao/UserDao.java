package com.mbti.web.dao;

import com.mbti.web.model.User;
import com.mbti.web.util.Db;
import com.mbti.web.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
  public User findByLogin(String login) throws SQLException {
    String sql = "select id, login, name, passwd, type, status, last_login from users where login=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, login);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return map(rs);
      }
    }
  }

  public User findById(int id) throws SQLException {
    String sql = "select id, login, name, passwd, type, status, last_login from users where id=?";
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

  /**
   * 登录校验：兼容旧数据（明文）与新数据（加盐哈希）。
   * - 若库中存的是明文且与输入匹配，则自动升级为哈希存储。
   */
  public User findByLoginAndPassword(String login, String password) throws SQLException {
    String sql = "select id, login, name, passwd, type, status, last_login from users where login=? and status=1";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, login);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        User u = map(rs);
        String stored = u.getPasswd();
        if (stored == null) {
          return null;
        }

        // 新：哈希校验
        if (PasswordUtil.verify(password, stored)) {
          return u;
        }

        // 旧：明文兼容（匹配则升级为哈希）
        if (stored.equals(password)) {
          updatePassword(u.getId(), password);
          u.setPasswd(PasswordUtil.hash(password));
          return u;
        }
        return null;
      }
    }
  }

  public void updateLastLogin(int userId) throws SQLException {
    String sql = "update users set last_login=now() where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.executeUpdate();
    }
  }

  public List<User> listAll() throws SQLException {
    String sql = "select id, login, name, passwd, type, status, last_login from users order by id desc";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<User> list = new ArrayList<>();
      while (rs.next()) {
        list.add(map(rs));
      }
      return list;
    }
  }

  public int create(String login, String name, String passwd, int type, int status) throws SQLException {
    String sql = "insert into users(login, name, passwd, type, status) values(?,?,?,?,?)";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, login);
      ps.setString(2, name);
      ps.setString(3, PasswordUtil.hash(passwd));
      ps.setInt(4, type);
      ps.setInt(5, status);
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
      throw new SQLException("Failed to create user");
    }
  }

  public void updateBasic(int id, String login, String name, int type, int status) throws SQLException {
    String sql = "update users set login=?, name=?, type=?, status=? where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, login);
      ps.setString(2, name);
      ps.setInt(3, type);
      ps.setInt(4, status);
      ps.setInt(5, id);
      ps.executeUpdate();
    }
  }

  public void updatePassword(int id, String newPasswd) throws SQLException {
    String sql = "update users set passwd=? where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, PasswordUtil.hash(newPasswd));
      ps.setInt(2, id);
      ps.executeUpdate();
    }
  }

  public void delete(int id) throws SQLException {
    String sql = "delete from users where id=?";
    try (Connection conn = Db.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, id);
      ps.executeUpdate();
    }
  }

  private User map(ResultSet rs) throws SQLException {
    User u = new User();
    u.setId(rs.getInt("id"));
    u.setLogin(rs.getString("login"));
    u.setName(rs.getString("name"));
    u.setPasswd(rs.getString("passwd"));
    u.setType(rs.getInt("type"));
    u.setStatus(rs.getInt("status"));
    Timestamp ts = rs.getTimestamp("last_login");
    if (ts != null) {
      u.setLastLogin(ts.toLocalDateTime());
    }
    return u;
  }
}
