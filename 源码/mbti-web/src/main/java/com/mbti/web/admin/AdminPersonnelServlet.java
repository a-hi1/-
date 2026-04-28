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
  private static final int PAGE_SIZE = 10;

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

      int page = 1;
      String pageStr = val(req.getParameter("page"));
      if (!pageStr.isEmpty()) {
        try {
          page = Math.max(1, Integer.parseInt(pageStr));
        } catch (NumberFormatException ignore) {
          page = 1;
        }
      }
      int total = personnelDao.countFiltered(teamId, nameKeyword, phoneKeyword);
      int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
      if (page > totalPages) {
        page = totalPages;
      }
      req.setAttribute("page", page);
      req.setAttribute("pageSize", PAGE_SIZE);
      req.setAttribute("total", total);
      req.setAttribute("totalPages", totalPages);

      if (teamId != null && nameKeyword.isEmpty() && phoneKeyword.isEmpty()) {
        req.setAttribute("personnel", personnelDao.listPaged(teamId, nameKeyword, phoneKeyword, page, PAGE_SIZE));
      } else {
        req.setAttribute("personnel", personnelDao.listPaged(teamId, nameKeyword, phoneKeyword, page, PAGE_SIZE));
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
        String name = val(req.getParameter("name"));
        String passwd = val(req.getParameter("passwd"));
        String phone = val(req.getParameter("phone"));
        String gender = val(req.getParameter("gender"));
        String birthdateStr = val(req.getParameter("birthdate"));
        String email = val(req.getParameter("email"));
        String teamIdStr = val(req.getParameter("teamId"));

        if (phone.isEmpty()) {
          req.getSession().setAttribute("flash", "需要填写手机号！");
          resp.sendRedirect(req.getContextPath() + "/admin/personnel");
          return;
        }

        Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);
        LocalDate birthdate = birthdateStr.isEmpty() ? null : LocalDate.parse(birthdateStr);

        // 使用手机号作为登录名
        String login = phone;
        User exist = userDao.findByLogin(login);
        if (exist != null) {
          req.getSession().setAttribute("flash", "手机号 " + phone + " 已经存在，请使用不同手机号或编辑现有记录");
          resp.sendRedirect(req.getContextPath() + "/admin/personnel");
          return;
        }

        int userId = userDao.create(login, name, passwd, 4, 1);
        personnelDao.upsertPersonnel(userId, phone, gender, birthdate, email, teamId);
        req.getSession().setAttribute("flash", "已添加参测人员：" + name + "(手机号:" + phone + ")");
      } else if ("update".equals(action)) {
        int userId = Integer.parseInt(req.getParameter("id"));
        String name = val(req.getParameter("name"));
        String phone = val(req.getParameter("phone"));
        String gender = val(req.getParameter("gender"));
        String birthdateStr = val(req.getParameter("birthdate"));
        String email = val(req.getParameter("email"));
        String teamIdStr = val(req.getParameter("teamId"));

        if (phone.isEmpty()) {
          req.getSession().setAttribute("flash", "需要填写手机号！");
          resp.sendRedirect(req.getContextPath() + "/admin/personnel");
          return;
        }

        Integer teamId = teamIdStr.isEmpty() ? null : Integer.parseInt(teamIdStr);
        LocalDate birthdate = birthdateStr.isEmpty() ? null : LocalDate.parse(birthdateStr);

        // 使用手机号作为登录名，更新目前用户的登录名
        String login = phone;
        userDao.updateBasic(userId, login, name, 4, 1);
        personnelDao.upsertPersonnel(userId, phone, gender, birthdate, email, teamId);
        req.getSession().setAttribute("flash", "已保存参测人员：" + name + "(手机号:" + phone + ")");
      } else if ("batchImport".equals(action)) {
        // 支持批量导入：每行一条，格式：name,phone,gender,birthdate,email,teamId(可选)
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

        // Flexible mapping logic
        boolean isHeader = true;
        int idxLogin = -1, idxName = -1, idxPwd = -1, idxGender = -1, idxBirthdate = -1, idxEmail = -1, idxPhone = -1;

        for (String line : rows) {
          String s = line == null ? "" : line.trim();
          if (s.isEmpty()) {
            continue;
          }
          
          String[] parts = s.split("[\\t,，]");

          if (isHeader && (s.contains("登录名") || s.contains("姓名") || s.contains("手机"))) {
            for (int i = 0; i < parts.length; i++) {
              String h = parts[i].trim();
              if (h.contains("登录名")) idxLogin = i;
              else if (h.contains("姓名")) idxName = i;
              else if (h.contains("密码")) idxPwd = i;
              else if (h.contains("性别")) idxGender = i;
              else if (h.contains("出生") || h.contains("生日")) idxBirthdate = i;
              else if (h.contains("邮箱")) idxEmail = i;
              else if (h.contains("手机")) idxPhone = i;
            }
            isHeader = false;
            continue; // Skip header row
          }
          isHeader = false;

          // Default index mapping if header wasn't found
          if (idxLogin == -1 && idxName == -1 && idxPhone == -1) {
             // 默认格式：登录名,姓名,密码,性别(M/F),出生日期,邮箱,手机号
             idxLogin = 0; idxName = 1; idxPwd = 2; idxGender = 3; idxBirthdate = 4; idxEmail = 5; idxPhone = 6;
          }

          int expectedMinParts = Math.max(idxLogin, idxName);
          if (expectedMinParts != -1 && parts.length <= expectedMinParts) {
            fail++;
            errors.add("列数不匹配：" + s);
            continue;
          }

          String login = (idxLogin != -1 && idxLogin < parts.length) ? parts[idxLogin].trim() : "";
          String name = (idxName != -1 && idxName < parts.length) ? parts[idxName].trim() : "";
          String password = (idxPwd != -1 && idxPwd < parts.length) ? parts[idxPwd].trim() : "123456";
          String gender = (idxGender != -1 && idxGender < parts.length) ? parts[idxGender].trim() : "";
          String birthdateStr = (idxBirthdate != -1 && idxBirthdate < parts.length) ? parts[idxBirthdate].trim() : "";
          String email = (idxEmail != -1 && idxEmail < parts.length) ? parts[idxEmail].trim() : "";
          String phone = (idxPhone != -1 && idxPhone < parts.length) ? parts[idxPhone].trim() : "";

          if (login.isEmpty() && phone.isEmpty()) {
            fail++;
            errors.add("缺少登录名和手机号（至少提供其一）：" + s);
            continue;
          }
          if (login.isEmpty()) login = phone; // Fallback login to phone if empty

          try {
            // Simplified team assignment defaulting
            Integer teamId = defaultTeamId;
            LocalDate birthdate = birthdateStr.isEmpty() ? null : LocalDate.parse(birthdateStr);

            User exist = userDao.findByLogin(login);
            int userId;
            if (exist == null) {
              userId = userDao.create(login, name, password, 4, 1);
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

        req.getSession().setAttribute("flash", "批量导入完成：成功 " + ok + " 条，失败 " + fail + " 条");
        if (!errors.isEmpty()) {
          req.getSession().setAttribute("importErrors", errors);
          req.getSession().setAttribute("importOk", ok);
          req.getSession().setAttribute("importFail", fail);
        }
      } else if ("batchDelete".equals(action)) {
        String[] ids = req.getParameterValues("selectedIds");
        int count = 0;
        if (ids != null) {
          for (String idStr : ids) {
            if (idStr == null || idStr.trim().isEmpty()) {
              continue;
            }
            int userId = Integer.parseInt(idStr);
            personnelDao.deletePersonnel(userId);
            userDao.delete(userId);
            count++;
          }
        }
        req.getSession().setAttribute("flash", "已批量删除 " + count + " 条参测人员");
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
