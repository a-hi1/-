package com.mbti.web.model;

import java.time.LocalDateTime;

public class Schedule {
  private int id;
  private LocalDateTime createDate;
  private LocalDateTime beginDate;
  private LocalDateTime endDate;
  private int duration;
  private int assessmentId;
  private int teamId;
  private int questionNumber;
  private int status;

  private String assessmentTitle;
  private String teamName;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public LocalDateTime getCreateDate() {
    return createDate;
  }

  public void setCreateDate(LocalDateTime createDate) {
    this.createDate = createDate;
  }

  public LocalDateTime getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(LocalDateTime beginDate) {
    this.beginDate = beginDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(int assessmentId) {
    this.assessmentId = assessmentId;
  }

  public int getTeamId() {
    return teamId;
  }

  public void setTeamId(int teamId) {
    this.teamId = teamId;
  }

  public int getQuestionNumber() {
    return questionNumber;
  }

  public void setQuestionNumber(int questionNumber) {
    this.questionNumber = questionNumber;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getAssessmentTitle() {
    return assessmentTitle;
  }

  public void setAssessmentTitle(String assessmentTitle) {
    this.assessmentTitle = assessmentTitle;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }
}
