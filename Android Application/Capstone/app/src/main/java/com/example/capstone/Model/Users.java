package com.example.capstone.Model;

public class Users {
    private String id;
    private String username;
    private String age;
    private String gender;
    private String imageURL;
    private String status;


    //Constructors

    public Users() {
    }

    public Users(String id, String username, String age, String gender, String imageURL, String status) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.imageURL = imageURL;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
