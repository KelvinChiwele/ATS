package com.techart.ats.models;

/**
 * Created by Kelvin on 05/06/2017.
 */

public class News {
    private String user;
    private String userUrl;
    private String newsTitle;
    private String news;
    private String imageUrl;
    private Long numComments;
    private Long numViews;
    private Long timeCreated;

    public News()
    {

    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
    public void setNews(String news) {
        this.news = news;
    }

    public String getNewsTitle() {
        return newsTitle;
    }
    public String getNews() {
        return news;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }
    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getNumComments() {
        return numComments;
    }

    public void setNumComments(Long numComments) {
        this.numComments = numComments;
    }

    public Long getNumViews() {
        return numViews;
    }

    public void setNumViews(Long numViews) {
        this.numViews = numViews;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }
}
