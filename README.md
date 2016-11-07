# Project 3 - Simple Tweets App

Simple Tweets App is an android app that allows a user to view his Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: 30 hours spent in total

## User Stories

The following **required** functionality is completed:

* User can **sign in to Twitter** using OAuth login
* User can **view tweets from their home timeline**
  * User is displayed the username, name, and body for each tweet
  * User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
  * User can view more tweets as they scroll with [infinite pagination](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView). Number of tweets is unlimited.
    However there are [Twitter Api Rate Limits](https://dev.twitter.com/rest/public/rate-limiting) in place.
* User can **compose and post a new tweet**
  * User can click a “Compose” icon in the Action Bar on the top right
  * User can then enter a new tweet and post this to twitter
  * User is taken back to home timeline with **new tweet visible** in timeline
* User can switch between Timeline and Mention views using tabs.
  * User can view their home timeline tweets.
  * User can view the recent mentions of their username.
* User can navigate to view their own profile
  * User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* User can click on the profile image in any tweet to see another user's profile.
  * User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
  * Profile view should include that user's timeline
* User can infinitely paginate any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:

* User can **see a counter with total number of characters left for tweet** on compose tweet page
* User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* User can **pull down to refresh tweets timeline**
* Improve the user interface and theme the app to feel "twitter branded"
* Robust error handling, check if internet is available, handle error cases, network failures
* When a network request is sent, user sees an indeterminate progress indicator
* User can take favorite or retweet actions on a tweet
* Usernames and hashtags are styled + clickable within tweets using clickable spans

The following **bonus** features are implemented:

* Compose tweet functionality is build using modal overlay
* Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [Leverage RecyclerView](http://guides.codepath.com/android/Using-the-RecyclerView) as a replacement for the ListView and ArrayAdapter for all lists of tweets.
* Move the "Compose" action to a [FloatingActionButton](https://github.com/codepath/android_guides/wiki/Floating-Action-Buttons) instead of on the AppBar.
* Replaced some icon drawables and other static image assets with [vector drawables](http://guides.codepath.com/android/Drawables#vector-drawables) where appropriate.
* Replace Picasso with [Glide](http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en) for more efficient image rendering.
* On the Twitter timeline, leverage the CoordinatorLayout to apply scrolling behavior that hides / shows the toolbar.

The following **additional** features are implemented:

* Included using [CircleImageView](https://github.com/hdodenhof/CircleImageView) for profile images.
* Used [Twitter Brand Guidelines] (https://brand.twitter.com/content/dam/brand-twitter/asset-download-zip-files/Twitter_Brand_Guidelines.pdf) for styling
## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/aditilonhari/MySimpleTweets/blob/master/simpleTweetsApp.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

- Figuring out the since_id and max_id differences for Endless scrolling was challenging task.
- Setting toolbar title with the twitter vector icon was difficult but I could get to it. However I opted out of using it ultimately to make my app closer to the actual Android Twitter app.
- Adding image URL's using data-binding to the project was something I tried my hands on but had to skip it given the project deadline.
- Adding image to fab/styling fab and clearing cache was something I stumbled upon for long.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

## License

    Copyright 2016 Aditi Lonhari

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
