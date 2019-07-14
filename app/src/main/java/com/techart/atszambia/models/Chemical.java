package com.techart.atszambia.models;

import java.util.List;

/**
 * Created by Kelvin on 08/08/2017.
 * Chemical objects
 */

public class Chemical {
    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private Long timeCreated;
    private Long numReviews;
    //for filtering chemicals
    private List<String> crops;
    //for matching chemicals to pests
    private List<String> pests;

    public Chemical() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(Long numReviews) {
        this.numReviews = numReviews;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCrops() {
        return crops;
    }

    public void setCrops(List<String> crops) {
        this.crops = crops;
    }

    public List<String> getPests() {
        return pests;
    }

    public void setPests(List<String> pests) {
        this.pests = pests;
    }
}
