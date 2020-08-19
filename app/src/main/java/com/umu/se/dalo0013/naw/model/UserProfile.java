package com.umu.se.dalo0013.naw.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

public class UserProfile {

    private String profilePictureUrl;
    private String userId;
    private Timestamp lastUpdated;
    private String height;
    private String userName;
    private String sex;
    private String weightClass;
    private String forearmSize;
    private String bicepSize;
    private String homeTown;
    private util.LatLng userLatLng;
    private String bio;

    public UserProfile() { //Must for firestore to work
    }

    public UserProfile(String profilePictureUrl, String userId, Timestamp lastUpdated, String height, String userName, String sex, String weightClass, String forearmSize, String bicepSize, String homeTown, util.LatLng userLatLng, String bio) {
        this.profilePictureUrl = profilePictureUrl;
        this.userId = userId;
        this.lastUpdated = lastUpdated;
        this.height = height;
        this.userName = userName;
        this.sex = sex;
        this.weightClass = weightClass;
        this.forearmSize = forearmSize;
        this.bicepSize = bicepSize;
        this.homeTown = homeTown;
        this.userLatLng = userLatLng;
        this.bio = bio;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeightClass() {
        return weightClass;
    }

    public void setWeightClass(String weightClass) {
        this.weightClass = weightClass;
    }

    public String getForearmSize() {
        return forearmSize;
    }

    public void setForearmSize(String forearmSize) {
        this.forearmSize = forearmSize;
    }

    public String getBicepSize() {
        return bicepSize;
    }

    public void setBicepSize(String bicepSize) {
        this.bicepSize = bicepSize;
    }

    public util.LatLng getUserLatLng() {
        return userLatLng;
    }

    public void setUserLatLng(util.LatLng userLatLng) {
        this.userLatLng = userLatLng;
    }

}
