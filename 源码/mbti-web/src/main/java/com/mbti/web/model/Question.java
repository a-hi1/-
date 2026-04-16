package com.mbti.web.model;

import java.util.ArrayList;
import java.util.List;

public class Question {
  private int id;
  private int type;
  private String title;
  private int assessmentId;
  private int dimensionId;

  private final List<Choice> choices = new ArrayList<>();

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(int assessmentId) {
    this.assessmentId = assessmentId;
  }

  public int getDimensionId() {
    return dimensionId;
  }

  public void setDimensionId(int dimensionId) {
    this.dimensionId = dimensionId;
  }

  public List<Choice> getChoices() {
    return choices;
  }
}
