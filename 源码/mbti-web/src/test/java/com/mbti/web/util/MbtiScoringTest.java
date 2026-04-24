package com.mbti.web.util;

import com.mbti.web.model.Dimension;
import org.junit.Assert;
import org.junit.Test;

public class MbtiScoringTest {

  @Test
  public void extractPair_shouldReturnUpperCaseLetters() {
    String[] pair = MbtiScoring.extractPair("外向（E）-内向（i）");
    Assert.assertEquals("E", pair[0]);
    Assert.assertEquals("I", pair[1]);
  }

  @Test
  public void chooseLetter_shouldFollowThresholdRule() {
    Dimension d = new Dimension();
    d.setTitle("感觉（S）-直觉（N）");

    Assert.assertEquals("S", MbtiScoring.chooseLetter(d, 2, 3));
    Assert.assertEquals("N", MbtiScoring.chooseLetter(d, 1, 3));
  }

  @Test
  public void pairKey_shouldReturnNull_whenTitleInvalid() {
    Assert.assertNull(MbtiScoring.pairKey("无括号标题"));
  }
}
