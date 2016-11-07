package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utilities.PatternEditableBuilder;

import org.parceler.Parcels;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aditi on 11/6/2016.
 */

public class DetailsActivity extends AppCompatActivity{

    private boolean liked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet_detail_view);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Intent i = getIntent();
        Tweet current_tweet = (Tweet)Parcels.unwrap(getIntent().getParcelableExtra("current_tweet"));
        populateData(current_tweet);
    }

    private void populateData(Tweet tweet){

        CircleImageView cvProfileImage = (CircleImageView)findViewById(R.id.ivProfileImageDetail);
        TextView tvUsername = (TextView)findViewById(R.id.tvUserName);
        TextView tvHandler = (TextView)findViewById(R.id.tvTwitterHandler);
        TextView tvBody = (TextView)findViewById(R.id.tvBody);
        TextView tvTimeStamp = (TextView)findViewById(R.id.tvCreatedDate);
        TextView tvLocation = (TextView)findViewById(R.id.tvLocation);
        TextView tvRetweet = (TextView)findViewById(R.id.tvRetweet);
        TextView tvLikes = (TextView)findViewById(R.id.tvLikes);

        ImageButton ibReply = (ImageButton) findViewById(R.id.ibReply);
        ImageButton ibRetweet = (ImageButton) findViewById(R.id.ibRetweet);
        ImageButton ibLike = (ImageButton) findViewById(R.id.ibLike);
        ImageButton ibChat = (ImageButton) findViewById(R.id.ibChat);

        Glide.with(getApplicationContext()).load(tweet.getUser().getProfileImageUrl())
                .into(cvProfileImage);

        tvUsername.setText(tweet.getUser().getName());
        tvHandler.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvTimeStamp.setText(tweet.getCreatedAt());
        tvLocation.setText("San Mateo, California"); //Change this
        tvRetweet.setText(String.valueOf(tweet.getRetweetCount()));
        tvLikes.setText(String.valueOf(tweet.getFavCount()));

        ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Reply tweet", Snackbar.LENGTH_LONG).show();
            }
        });

        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int increase_count = Integer.parseInt(tvRetweet.getText().toString());
                Log.d("DEBUG retweet", String.valueOf(increase_count));
                increase_count++;
                 tvRetweet.setText(String.valueOf(increase_count));
            }
        });

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!liked) {
                    liked = true;
                    int increase_count = Integer.parseInt(tvLikes.getText().toString());
                    increase_count++;
                    tvLikes.setText(String.valueOf(increase_count));

                    Glide.with(DetailsActivity.this).load(R.drawable.ic_like)
                            .into(ibLike);
                }
                else{
                    liked = false;
                    int decrease_count = Integer.parseInt(tvLikes.getText().toString());
                    decrease_count--;
                    tvLikes.setText(String.valueOf(decrease_count));

                    Glide.with(DetailsActivity.this).load(R.drawable.ic_empty_like)
                            .into(ibLike);
                }
            }
        });

        ibChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Chat to tweet", Snackbar.LENGTH_LONG).show();
            }
        });

        cvProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailsActivity.this, ProfileActivity.class);
                i.putExtra("screen_name", Parcels.wrap(tweet.getUser().getScreenName().toString()));
                startActivity(i);
            }
        });

        // Style clickable spans based on pattern of @User and #hashtags
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.rgb(64,153,255),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent i = new Intent(DetailsActivity.this, ProfileActivity.class);
                                i.putExtra("screen_name", Parcels.wrap(text));
                                startActivity(i);
                            }
                        }).into(tvBody);
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), Color.rgb(64,153,255))
                .into(tvBody);
    }

}
