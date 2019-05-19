package com.group5.charryt.data;


import java.util.Date;
import java.util.List;

public class Booking {
    private String id;
    private Date date;
    private Date dateCreated;
    private List<User> involvedUsers;
    private List<String> involvedUserIds;
    private String description;
    private Listing listing;

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public List<User> getInvolvedUsers() {
        return involvedUsers;
    }

    public void setInvolvedUsers(List<User> users) {
        this.involvedUsers = users;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getInvolvedUserIds() {
        return involvedUserIds;
    }

    public void setInvolvedUserIds(List<String> involvedUserIds) {
        this.involvedUserIds = involvedUserIds;
    }
}
