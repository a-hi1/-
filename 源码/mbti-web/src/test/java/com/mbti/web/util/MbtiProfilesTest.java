package com.mbti.web.util;

import org.junit.Assert;
import org.junit.Test;

public class MbtiProfilesTest {

  @Test
  public void extractType_shouldParseFromMixedText() {
    String type = MbtiProfiles.extractType("你的测评类型为 enfp（活力型）");
    Assert.assertEquals("ENFP", type);
  }

  @Test
  public void fromResultText_shouldReturnProfile() {
    MbtiProfiles.Profile profile = MbtiProfiles.fromResultText("结果: INTJ");
    Assert.assertNotNull(profile);
    Assert.assertEquals("INTJ", profile.getType());
    Assert.assertFalse(profile.getStrengths().isEmpty());
  }
}
