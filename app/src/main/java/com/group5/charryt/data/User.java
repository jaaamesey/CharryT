package com.group5.charryt.data;

import org.parceler.Parcel;

@SuppressWarnings("WeakerAccess")
@Parcel
public class User {
    private static User currentUser;

    private UserType userType = UserType.Donor; // User type is donor by default

    private String id;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String name = ""; // Only used for charities

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public enum UserType {
        Donor,
        Charity
    }

    public User(){

    }


    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
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
        if (!name.equals(""))
            return name;
        return (getFirstName() + " " + getLastName()).trim();
    }

}
