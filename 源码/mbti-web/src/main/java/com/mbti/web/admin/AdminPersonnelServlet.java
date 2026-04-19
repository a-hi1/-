package com.mbti.web.admin;

import com.mbti.web.dao.PersonnelDao;
import com.mbti.web.dao.TeamDao;
import com.mbti.web.dao.UserDao;
import com.mbti.web.model.User;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "adminPersonnelServlet", urlPatterns = "/admin/personnel")
@MultipartConfig
public class AdminPersonnelServlet extends HttpServlet {
  private final PersonnelDao personnelDao = new PersonnelDao();
  private final UserDao userDao = new UserDao();
  private final TeamDao teamDao = new TeamDao();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      req.setAttribute("teams", teamDao.listAll());

      String teamIdStr = val(req.getParameter("teamId"));
      String nameKeyword = val(req.getParameter("name"));
      String phoneKeyword = val(req.getParameter("phone"));
      // 兼容旧参数 q：若未传 name/phone，则用 q 作为姓名关键字。
      String legacyQ = val(req.getParameter("q"));
      if (nameKeyword.isEmpty() && phoneKeyword.isEmpty() && !legacyQ.isEmpty()) {
        nameKeyword = legacyQ;
      }
      Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);
      req.setAttribute("teamId", teamId);
      req.setAttribute("name", nameKeyword);
      req.setAttribute("phone", phoneKeyword);

      if (teamId != null && nameKeyword.isEmpty() && phoneKeyword.isEmpty()) {
        req.setAttribute("personnel", personnelDao.findByTeamId(teamId));
      } else {
        req.setAttribute("personnel", personnelDao.listFiltered(teamId, nameKeyword, phoneKeyword));
      }

      String idStr = req.getParameter("id");
      if (idStr != null && !idStr.trim().isEmpty()) {
        req.setAttribute("edit", personnelDao.findById(Integer.parseInt(idStr)));
      }
      req.getRequestDispatcher("/WEB-INF/jsp/admin/personnel.jsp").forward(req, resp);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String action = val(req.getParameter("action"));
    try {
      if ("create".equals(action)) {
        String login = val(req.getParameter("login"));
        String name = val(req.getParameter("name"));
        String passwd = val(req.getParameter("passwd"));
        String phone = val(req.getParameter("phone"));
        String gender = val(req.getParameter("gender"));
        String birthdateStr = val(req.getParameter("birthdate"));
        String email = val(req.getParameter("email"));
        String teamIdStr = val(req.getParameter("teamId"));

        Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);
        LocalDate birthdate = birthdateStr.isEmpty() ? null : LocalDate.parse(birthdateStr);

        int userId = userDao.create(login, name, passwd, 4, 1);
        personnelDao.upsertPersonnel(userId, phone, gender, birthdate, email, teamId);
        req.getSession().setAttribute("flash", "已添加参测人员：" + login);
      } else if ("update".equals(action)) {
        int userId = Integer.parseInt(req.getParameter("id"));
        String login = val(req.getParameter("login"));
        String name = val(req.getParameter("name"));
        String phone = val(req.getParameter("phone"));
        String gender = val(req.getParameter("gender"));
        String birthdateStr = val(req.getParameter("birthdate"));
        String email = val(req.getParameter("email"));
        String teamIdStr = val(req.getParameter("teamId"));

        Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);
        LocalDate birthdate = birthdateStr.isEmpty() ? null : LocalDate.parse(birthdateStr);

        // type 固定 4，status 固定 1（参测人员）
        userDao.updateBasic(userId, login, name, 4, 1);
        personnelDao.upsertPersonnel(userId, phone, gender, birthdate, email, teamId);
        req.getSession().setAttribute("flash", "已保存参测人员：" + login);
      } else if ("batchImport".equals(action)) {
        // 支持批量导入：每行一条，格式：login,name,phone,gender,birthdate,email,teamId(可选)
        String raw = "";
        Part filePart = null;
        try {
          filePart = req.getPart("file");
        } catch (IllegalStateException ignore) {
          // 非 multipart 或超出大小等情况，走原始文本导入
        }
        if (filePart != null && filePart.getSize() > 0) {
          raw = readUtf8(filePart.getInputStream());
        } else {
          raw = val(req.getParameter("lines"));
        }

        String defaultTeamIdStr = val(req.getParameter("defaultTeamId"));
        Integer defaultTeamId = defaultTeamIdStr.isEmpty() ? null : Integer.parseInt(defaultTeamIdStr);
        if (raw.isEmpty()) {
          req.getSession().setAttribute("flash", "导入内容为空");
          resp.sendRedirect(req.getContextPath() + "/admin/personnel");
          return;
        }

        String[] rows = raw.split("\\r?\\n");
        int ok = 0;
        int fail = 0;
        List<String> errors = new ArrayList<>();

        for (String line : rows) {
          String s = line == null ? "" : line.trim();
          if (s.isEmpty()) {
            continue;
          }
          // 允许用制表符/逗号/中文逗号分隔
          String[] parts = s.split("[\\t,，]");
          if (parts.length < 2) {
            fail++;
            errors.add("格式错误：" + s);
            continue;
          }

          String login = parts[0].trim();
          String name = parts[1].trim();
          String phone = parts.length > 2 ? parts[2].trim() : "";
          String gender = parts.length > 3 ? parts[3].trim() : "";
          String birthdateStr = parts.length > 4 ? parts[4].trim() : "";
          String email = parts.length > 5 ? parts[5].trim() : "";
          String teamStr = parts.length > 6 ? parts[6].trim() : "";

          if (login.isEmpty() || name.isEmpty()) {
            fail++;
            errors.add("登录名/姓名为空：" + s);
            continue;
          }

          try {
            Integer teamId = teamStr.isEmpty() ? defaultTeamId : Integer.parseInt(teamStr);
            LocalDate birthdate = birthdateStr.isEmpty() ? null : LocalDate.parse(birthdateStr);

            User exist = userDao.findByLogin(login);
            int userId;
            if (exist == null) {
              userId = userDao.create(login, name, "123456", 4, 1);
            } else {
              userId = exist.getId();
              userDao.updateBasic(userId, login, name, 4, 1);
            }
            personnelDao.upsertPersonnel(userId, phone, gender, birthdate, email, teamId);
            ok++;
          } catch (Exception ex) {
            fail++;
            errors.add("导入失败：" + login + " - " + ex.getMessage());
          }
        }

        req.getSession().setAttribute("flash", "批量导入完成：成功 " + ok + " 条，失败 " + fail + " 条" + (errors.isEmpty() ? "" : "（请检查数据格式/重复登录名）"));
      } else if ("resetPwd".equals(action)) {
        int userId = Integer.parseInt(req.getParameter("id"));
        userDao.updatePassword(userId, "123456");
        req.getSession().setAttribute("flash", "已重置密码为 123456");
      } else if ("delete".equals(action)) {
        int userId = Integer.parseInt(req.getParameter("id"));
        personnelDao.deletePersonnel(userId);
        userDao.delete(userId);
        req.getSession().setAttribute("flash", "已删除参测人员");
      }

      resp.sendRedirect(req.getContextPath() + "/admin/personnel");
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
      byte[] bytes = outputStream.toByteArray();
      String text = new String(bytes, StandardCharsets.UTF_8);
      // 去掉 UTF-8 BOM
      if (text.startsWith("\uFEFF")) {
        return text.substring(1);
      }
      return text;
    }
  }
}
