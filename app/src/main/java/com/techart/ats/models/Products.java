package com.techart.ats.models;

/**
 * Created by Kelvin on 21/09/2017.
 */

public class Products {
    private String name;
    private String category;
    private Long count;
    private Long clicks;
    private Long clients;
    private String description;
    private String imageUrl;

    public Products() {

    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public Long getClients() {
        return clients;
    }

    public void setClients(Long clients) {
        this.clients = clients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
