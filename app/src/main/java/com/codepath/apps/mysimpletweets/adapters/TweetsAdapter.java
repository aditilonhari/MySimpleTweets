package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aditi on 10/25/2016.
 */

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {


    // Store a member variable for the tweets
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView cvProfileImage;
        TextView tvUservame;
        TextView tvHandler;
        TextView tvBody;
        TextView tvCreatedDate;

        public ViewHolder(View itemView) {
            super(itemView);
            cvProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUservame = (TextView) itemView.findViewById(R.id.tvUserName);
            tvHandler = (TextView) itemView.findViewById(R.id.tvTwitterHandler);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedDate = (TextView) itemView.findViewById(R.id.tvCreatedDate);
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

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder viewHolder, int position) {
        Tweet tweet = mTweets.get(position);

        viewHolder.tvUservame.setText(tweet.getUser().getName());
        viewHolder.tvHandler.setText(" @" + tweet.getUser().getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvCreatedDate.setText(tweet.getCreatedAt());
        viewHolder.cvProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .into(viewHolder.cvProfileImage);
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

