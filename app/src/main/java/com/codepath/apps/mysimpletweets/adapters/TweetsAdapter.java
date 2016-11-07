package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utilities.PatternEditableBuilder;

import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aditi on 10/25/2016.
 */

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {


    // Store a member variable for the tweets
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;
    boolean liked = false;
    boolean retweeted = false;

    private tweetsAdapterListener listener;

    public interface tweetsAdapterListener{

        public void launchProfileActivity(String screenName);
        public void launchDetailsView(Tweet currentTweet);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView cvProfileImage;
        TextView tvUservame;
        TextView tvHandler;
        TextView tvBody;
        TextView tvCreatedDate;

        ImageButton ibReply;
        ImageButton ibReTweet;
        ImageButton ibLike;
        ImageButton ibChat;

        TextView tvRetweetCount;
        TextView tvLikeCount;

        public ViewHolder(View itemView) {
            super(itemView);
            cvProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUservame = (TextView) itemView.findViewById(R.id.tvUserName);
            tvHandler = (TextView) itemView.findViewById(R.id.tvTwitterHandler);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedDate = (TextView) itemView.findViewById(R.id.tvCreatedDate);
            ibReply = (ImageButton) itemView.findViewById(R.id.ibReply);
            ibReTweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);
            ibLike = (ImageButton) itemView.findViewById(R.id.ibLike);
            ibChat = (ImageButton) itemView.findViewById(R.id.ibChat);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
        }
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View tweetView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    public void setListener(tweetsAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder viewHolder, int position) {
        Tweet tweet = mTweets.get(position);

        viewHolder.tvUservame.setText(tweet.getUser().getName());
        viewHolder.tvHandler.setText(" @" + tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvCreatedDate.setText(tweet.getCreatedAt());
        viewHolder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        viewHolder.tvLikeCount.setText(String.valueOf(tweet.getFavCount()));
        viewHolder.cvProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .into(viewHolder.cvProfileImage);

        viewHolder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Reply tweet not implemented", Snackbar.LENGTH_LONG).show();
            }
        });

        viewHolder.ibReTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!retweeted) {
                    retweeted = true;
                    int increase_count = Integer.parseInt(viewHolder.tvRetweetCount.getText().toString());
                    Log.d("DEBUG retweet", String.valueOf(increase_count));
                    increase_count++;
                    viewHolder.tvRetweetCount.setText(String.valueOf(increase_count));
                }
            }
        });

        viewHolder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!liked) {
                    int increase_count = Integer.parseInt(viewHolder.tvLikeCount.getText().toString());
                    increase_count++;
                    viewHolder.tvLikeCount.setText(String.valueOf(increase_count));

                    Glide.with(getContext()).load(R.drawable.ic_like)
                            .into(viewHolder.ibLike);
                }
                else{
                    liked = false;
                    int decrease_count = Integer.parseInt(viewHolder.tvLikeCount.getText().toString());
                    decrease_count--;
                    viewHolder.tvLikeCount.setText(String.valueOf(decrease_count));

                    Glide.with(getContext()).load(R.drawable.ic_empty_like)
                            .into(viewHolder.ibLike);
                }
            }
        });

        viewHolder.ibChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Chat to tweet not implemented", Snackbar.LENGTH_LONG).show();
            }
        });

        viewHolder.cvProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.launchProfileActivity(tweet.getUser().getScreenName().toString());
            }
        });

        // Style clickable spans based on pattern of @User and #hashtags
        new PatternEditableBuilder().
            addPattern(Pattern.compile("\\@(\\w+)"), Color.rgb(64,153,255),
                    new PatternEditableBuilder.SpannableClickedListener() {
                        @Override
                        public void onSpanClicked(String text) {
                            listener.launchProfileActivity(text);
                        }
                    }).into(viewHolder.tvBody);
        new PatternEditableBuilder().
            addPattern(Pattern.compile("\\#(\\w+)"), Color.rgb(64,153,255))
                .into(viewHolder.tvBody);

        viewHolder.tvBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.launchDetailsView(tweet);
            }
        });
    }


    // Pass in the contact array into the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }


}

