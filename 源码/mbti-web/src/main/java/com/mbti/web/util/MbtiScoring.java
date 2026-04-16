package com.mbti.web.util;

import com.mbti.web.model.Dimension;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MbtiScoring {
  private static final Pattern LETTER_PATTERN = Pattern.compile("（\\s*([A-Za-z])\\s*）");

  private MbtiScoring() {}

  public static String chooseLetter(Dimension dimension, int score, int total) {
    String[] pair = extractPair(dimension.getTitle());
    if (pair[0] == null || pair[1] == null) {
      return "?";
    }
    if (total <= 0) {
      return "?";
    }
    // score>=一半 => 选择前者（与 choices.checked=1 对应）
    int threshold = (total + 1) / 2;
    return score >= threshold ? pair[0] : pair[1];
  }

  public static String pairKey(String title) {
    String[] pair = extractPair(title);
    if (pair[0] == null || pair[1] == null) {
      return null;
    }
    return pair[0] + pair[1];
  }

  public static String[] extractPair(String title) {
    String[] pair = new String[] {null, null};
    if (title == null) {
      return pair;
    }
    Matcher m = LETTER_PATTERN.matcher(title);
    int i = 0;
    while (m.find() && i < 2) {
      pair[i++] = m.group(1).toUpperCase();
    }
    return pair;
  }
}
