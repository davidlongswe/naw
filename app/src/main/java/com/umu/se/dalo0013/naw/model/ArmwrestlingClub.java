package com.umu.se.dalo0013.naw.model;

public class ArmwrestlingClub {

    String clubInfoURL;
    String clubName;
    String clubLocLatitude;
    String clubLocLongitude;
    String clubAddress;

    public ArmwrestlingClub() {
    }

    public ArmwrestlingClub(String clubInfoURL, String clubName, String clubLocLatitude, String clubLocLongitude, String clubAddress) {
        this.clubInfoURL = clubInfoURL;
        this.clubName = clubName;
        this.clubLocLatitude = clubLocLatitude;
        this.clubLocLongitude = clubLocLongitude;
        this.clubAddress = clubAddress;
    }

    public String getClubInfoURL() {
        return clubInfoURL;
    }

    public void setClubInfoURL(String clubInfoURL) {
        this.clubInfoURL = clubInfoURL;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubLocLatitude() {
        return clubLocLatitude;
    }

    public void setClubLocLatitude(String clubLocLatitude) {
        this.clubLocLatitude = clubLocLatitude;
    }

    public String getClubLocLongitude() {
        return clubLocLongitude;
    }

    public void setClubLocLongitude(String clubLocLongitude) {
        this.clubLocLongitude = clubLocLongitude;
    }

    public String getClubAddress() {
        return clubAddress;
    }

    public void setClubAddress(String clubAddress) {
        this.clubAddress = clubAddress;
    }
}
