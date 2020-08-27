package com.umu.se.dalo0013.naw.model;
/**
 * ArmWrestlingClub - object storing club details
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class ArmWrestlingClub {

    String clubInfoURL;
    String clubName;
    String clubLocLatitude;
    String clubLocLongitude;
    String clubAddress;

    public ArmWrestlingClub() {
    }

    /**
     * ArmWrestlingClub constructor
     * @param clubInfoURL A URL to direct user to club homepage
     * @param clubName The arm wrestling clubs name
     * @param clubLocLatitude The arm wrestling clubs latitude on a map
     * @param clubLocLongitude The arm wrestling clubs longitude on a map
     * @param clubAddress The arm wrestling club address on a map
     */
    public ArmWrestlingClub(String clubInfoURL, String clubName, String clubLocLatitude, String clubLocLongitude, String clubAddress) {
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
