package com.group5.charryt.data;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("WeakerAccess")
@Parcel
public class Listing {
    private String id;
    private User owner; // User that created the listing
    private String title;
    private String description;
    private Date postDate; // The date the listing was posted
    private Date endDate; // The date the listing expires
    private ArrayList<String> tags = new ArrayList<>();

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public User getCreator() {
        return owner;
    }

    public void setCreator(User owner) {
        this.owner = owner;
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
