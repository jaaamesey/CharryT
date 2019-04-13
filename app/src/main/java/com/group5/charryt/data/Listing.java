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
}
