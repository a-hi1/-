package com.mbti.web.model;

import java.time.LocalDateTime;

public class TestScheduleRow {
  private int scheduleId;
  private String assessmentTitle;
  private LocalDateTime beginDate;
  private LocalDateTime endDate;
  private int duration;
  private String resultText;
  private boolean canStart;
  private boolean inProgress;

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

  public String getResultText() {
    return resultText;
  }

  public void setResultText(String resultText) {
    this.resultText = resultText;
  }

  public boolean isCanStart() {
    return canStart;
  }

  public void setCanStart(boolean canStart) {
    this.canStart = canStart;
  }

  public boolean isInProgress() {
    return inProgress;
  }

  public void setInProgress(boolean inProgress) {
    this.inProgress = inProgress;
  }
}
