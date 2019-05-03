package com.group5.charryt.data;

import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class Listing {
    private User creator; // User that created the listing
    private String title;
    private String description;
    private Date postDate; // The date the listing was posted
    private Date endDate; // The date the listing expires
    private ArrayList<String> tags = new ArrayList<>();


    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

}
