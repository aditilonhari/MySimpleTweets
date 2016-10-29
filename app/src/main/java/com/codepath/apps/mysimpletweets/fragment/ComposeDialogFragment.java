package com.codepath.apps.mysimpletweets.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
    ImageButton btnCancel;
    CircleImageView ivProfileImage;
    TextView tvUsername;
    TextView tvTwitterHandler;
    EditText etBodyCompose;
    Button btnTweet;
    TextView tvCounter;

   //private FragmentComposeBinding binding;
    static Bundle bundle;
    Tweet newTweet;

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
       /// binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose, container, false);
        //return binding.getRoot();
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Compose Tweet");

        tvUsername = (TextView) view.findViewById(R.id.tvUserNameCompose);
        tvTwitterHandler = (TextView) view.findViewById(R.id.tvTwitterHandlerCompose);
        tvCounter = (TextView) view.findViewById(R.id.tvCounterCompose);
        ivProfileImage =(CircleImageView) view.findViewById(R.id.ivProfileImgCompose);
        btnCancel = (ImageButton) view.findViewById(R.id.btnCancelCompose);
        btnTweet = (Button) view.findViewById(R.id.btnTweetCompose);
        etBodyCompose = (EditText) view.findViewById(R.id.etBodyCompose);

        etBodyCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        btnTweet.setOnClickListener(v -> {

            ComposeDialogListener listener = (ComposeDialogListener) getActivity();
            if(Integer.parseInt(tvCounter.getText().toString()) < 0){
                Log.d("DEBUG: ", "Can't enter more than 140 characters in a tweet.");
                Snackbar.make(v.findViewById(R.id.relativeLayout_fragment), "No more than 140 characters!", Snackbar.LENGTH_INDEFINITE)
                        .show();
            }
            else {
                String status = etBodyCompose.getText().toString();
                client.updateStatus(status, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        newTweet = Tweet.fromJSON(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.d("DEBUG","No response from update status call");
                        newTweet = null;
                    }
                });

                bundle.putParcelable("newTweet", Parcels.wrap(newTweet));
                listener.onFinishEditDialog(bundle);
            }
            dismiss();
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
                                tvCounter.setText(Integer.toString(140 - etBodyCompose.getText().length()));
                            }
                        });
                    }


                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        User currentUser = (User) Parcels.unwrap(getArguments().getParcelable("user"));
        tvUsername.setText(currentUser.getName());
        tvTwitterHandler.setText("@" + currentUser.getScreenName());
        tvCounter.setText("140");
        ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(currentUser.getProfileImageUrl())
                .into(ivProfileImage);
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
