package com.mbti.web.util;

import javax.servlet.http.HttpServletRequest;

public final class Web {
  public static final String SESSION_USER = "currentUser";

  private Web() {}

  public static String ctx(HttpServletRequest req, String path) {
    return req.getContextPath() + path;
  }
}
