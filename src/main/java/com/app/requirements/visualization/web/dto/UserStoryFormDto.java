package com.app.requirements.visualization.web.dto;

public class UserStoryFormDto {

    private String persona;
    private String action;
    private String benefit;

    public UserStoryFormDto() {
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }
}
