package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by aditi on 10/25/2016.
 */

@Parcel
public class Tweet {

    //attributes
    public String body;
    public long uid; // unique id for tweet - db id
    public User user;
    public String createdAt;
    public String id_Str;
    public int favCount;
    public int retweetCount;
    public String replyScreenName;

    public Tweet(){

    }

    //Deserialize JSON
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.id_Str = jsonObject.getString("id_str");
            tweet.favCount = jsonObject.getInt("favorite_count");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.replyScreenName = jsonObject.getString("in_reply_to_screen_name");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject tweetjson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetjson);
                if(tweet != null)
                    tweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }

        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {

        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

       String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    public String getCreatedAt() {
        return  getRelativeTimeAgo(createdAt);
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getId_Str() {
        return id_Str;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public int getFavCount() { return favCount;}

    public int getRetweetCount() { return retweetCount;}

    public String getReplyScreenName() {   return replyScreenName;}

}
