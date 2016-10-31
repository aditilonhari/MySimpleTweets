package com.codepath.apps.mysimpletweets.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.io.IOException;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	//public static final String REST_CONSUMER_KEY = "rhHH3lbQjft8pPFcv9D3iFscu";       // Change this
	public static final String REST_CONSUMER_KEY = "MlZWQPfUBTDcEJfxDf9Mm43ba";       // Change this
	//public static final String REST_CONSUMER_SECRET = "6uVh3xViQlr3JMmZ7b5a2k01R4GBz7trSBhSQdOzc0HEjPd7JE"; // Change this
	public static final String REST_CONSUMER_SECRET = "Ncpy4WZy56Ukn5vrzH1yi2kzJZCfyrvNFiLpfWfBBvguWKyNWJ"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	private long max_id;

	public void setMax_id(long max_id) {
		this.max_id = max_id;
	}

	public  long getMax_id() {
		return max_id;
	}

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
		max_id = -1;
	}

	// GET Method
	public void getCurrentUser(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		Log.d("DEBUG", apiUrl);
		getClient().get(apiUrl, handler);
	}

	public void updateStatus(String status, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		getClient().post(apiUrl, params, handler);
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 50);
		params.put("since_id", 1);
		if(max_id != -1)
				params.put("max_id", max_id);
		getClient().get(apiUrl, params, handler);
	}

	public boolean isNetworkAvailable(){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public boolean isOnline(){
		Runtime runtime = Runtime.getRuntime();

		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException | InterruptedException e) {e.printStackTrace();}
		return false;
	}




	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
`	 *    i.e client.post(apiUrl, params, handler);
	 */
}