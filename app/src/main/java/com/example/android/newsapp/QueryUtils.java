package com.example.android.newsapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.util.Log;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.util.regex.Pattern.quote;

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // private float DECREASE_AMOUNT_IN_PX = getResources().getDimensionPixelOffset(R.dimen.list_item_height);

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        // Return the list of {@link News}s
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> allNews = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // each news base response is associated with object response
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");

            // each object response is associated with JSON array under key results
            JSONArray newsArray = responseObject.getJSONArray("results");

            // For each news in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                // For a given news, extract the String associated with key "webTitle",
                // which represents the value of the web title.
                String currentWebTitle = currentNews.getString("webTitle");

                // Declare variable currentAuthor. We are not sure if the tagObject Object will
                // contain this information, so I first check if current Object contains
                // tag "webTitle" and then act accordingly
                String currentAuthor = null;
                boolean foundName = true;

                // Check if currentNews object holds information about the JSONArray under key "tags"
                if (currentNews.has("tags")) {
                    // Check if JSONArray under key "tags" isn't empty
                    if (currentNews.getJSONArray("tags").length() != 0) {
                        // Now that we know that array under key tags exists, we can get the first
                        // object (index: 0) from the array
                        JSONObject tagObject = currentNews.getJSONArray("tags").getJSONObject(0);

                        // IF: this object holds information about the webTitle, get it,
                        // ELSE IF: the object "webTitle" doesn't exists, check if objects
                        // under key "firstName" and "lastName" exist
                        // ELSE: set the  value of foundName to false, which will be handled below
                        if (tagObject.has("webTitle")) {
                            // if this object contains information about the author, set the value
                            // of the currentAuthor to be the value under the key webTitle
                            currentAuthor = tagObject.getString("webTitle");
                        } else if (tagObject.has("firstName") && tagObject.has("lastName")) {
                            currentAuthor = tagObject.getString("firstName")
                                    + " " + tagObject.getString("lastName");
                        } else {
                            foundName = false;
                        }
                    } else {
                        foundName = false;
                    }
                } else {
                    foundName = false;
                }

                // In case, there is no information about the author, set the value of 
                // currentAuthor to no_author, will will be handled in NewsAdapter class
                if (!foundName) {
                    currentAuthor = "no_author";
                }

                // For a given news, extract the String associated with key "sectionName",
                // which represents the value of section name
                String currentSectionName = currentNews.getString("sectionName");

                // Declare variable currentWebPublicationDate. We are not sure if the currentNews array will
                String currentWebPublicationDate;
                // contain this information, so I first check if current list contains key "webPublicationDate"
                // and then act accordingly

                if (currentNews.has("webPublicationDate")) {
                    currentWebPublicationDate = currentNews.getString("webPublicationDate");
                } else {
                    // Construct an object with string "no_date". objects with that kind of value
                    // will have the visibility of the TextView under ID date set to GONE in
                    // NewsAdapter class.
                    currentWebPublicationDate = "no_date";
                }

                // For a given news, extract the String associated with key "webUrl",
                // which represents the value of section name
                String currentWebUrl = currentNews.getString("webUrl");

                News news = new News(currentWebTitle, currentSectionName, currentAuthor,
                        currentWebPublicationDate, currentWebUrl);

                allNews.add(news);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
            e.printStackTrace();
        }

        // Return the list of news
        return allNews;
    }
}
