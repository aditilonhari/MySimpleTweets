package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.ComposeDialogFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener{

    static final int COMPOSE_ACTIVITY = 1;
    // Instance of the progress action-view
    MenuItem miActionProgressItem;
    NumberProgressBar progressBar;
    private TwitterClient client;
    public User currentUser;
    TweetsPagerAdapter tweetsPagerAdapter;
    ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        initialSetup();

        //get view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        //pager adapter
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        //set view adapter for pager
        vpPager.setAdapter(tweetsPagerAdapter);

        // find pager sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //attach pager tabstrip to the view pager
        tabStrip.setViewPager(vpPager);
        tabStrip.setTextColor(Color.rgb(64,153,255));
        tabStrip.setIndicatorColor(Color.rgb(64,153,255));
    }

    private void initialSetup(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (NumberProgressBar) findViewById(R.id.numberProgressBar);
        hideProgressBar();

        client = TwitterApplication.getRestClient();
        getCurrentUserDetails();
        //set the Floating action button for Compose activity
        setFAB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        Drawable drawable = searchItem.getIcon();
        drawable.setColorFilter(getResources().getColor(R.color.twitter_blue), PorterDuff.Mode.SRC_ATOP);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                showProgressBar();
                HomeTimelineFragment homeTimelineFragment= (HomeTimelineFragment)getCurrentFragment();
                homeTimelineFragment.populateTimeline();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                hideProgressBar();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private Fragment getCurrentFragment(){
        return tweetsPagerAdapter.getItem(vpPager.getCurrentItem());
    }

    private void setFAB(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setSize(FloatingActionButton.SIZE_AUTO);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.twitter_blue)));
        Glide.with(this).load(R.drawable.ic_action_compose)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(fab);
        fab.setOnClickListener(view -> showComposeDialog());

    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(currentUser);
        composeDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        composeDialogFragment.setTargetFragment(composeDialogFragment, COMPOSE_ACTIVITY);
        showProgressBar();
        composeDialogFragment.show(fm, "activity_compose");
    }

    @Override
    public void onFinishEditDialog(Bundle inputBundle) {
        hideProgressBar();

        if(vpPager.getCurrentItem() != 0) {
            vpPager.setCurrentItem(0);
        }
        Tweet newTweet = Parcels.unwrap(inputBundle.getParcelable("newTweet"));
        HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) getCurrentFragment();
        homeTimelineFragment.addNewTweet(newTweet);
    }

    // return order of fragments in viewpager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        // adapter gets manager used to insert and remove fragment frm activity
        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        //order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return new HomeTimelineFragment();
            else if (position == 1)
                return new MentionsTimelineFragment();
            else
                return null;
        }

        // return tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // how many fragments to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

    private void showProgressBar() {
        // Show progress item
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        // Hide progress item
        progressBar.setVisibility(View.INVISIBLE);
    }


    public void onProfileView(MenuItem mi){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }


    public void getCurrentUserDetails(){
        client.getUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                currentUser = User.fromJSON(response);
                Log.d("DEBUG: Current user - ", currentUser.toString());
                CircleImageView currentUserImage = (CircleImageView) findViewById(R.id.ivProfileImageToolbar);
                currentUserImage.setImageResource(android.R.color.transparent);
                Glide.with(TimelineActivity.this).load(currentUser.getProfileImageUrl())
                        .into(currentUserImage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                currentUser = null;
                Snackbar.make(findViewById(R.id.contentTimeline), "Current user data not available", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }
}
