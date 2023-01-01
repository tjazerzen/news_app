package com.example.android.newsapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    // Tag for LOG messages
    private static final String LOG_TAG = NewsLoader.class.getName();

    // Query URL
    private final String mURL;


    /**
     * Constructs a new {@link NewsLoader}
     *
     * @param context of the acitivty
     * @param url     to load data from
     */
    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<News> loadInBackground() {
        // Check for the errors
        if (mURL == null) return null;

        // Perform the network request, parse the JSON response and extract a list of News
        return QueryUtils.fetchNewsData(mURL);
    }
}

