package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by aditi on 10/25/2016.
 */

@Parcel
public class User {
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public User(){
        //for parceler lib
    }

    public String getScreenName() {
        return screenName;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    //deserialize json to User object
    public static User fromJSON(JSONObject jsonObject){
        User u = new User();

        try {
            u.name = jsonObject.getString("name");
            u.uid = jsonObject.getLong("id");
            u.screenName = jsonObject.getString("screen_name");
            u.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }
}
