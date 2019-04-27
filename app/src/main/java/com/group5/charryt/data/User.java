package com.group5.charryt.data;

import android.location.Location;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class User {
    private String emailAddress;
    private String firstName;
    private String lastName;
//    private Location location;
//    private ArrayList<Booking> bookings = new ArrayList<>();
//    private ArrayList<Listing> listings = new ArrayList<>();

    public User(String e, String fName, String lName){
        emailAddress = e;
        firstName = fName;
        lastName = lName;
    }

    public User(){

    }
}
