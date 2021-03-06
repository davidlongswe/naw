package util;

import android.app.Application;
/**
 * UserProfileApi - A helper class to easily obtain the logged in users essential details
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class UserProfileApi extends Application {
    private String username;
    private String userId;
    private static UserProfileApi instance;

    public static UserProfileApi getInstance() {
        if (instance == null){
            instance = new UserProfileApi();
        }
        return instance;
    }

    public UserProfileApi(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
