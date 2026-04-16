package com.mbti.web.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Db {
  private static final AtomicBoolean MIGRATED = new AtomicBoolean(false);

  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private Db() {}

  public static Connection getConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(AppConfig.dbUrl(), AppConfig.dbUsername(), AppConfig.dbPassword());
    if (MIGRATED.compareAndSet(false, true)) {
      try {
        SchemaMigrations.ensure(conn);
      } catch (SQLException e) {
        // 如果没有权限做 ALTER（例如非 root），不阻塞业务连接；后续按手工 SQL 修复。
      }
    }
    return conn;
  }
}
