package com.group5.charryt.data;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.parceler.Parcel;

@SuppressWarnings("WeakerAccess")
@Parcel
public class User {
    private UserType userType = UserType.Donor; // User type is donor by default

    private String emailAddress;
    private String firstName;
    private String lastName;

    public User(String emailAddress, String fName, String lName, UserType userType) {
        this.emailAddress = emailAddress;
        this.firstName = fName;
        this.lastName = lName;
        this.userType = userType;
    }

    //private Location location;


    public User(String emailAddress, String fName, String lName) {
        this.emailAddress = emailAddress;
        this.firstName = fName;
        this.lastName = lName;
    }

    public enum UserType {
        Donor,
        Charity
    }

    public User(){

    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return (getFirstName() + " " + getLastName()).trim();
    }

}
