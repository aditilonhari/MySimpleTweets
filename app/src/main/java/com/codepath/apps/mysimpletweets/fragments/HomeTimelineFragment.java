package com.codepath.apps.mysimpletweets.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utilities.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aditi on 11/3/2016.
 */

public class HomeTimelineFragment extends TweetsListFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentView = super.onCreateView(inflater, container, savedInstanceState);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                long max_id = tweets.get(tweets.size()-1).getUid() ;
                client.setMax_id(max_id - 1);
                populateTimeline();
            }
        };

        rvTweets.addOnScrollListener(scrollListener);
        mOnClickListener = view -> {
            tweets.clear();
            client.setMax_id(-1);
            populateTimeline();
        };
        return  parentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateTimeline();
    }


    public void populateTimeline(){

        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG hometimeline", json.toString());
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
