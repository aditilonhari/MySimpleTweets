package com.codepath.apps.mysimpletweets.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.adapters.TweetsAdapter;
import com.codepath.apps.mysimpletweets.fragment.ComposeDialogFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener {
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;
    private RecyclerView rvTweets;
    View.OnClickListener mOnClickListener;
    private SwipeRefreshLayout swipeContainer;
    static final int COMPOSE_ACTIVITY = 1;
    CircleImageView currentUserImage;
    public User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        toolbar.setTitleTextColor(Color.BLACK);

        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(this, tweets);
        rvTweets.setAdapter(aTweets);
        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline();
            }
        });

        client = TwitterApplication.getRestClient(); // Singleton client
        getCurrentUserDetails();
        populateTimeline();
        setSwipeToRefresh();

        mOnClickListener = view -> {
            tweets.clear();
            populateTimeline();
        };
        setFAB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        Drawable drawable = searchItem.getIcon();
        drawable.setColorFilter(getResources().getColor(R.color.twitter_blue), PorterDuff.Mode.SRC_ATOP);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                populateTimeline();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void getCurrentUserDetails(){
        client.getCurrentUser(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                currentUser = User.fromJSON(response);
                currentUserImage = (CircleImageView) findViewById(R.id.ivProfileImageToolbar);
                currentUserImage.setImageResource(android.R.color.transparent);
                Glide.with(getApplicationContext()).load(currentUser.getProfileImageUrl())
                        .into(currentUserImage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Snackbar.make(findViewById(R.id.content_timeline), "Current user data not available", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void populateTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                if(json.length() == 0){
                    Snackbar.make(findViewById(R.id.content_timeline), "No data available", Snackbar.LENGTH_LONG)
                            .show();
                }
                else {
                    //int max_id = Integer.parseInt(tweets.get(tweets.size()-1).getId_Str());
                    //client.setMax_id(max_id);
                    tweets.addAll(Tweet.fromJSONArray(json));
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    aTweets.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorObject) {
                Log.d("DEBUG", "onResponseFailure");
                if (!client.isNetworkAvailable()){
                    Log.d("DEBUG: ", "Network not connected");
                    Snackbar.make(findViewById(R.id.content_timeline), "Network connectivity lost!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", mOnClickListener)
                        .setActionTextColor(Color.RED)
                        .show();
                }

                if (!client.isOnline()) {
                    Log.d("DEBUG: ", "Device not online. Check Internet connection!");
                    Snackbar.make(findViewById(R.id.content_timeline), "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", mOnClickListener)
                            .setActionTextColor(Color.RED)
                            .show();
                }
            }
        });
    }

    private void setSwipeToRefresh(){

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setProgressBackgroundColorSchemeColor(Color.rgb(64,153,255));
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setFAB(){
       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setSize(FloatingActionButton.SIZE_AUTO);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.twitter_blue)));
        Glide.with(getApplicationContext()).load(R.drawable.ic_action_compose)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(fab);
        fab.setOnClickListener(view -> showComposeDialog());

    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(currentUser);
        composeDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        composeDialogFragment.setTargetFragment(composeDialogFragment, COMPOSE_ACTIVITY);

        composeDialogFragment.show(fm, "activity_compose");
    }

    @Override
    public void onFinishEditDialog(Bundle inputBundle) {

        Tweet newTweet = inputBundle.getParcelable("newTweet");
        tweets.add(0,newTweet);

        aTweets.notifyDataSetChanged();
        Snackbar.make(findViewById(R.id.content_timeline), "New Tweet!!", Snackbar.LENGTH_INDEFINITE).show();
    }

}
