package com.mbti.web.model;

import java.time.LocalDate;

public class Team {
  private int id;
  private String name;
  private LocalDate beginYear;
  private Integer status;
  private Integer creatorId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getBeginYear() {
    return beginYear;
  }

  public void setBeginYear(LocalDate beginYear) {
    this.beginYear = beginYear;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Integer creatorId) {
    this.creatorId = creatorId;
  }
}
