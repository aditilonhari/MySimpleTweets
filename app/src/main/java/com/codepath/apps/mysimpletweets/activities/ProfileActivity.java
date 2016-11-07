package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient client;
    User user;
    NumberProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar = (NumberProgressBar) findViewById(R.id.numberProgressBar);
        hideProgressBar();

        client = TwitterApplication.getRestClient();
        //get screenName from activity that launches it
        String screenName = Parcels.unwrap(getIntent().getParcelableExtra("screen_name"));
        if(screenName == null) {
            //get user info
            client.getUserInfo(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    // my current account information
                    getSupportActionBar().setTitle("@" + user.getScreenName());
                    populateProfileHeader(user);

                }
            });
        }
        else{
            //get clicked user info
            client.getClickedUserInfo(screenName, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    // my current account information
                    getSupportActionBar().setTitle("@" + user.getScreenName());
                    populateProfileHeader(user);
                }
            });
        }

        if(savedInstanceState == null){
            //create usertimeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            //display user fragment within the activity (dynamic way)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit(); // changes the fragments
        }
    }

    private void populateProfileHeader(User user) {
        TextView tvUsername = (TextView) findViewById(R.id.tvUserNameProfile);
        TextView tvTagline = (TextView) findViewById(R.id.tvTaglineProfile);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImageProfile);

        tvUsername.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(user.getFollowersCount() + " Followers");
        tvFollowing.setText(user.getFriendsCount() + " Following");
        Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
    }

    public void showProgressBar() {
        // Show progress item
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        // Hide progress item
        progressBar.setVisibility(View.INVISIBLE);
    }
}
