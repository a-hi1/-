package com.mbti.web.admin;

import com.mbti.web.dao.TeamDao;
import com.mbti.web.model.Team;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "adminTeamUpdateServlet", urlPatterns = "/admin/teams/update")
public class AdminTeamUpdateServlet extends HttpServlet {
  private final TeamDao teamDao = new TeamDao();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    try {
      String idStr = val(req.getParameter("id"));
      String name = val(req.getParameter("name"));
      String beginYearStr = val(req.getParameter("beginYear"));
      String statusStr = val(req.getParameter("status"));

      String err = null;
      if (idStr.isEmpty()) {
        err = "批次ID不能为空";
      } else if (name.isEmpty()) {
        err = "批次名称不能为空";
      } else if (beginYearStr.isEmpty()) {
        err = "批次创建时间不能为空";
      } else if (!("1".equals(statusStr) || "2".equals(statusStr))) {
        err = "批次状态参数不合法";
      }

      int id = idStr.isEmpty() ? 0 : Integer.parseInt(idStr);
      if (err != null) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        if (!beginYearStr.isEmpty()) {
          team.setBeginYear(LocalDate.parse(beginYearStr));
        }
        if (!statusStr.isEmpty()) {
          team.setStatus(Integer.parseInt(statusStr));
        }
        req.setAttribute("team", team);
        req.setAttribute("error", err);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/team_edit.jsp").forward(req, resp);
        return;
      }

      teamDao.update(id, name, LocalDate.parse(beginYearStr), Integer.parseInt(statusStr));
      resp.sendRedirect(req.getContextPath() + "/admin/teams");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }
}
