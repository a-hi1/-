package com.mbti.web.model;

public class Dimension {
  private int id;
  private String title;
  private String depict;
  private int assessmentId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDepict() {
    return depict;
  }

  public void setDepict(String depict) {
    this.depict = depict;
  }

  public int getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(int assessmentId) {
    this.assessmentId = assessmentId;
  }
}
