package com.group5.charryt.data;

@SuppressWarnings("WeakerAccess")
public class User {
    private String emailAddress;
    private String firstName;
    private String lastName;
//    private Location location;

    public User(String e, String fName, String lName){
        emailAddress = e;
        firstName = fName;
        lastName = lName;
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

}
