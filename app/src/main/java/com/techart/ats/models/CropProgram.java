package com.techart.ats.models;

/**
 * Created by Kelvin on 08/08/2017.
 */

public class CropProgram {
    private String name;
    private String fileUrl;
    private String fbImageUrl;
    private String shortDescription;
    private Long numViews;
    private Long timeCreated;

    public CropProgram()
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFbImageUrl() {
        return fbImageUrl;
    }

    public void setFbImageUrl(String fbImageUrl) {
        this.fbImageUrl = fbImageUrl;
    }
}
