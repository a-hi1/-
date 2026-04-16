package com.mbti.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
  private static final Properties PROPS = new Properties();

  static {
    try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("app.properties")) {
      if (in == null) {
        throw new IllegalStateException("app.properties not found in classpath");
      }
      PROPS.load(in);
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private AppConfig() {}

  public static String get(String key) {
    String value = PROPS.getProperty(key);
    return value == null ? "" : value.trim();
  }

  public static String dbUrl() {
    return get("db.url");
  }

  public static String dbUsername() {
    return get("db.username");
  }

  public static String dbPassword() {
    return get("db.password");
  }
}
