package com.mbti.web.filter;

import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AuthFilter implements Filter {
  private final Set<String> publicPaths = new HashSet<>();

  @Override
  public void init(FilterConfig filterConfig) {
    // 允许直接访问的路径
    publicPaths.addAll(Arrays.asList("/", "/index.jsp", "/login", "/logout", "/register"));
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    String uri = req.getRequestURI();
    String ctx = req.getContextPath();
    String path = uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;

    if (path.startsWith("/static/") || publicPaths.contains(path)) {
      chain.doFilter(request, response);
      return;
    }

    User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
    if (u == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    // 后台权限：非参测人员（type!=4）可进入后台
    if (path.startsWith("/admin") && u.getType() == 4) {
      resp.setStatus(403);
      resp.setContentType("text/plain; charset=UTF-8");
      resp.getWriter().write("403 Forbidden");
      return;
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {}
}
