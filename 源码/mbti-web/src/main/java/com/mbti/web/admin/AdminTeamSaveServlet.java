package com.mbti.web.admin;

import com.mbti.web.dao.TeamDao;
import com.mbti.web.model.User;
import com.mbti.web.util.Web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "adminTeamSaveServlet", urlPatterns = "/admin/teams/save")
public class AdminTeamSaveServlet extends HttpServlet {
  private final TeamDao teamDao = new TeamDao();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    try {
      String name = val(req.getParameter("name"));
      String beginYearStr = val(req.getParameter("beginYear"));
      String statusStr = val(req.getParameter("status"));

      String err = null;
      if (name.isEmpty()) {
        err = "批次名称不能为空";
      } else if (beginYearStr.isEmpty()) {
        err = "批次创建时间不能为空";
      } else if (!("1".equals(statusStr) || "2".equals(statusStr))) {
        err = "批次状态参数不合法";
      }

      if (err != null) {
        req.setAttribute("error", err);
        req.setAttribute("name", name);
        req.setAttribute("beginYear", beginYearStr);
        req.setAttribute("status", statusStr.isEmpty() ? "1" : statusStr);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/team_create.jsp").forward(req, resp);
        return;
      }

      LocalDate beginYear = LocalDate.parse(beginYearStr);
      int status = Integer.parseInt(statusStr);
      User u = (User) req.getSession().getAttribute(Web.SESSION_USER);
      Integer creatorId = u == null ? null : u.getId();
      teamDao.create(name, beginYear, status, creatorId);

      resp.sendRedirect(req.getContextPath() + "/admin/teams");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
