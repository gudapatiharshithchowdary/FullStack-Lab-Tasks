package com.jobportal.model;

public class ScreeningDetails {
    private String date;
    private String time;
    private String mode;
    private String platformLocation;
    private String contactInformation;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPlatformLocation() {
        return platformLocation;
    }

    public void setPlatformLocation(String platformLocation) {
        this.platformLocation = platformLocation;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }
}
