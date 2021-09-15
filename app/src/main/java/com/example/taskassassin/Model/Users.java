package com.example.taskassassin.Model;

public class Users {

    public String name;
    public String profile_image;
    public long score;
    public String thumb_image;

    public Users(){}

    public Users(String name, String profile_image, long score, String thumb_image) {
        this.name = name;
        this.profile_image = profile_image;
        this.score = score;
        this.thumb_image = thumb_image;
    }


    public Users(String name, String profile_image, long score) {
        this.name = name;
        this.profile_image = profile_image;
        this.score = score;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }




}
