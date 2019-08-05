package com.techart.ats.models;

/**
 * Created by Kelvin on 05/06/2017.
 */

public class Question {
    private String crop;
    private String userName;
    private Long questionNumber;
    private String problemType;
    private String userUrl;
    private String postKey;
    private String clientCategory;
    private Long timeCreated;
    private Long answerCount;
    private String question;
    private String imageUrl;

    public Question() {
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }



    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(String clientCategory) {
        this.clientCategory = clientCategory;
    }

    public Long getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Long answerCount) {
        this.answerCount = answerCount;
    }


    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public Long getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Long questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }
}
