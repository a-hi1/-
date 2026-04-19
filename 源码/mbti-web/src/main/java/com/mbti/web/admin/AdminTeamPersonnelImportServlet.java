package com.mbti.web.admin;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.TeamDao;
import com.mbti.web.model.Team;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@WebServlet(name = "adminTeamPersonnelImportServlet", urlPatterns = "/admin/teams/personnel/import")
@MultipartConfig
public class AdminTeamPersonnelImportServlet extends HttpServlet {
  private final TeamDao teamDao = new TeamDao();
  private final PersonnelDao personnelDao = new PersonnelDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String teamIdStr = val(req.getParameter("teamId"));
      if (teamIdStr.isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel");
        return;
      }
      int teamId = Integer.parseInt(teamIdStr);
      Team team = teamDao.findById(teamId);
      if (team == null) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel");
        return;
      }

      req.setAttribute("team", team);
      req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_import.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    try {
      String teamIdStr = val(req.getParameter("teamId"));
      if (teamIdStr.isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel");
        return;
      }
      int teamId = Integer.parseInt(teamIdStr);
      Team team = teamDao.findById(teamId);
      if (team == null) {
        resp.sendRedirect(req.getContextPath() + "/admin/teams/personnel");
        return;
      }

      String raw = "";
      Part filePart = null;
      try {
        filePart = req.getPart("file");
      } catch (IllegalStateException ignore) {
        // ignore
      }
      if (filePart != null && filePart.getSize() > 0) {
        raw = readUtf8(filePart.getInputStream());
      } else {
        raw = val(req.getParameter("lines"));
      }

      if (raw.isEmpty()) {
        req.setAttribute("team", team);
        req.setAttribute("error", "导入内容为空");
        req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_import.jsp").forward(req, resp);
        return;
      }

      String[] rows = raw.split("\\r?\\n");
      int ok = 0;
      int skipped = 0;
      int fail = 0;
      StringBuilder msg = new StringBuilder();
      for (String line : rows) {
        String s = line == null ? "" : line.trim();
        if (s.isEmpty()) {
          continue;
        }
        String[] parts = s.split("[\\t,，]");
        if (parts.length < 2) {
          fail++;
          msg.append("格式错误: ").append(s).append("\n");
          continue;
        }

        String phone = parts[0].trim();
        String name = parts[1].trim();
        String genderRaw = parts.length > 2 ? parts[2].trim() : "M";
        String birthdateStr = parts.length > 3 ? parts[3].trim() : "";

        if (phone.isEmpty() || name.isEmpty()) {
          fail++;
          msg.append("手机号或姓名为空: ").append(s).append("\n");
          continue;
        }

        String gender;
        if ("男".equals(genderRaw)) {
          gender = "M";
        } else if ("女".equals(genderRaw)) {
          gender = "F";
        } else if ("M".equalsIgnoreCase(genderRaw) || "F".equalsIgnoreCase(genderRaw)) {
          gender = genderRaw.toUpperCase();
        } else {
          fail++;
          msg.append("性别不合法: ").append(s).append("\n");
          continue;
        }

        LocalDate birthdate = null;
        if (!birthdateStr.isEmpty()) {
          try {
            birthdate = LocalDate.parse(birthdateStr);
          } catch (Exception ex) {
            fail++;
            msg.append("生日格式错误: ").append(s).append("\n");
            continue;
          }
        }

        try {
          boolean inserted = personnelDao.importTestPersonnel(phone, name, gender, birthdate, teamId);
          if (inserted) {
            ok++;
          } else {
            skipped++;
          }
        } catch (Exception ex) {
          fail++;
          msg.append("导入失败: ").append(s).append(" (" + ex.getMessage() + ")\n");
        }
      }

      req.setAttribute("team", team);
      req.setAttribute("ok", ok);
      req.setAttribute("skipped", skipped);
      req.setAttribute("fail", fail);
      req.setAttribute("detail", msg.toString());
      req.getRequestDispatcher("/WEB-INF/jsp/admin/team_personnel_import.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private String val(String s) {
    return s == null ? "" : s.trim();
  }

  private String readUtf8(InputStream in) throws IOException {
    try (InputStream inputStream = in) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      int read;
      while ((read = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, read);
      }
      String text = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
      if (text.startsWith("\uFEFF")) {
        return text.substring(1);
      }
      return text;
    }
  }
}
