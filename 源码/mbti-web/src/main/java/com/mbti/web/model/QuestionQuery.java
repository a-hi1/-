package com.mbti.web.model;

public class QuestionQuery {
  private Integer assessmentId;
  private Integer status;
  private Integer dimensionId;

  public Integer getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(Integer assessmentId) {
    this.assessmentId = assessmentId;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getDimensionId() {
    return dimensionId;
  }

  public void setDimensionId(Integer dimensionId) {
    this.dimensionId = dimensionId;
  }
}