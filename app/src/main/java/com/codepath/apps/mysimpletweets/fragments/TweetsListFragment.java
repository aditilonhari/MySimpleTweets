package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.DetailsActivity;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetsAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utilities.EndlessRecyclerViewScrollListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment{

    protected TwitterClient client;
    protected View.OnClickListener mOnClickListener;
    protected TweetsAdapter.tweetsAdapterListener tweetsAdapterListener;
    protected EndlessRecyclerViewScrollListener scrollListener;

    protected ArrayList<Tweet> tweets;
    protected TweetsAdapter aTweets;
    protected RecyclerView rvTweets;
    protected LinearLayoutManager linearLayoutManager;

    //inflaton logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        rvTweets.setAdapter(aTweets);

        // Set layout manager to position the items
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);

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

        return v;
    }

    // creation lifecycle event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(getActivity(), tweets);

        aTweets.setListener(new TweetsAdapter.tweetsAdapterListener() {
            @Override
            public void launchProfileActivity(String screenName) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screen_name", Parcels.wrap(screenName));
                getContext().startActivity(i);
            }

            @Override
            public void launchDetailsView(Tweet currentTweet) {
                Intent i = new Intent(getContext(), DetailsActivity.class);
                i.putExtra("current_tweet", Parcels.wrap(currentTweet));
                getContext().startActivity(i);
            }
        });
    }

    protected void addAll(List<Tweet> new_tweets){
        Log.d("DEBUG: tweets - ",new_tweets.toString());
        tweets.addAll(new_tweets);

    }

    public void addNewTweet(Tweet newTweet){
        tweets.add(0,newTweet);

        aTweets.notifyDataSetChanged();
        rvTweets.smoothScrollToPosition(0);
    }


    public void populateTimeline(){

    };

}
