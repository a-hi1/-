package com.mbti.web.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SchemaMigrations {
  private SchemaMigrations() {}

  public static void ensure(Connection conn) throws SQLException {
    ensureUsersPasswdLength(conn);
  }

  private static void ensureUsersPasswdLength(Connection conn) throws SQLException {
    // 旧脚本 users.passwd=VARCHAR(20)，哈希后需要至少 64。
    Integer len = getVarcharLength(conn, "users", "passwd");
    if (len != null && len < 64) {
      try (PreparedStatement ps = conn.prepareStatement("alter table users modify passwd varchar(128)")) {
        ps.executeUpdate();
      }
    }
  }

  private static Integer getVarcharLength(Connection conn, String table, String column) throws SQLException {
    String sql = "select character_maximum_length " +
        "from information_schema.columns " +
        "where table_schema=database() and table_name=? and column_name=?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, table);
      ps.setString(2, column);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        int v = rs.getInt(1);
        return rs.wasNull() ? null : v;
      }
    }
  }
}
