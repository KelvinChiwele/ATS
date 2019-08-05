package com.techart.ats.models;

/**
 * Created by Kelvin on 08/08/2017.
 */

public class Disease {
    private String name;
    private String imageUrl;
    private String generalInformation;
    private String symptoms;
    private String spread;
    private String diseaseType;
    private String management;
    private String shortDescription;
    private Long numViews;
    private Long timeCreated;

    public Disease()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Long getNumViews() {
        return numViews;
    }

    public void setNumViews(Long numViews) {
        this.numViews = numViews;
    }

    public String getGeneralInformation() {
        return generalInformation;
    }

    public void setGeneralInformation(String generalInformation) {
        this.generalInformation = generalInformation;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getSpread() {
        return spread;
    }

    public void setSpread(String spread) {
        this.spread = spread;
    }

    public String getManagement() {
        return management;
    }

    public void setManagement(String management) {
        this.management = management;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }
}
