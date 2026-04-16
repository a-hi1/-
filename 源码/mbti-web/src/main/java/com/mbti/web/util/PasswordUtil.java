package com.mbti.web.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加盐哈希工具类。
 * 采用 SHA-256 + 简单盐值策略。
 * 真实生产环境建议使用 BCrypt 或 PBKDF2。
 * 此处演示基本原理：Hash = SHA256(Password + Salt)
 */
public class PasswordUtil {

  // 固定盐值（生产环境应每个用户随机生成并存库，此处简化统一使用固定盐）
  private static final String STATIC_SALT = "mbti_project_salt_2024!";

  public static String hash(String rawPassword) {
    if (rawPassword == null) {
      return null;
    }
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      String salted = rawPassword + STATIC_SALT;
      byte[] hash = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not supported", e);
    }
  }

  public static boolean verify(String rawPassword, String hashedPassword) {
    if (rawPassword == null || hashedPassword == null) {
      return false;
    }
    return hash(rawPassword).equals(hashedPassword);
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        sb.append('0');
      }
      sb.append(hex);
    }
    return sb.toString();
  }
}
