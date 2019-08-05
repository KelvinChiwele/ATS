package com.techart.ats.models;

/**
 * User object
 * Created by Kelvin on 11/10/2017.
 */

public class Users {
    private String name;
    private String imageUrl;
    private String signedAs;
    private String email;
    private String district;
    private String phoneNumber;
    private Long timeCreated;

    public Users(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSignedAs() {
        return signedAs;
    }

    public void setSignedAs(String signedAs) {
        this.signedAs = signedAs;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
