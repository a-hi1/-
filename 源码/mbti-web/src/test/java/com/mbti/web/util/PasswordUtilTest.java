package com.mbti.web.util;

import org.junit.Assert;
import org.junit.Test;

public class PasswordUtilTest {

  @Test
  public void hash_shouldBeDeterministic_forSameInput() {
    String h1 = PasswordUtil.hash("123456");
    String h2 = PasswordUtil.hash("123456");
    Assert.assertNotNull(h1);
    Assert.assertEquals(h1, h2);
    Assert.assertEquals(64, h1.length());
  }

  @Test
  public void verify_shouldReturnTrue_forCorrectPassword() {
    String hashed = PasswordUtil.hash("admin123");
    Assert.assertTrue(PasswordUtil.verify("admin123", hashed));
  }

  @Test
  public void verify_shouldReturnFalse_forWrongOrNullInput() {
    String hashed = PasswordUtil.hash("admin123");
    Assert.assertFalse(PasswordUtil.verify("wrong", hashed));
    Assert.assertFalse(PasswordUtil.verify(null, hashed));
    Assert.assertFalse(PasswordUtil.verify("admin123", null));
  }
}
