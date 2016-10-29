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

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public CircleImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvCreatedDate;
        public TextView tvTwitterHandler;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ivProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedDate = (TextView) itemView.findViewById(R.id.tvCreatedDate);
            tvTwitterHandler = (TextView) itemView.findViewById(R.id.tvTwitterHandler);
        }
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
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        // Set item views based on your views and data model
        TextView tvUsername = viewHolder.tvUsername;
        tvUsername.setText(tweet.getUser().getName());
        TextView tvBody = viewHolder.tvBody;
        tvBody.setText(tweet.getBody());
        TextView tvCreatedDate = viewHolder.tvCreatedDate;
        tvCreatedDate.setText(tweet.getCreatedAt());
       TextView tvTwitterHandler = viewHolder.tvTwitterHandler;
        tvTwitterHandler.setText(" @" + tweet.getUser().getScreenName());

        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl())
               .into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
