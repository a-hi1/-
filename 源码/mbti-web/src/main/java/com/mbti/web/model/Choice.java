package com.mbti.web.model;

public class Choice {
  private int id;
  private int questionId;
  private String title;
  private int checked;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getQuestionId() {
    return questionId;
  }

  public void setQuestionId(int questionId) {
    this.questionId = questionId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getChecked() {
    return checked;
  }

  public void setChecked(int checked) {
    this.checked = checked;
  }
}
