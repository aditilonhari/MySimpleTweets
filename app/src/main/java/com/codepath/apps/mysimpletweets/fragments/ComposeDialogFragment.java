package com.codepath.apps.mysimpletweets.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

//import com.codepath.apps.mysimpletweets.models.Tweet;

/**
 * Created by aditi on 10/27/2016.
 */

public class ComposeDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private static TwitterClient client;
    static Bundle bundle;
    Tweet newTweet;

    CircleImageView cvProfileImageCompose;
    TextView tvUsernameCompose;
    TextView tvTwitterHandlerCompose;
    TextView tvCounterCompose;
    EditText etBodyCompose;
    ImageButton btnCancelCompose;
    Button btnTweetCompose;

    // 1. Defines the listener interface with a method passing back data result.
    public interface ComposeDialogListener {
        void onFinishEditDialog(Bundle b);
    }

    public ComposeDialogFragment(){
        // required for Dialog Fragment
    }

    public static ComposeDialogFragment newInstance(User curr_user){
        ComposeDialogFragment frag = new ComposeDialogFragment();
        bundle = new Bundle();
        client = TwitterApplication.getRestClient();
        bundle.putParcelable("user", Parcels.wrap(curr_user));
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Compose Tweet");
        User currentUser = Parcels.unwrap(getArguments().getParcelable("user"));
        Log.d("DEBUG Current Compose", currentUser.toString());
        tvUsernameCompose = (TextView) view.findViewById(R.id.tvUserNameCompose);
        tvTwitterHandlerCompose = (TextView) view.findViewById(R.id.tvTwitterHandlerCompose);
        tvCounterCompose = (TextView) view.findViewById(R.id.tvCounterCompose);
        btnCancelCompose = (ImageButton) view.findViewById(R.id.btnCancelCompose);
        btnTweetCompose = (Button) view.findViewById(R.id.btnTweetCompose);
        cvProfileImageCompose = (CircleImageView) view.findViewById(R.id.ivProfileImgCompose);
        etBodyCompose = (EditText) view.findViewById(R.id.etBodyCompose);

        btnCancelCompose.setOnClickListener(v -> {
            dismiss();
        });

        btnTweetCompose.setOnClickListener(v -> {
            ComposeDialogListener listener = (ComposeDialogListener) getActivity();
            String status = etBodyCompose.getText().toString();
            client.updateStatus(status, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    newTweet = Tweet.fromJSON(response);
                    bundle.putParcelable("newTweet", Parcels.wrap(newTweet));
                    listener.onFinishEditDialog(bundle);
                    dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("DEBUG","No response from update status call");
                    newTweet = null;
                }
            });
        });

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        if(getActivity() == null)
                            return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int count = 140 - etBodyCompose.getText().length();
                                tvCounterCompose.setText(Integer.toString(count));
                                if(count < 0) {
                                    Log.d("DEBUG: ", "Can't enter more than 140 characters in a tweet.");
                                    tvCounterCompose.setTextColor(Color.RED);
                                    btnTweetCompose.setEnabled(false);
                                    btnTweetCompose.setBackgroundColor(Color.LTGRAY);
                                }
                                else{
                                    tvCounterCompose.setTextColor(Color.rgb(29,161,242));
                                    btnTweetCompose.setEnabled(true);
                                    btnTweetCompose.setBackgroundColor(Color.rgb(29,161,242));
                                }
                            }
                        });
                    }


                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        tvUsernameCompose.setText(currentUser.getName());
        tvTwitterHandlerCompose.setText("@" + currentUser.getScreenName());
        tvCounterCompose.setText("140");
        cvProfileImageCompose.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(currentUser.getProfileImageUrl())
                .into(cvProfileImageCompose);
        etBodyCompose.setHint("What's hapennings!");
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

}
