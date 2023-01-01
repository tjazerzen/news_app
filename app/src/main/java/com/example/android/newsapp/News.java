package com.example.android.newsapp;

import android.util.Log;

public class News {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Title of the News
     */
    private String mTitle;

    /**
     * Name of the section
     */
    private String mSectionName;

    /**
     * The name of the author
     * Note, that not all instances will contain information about the author
     */
    private String mAuthor;
    private boolean authorQ = true;


    /**
     * The date of the published News
     * Note, that not all instances will constain information about the date of the publication
     * The date is stored not in milliseconds (Unix time), but in normal date format
     */
    // TODO: Figure out, if the date is really stored in milliseconds
    private String mDate;
    private boolean timeQ = true;

    /**
     * The URL address of the article
     */
    private String mURL;

    /**
     * When the computer will parse the JSON data, it will check for each item in the file if
     * there is information about the author and the date of the publication.
     * If there isn't, the instance will be constructed with string "no_author" in the author value
     * of the constructor and "no_date" value in mTimeInMillisecons class.
     * Also, the methods hasAuthor() and hasDate() are defined to check, whether there two variables
     * are known.
     * Initially, I set the values of timeQ and authorQ to true and then check in the constructor
     * method whether the JSON file contains information about these to variables
     */

    /**
     * Constructs a new {@link News} object.
     *
     * @param title       is the title of this news
     * @param sectionName is the name of the section of this news
     * @param author      is the author of this news. Note that it might be unknown
     * @param date        is the time published of this news. Note that it might be unknown.
     * @param URL         is the the URL of this news
     */

    public News(String title, String sectionName, String author, String date, String URL) {
        mTitle = title;
        mSectionName = sectionName;
        mAuthor = author;
        // Check whether information about the author is known and set variable hasAuthor accordingly
        authorQ = !mAuthor.equals("no_author");
        mDate = date;
        // Check whether information about the published date is known and set variable hasDate accordingly
        timeQ = !mDate.equals("no_date");
        mURL = URL;
    }

    /**
     * @return information if this news contains information about the author
     */
    public boolean hasAuthor() {
        return authorQ;
    }

    /**
     * @return information if this news contains information about the published date
     */

    public boolean hasDate() {
        return timeQ;
    }

    /**
     * @return the title of the news
     */
    public String getmTitle() {
        return mTitle;
    }

    /**
     * @return the section name of the news
     */
    public String getmSectionName() {
        return mSectionName;
    }

    /**
     * @return the name of the author.
     * Note that special cases when the author is unknown will be handled in other classes
     * with the help of the method hasAuthor().
     */
    public String getmAuthor() {
        return mAuthor;
    }

    /**
     * @return the published date of the news
     * Note that special cases when the published date is unknown will be handled in other classes
     * with the help of the method hasDate().
     */
    public String getmDate() {
        return mDate;
    }

    /**
     * @return the URL of the news
     */
    public String getmURL() {
        return mURL;
    }

}
