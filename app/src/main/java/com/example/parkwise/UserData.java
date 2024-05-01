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
    String carColor, carMake, LPNumber;

    UserData (String username, int profileImg, String firstName, String lastName, String email, String role, String LPNumber, String carMake, String carColor){
        this.username = username;
        this.profileImg = profileImg;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.LPNumber = LPNumber;
        this.carColor = carColor;
        this.carMake = carMake;
    }

}