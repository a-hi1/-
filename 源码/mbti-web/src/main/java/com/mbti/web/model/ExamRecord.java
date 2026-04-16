package com.mbti.web.model;

import java.time.LocalDate;

public class ExamRecord {
  private int id;
  private int scheduleId;
  private String assessmentTitle;
  private LocalDate beginTime;
  private LocalDate endTime;
  private String result;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getScheduleId() {
    return scheduleId;
  }

  public void setScheduleId(int scheduleId) {
    this.scheduleId = scheduleId;
  }

  public String getAssessmentTitle() {
    return assessmentTitle;
  }

  public void setAssessmentTitle(String assessmentTitle) {
    this.assessmentTitle = assessmentTitle;
  }

  public LocalDate getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(LocalDate beginTime) {
    this.beginTime = beginTime;
  }

  public LocalDate getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDate endTime) {
    this.endTime = endTime;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
