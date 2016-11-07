package com.codepath.apps.mysimpletweets.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aditi on 11/3/2016.
 */

public class UserTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    View.OnClickListener mOnClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentView = super.onCreateView(inflater, container, savedInstanceState);
        return  parentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient(); // Singleton client
        populateTimeline();
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        userFragment.setArguments(args);
        return userFragment;
    }

    public void populateTimeline(){
        String screenName = getArguments().getString("screenName");
        client.getUserTimeLine(screenName, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG Usertimeline", json.toString());
                if(json.length() == 0){
                    Snackbar.make(getActivity().findViewById(R.id.contentTimeline), "No data available", Snackbar.LENGTH_LONG)
                            .show();
                }
                else {
                    addAll(Tweet.fromJSONArray(json));
                    aTweets.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorObject) {
                Log.d("DEBUG", "onResponseFailure");
                if (!client.isNetworkAvailable()){
                    Log.d("DEBUG: ", "Network not connected");

                    Snackbar.make(getActivity().findViewById(R.id.contentTimeline), "Network connectivity lost!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", mOnClickListener)
                            .setActionTextColor(Color.RED)
                            .show();
                }

                if (!client.isOnline()) {
                    Log.d("DEBUG: ", "Device not online. Check Internet connection!");
                    Snackbar.make(getActivity().findViewById(R.id.contentTimeline), "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", mOnClickListener)
                            .setActionTextColor(Color.RED)
                            .show();
                }
            }
        });
    }
}
