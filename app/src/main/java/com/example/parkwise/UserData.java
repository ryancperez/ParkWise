package com.example.parkwise;

public class UserData {
    String username;
    int profileImg;
    String firstName;
    String lastName;
    String email;
    String role;
    String startTime;
    String endTime;

    UserData (String username, int profileImg, String firstName, String lastName, String email, String role){
        this.username = username;
        this.profileImg = profileImg;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

}