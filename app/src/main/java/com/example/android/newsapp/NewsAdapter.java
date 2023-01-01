package com.example.android.newsapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {


    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param news    A List of News objects to display in a list
     */
    public NewsAdapter(Activity context, ArrayList<News> news) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for four TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }


        // Get the {@link News} object located at this position in the list
        News currentNews = getItem(position);


        // Find the TextView in the list_item.xml layout with the ID magnitude
        TextView titleTextView = listItemView.findViewById(R.id.TextViewTitle);
        // Set the text of this TextView to be the title of this News
        titleTextView.setText(currentNews.getmTitle());

        // Find the TextView in the list_item.xml layout with the TextViewSectionName
        TextView sectionNameTextView = listItemView.findViewById(R.id.TextViewSectionName);
        // Set the text of this TextView to be the section name of this news
        sectionNameTextView.setText(String.format("%s %s",
                getContext().getString(R.string.section_name), currentNews.getmSectionName()));

        // Find the TextView in the list_item.xml layout with the TextViewAuthor
        TextView authorTextView = listItemView.findViewById(R.id.TextViewAuthor);


        // We don't know if all the news will contain information about the Author. As explained
        // in the News class, if there is no information about the author, the instance is going
        // to be created by constructing the object with the value "none" in place of the author
        if (currentNews.hasAuthor()) {
            authorTextView.setText(String.format("%s %s",
                    getContext().getString(R.string.author_name),
                    currentNews.getmAuthor()));
            authorTextView.setVisibility(View.VISIBLE);
        } else {
            // Hide the visibility of the TextViews for the author section
            authorTextView.setVisibility(View.GONE);
        }


        // Find the TextView in the list_item.xml layout with the TextViewAuthor
        TextView dateTextView = listItemView.findViewById(R.id.TextViewDate);

        // We don't know if all the news will contain information about the Date. As explained
        // in the News class, if there is no information about the date, the instance is going
        // to be created by constructing the object with the value "none" in place of the author
        if (currentNews.hasDate()) {
            // Guardian api stores strings in format
            // "yyyy-mm-dd + <part of the string that is not relevant for us>"
            // So we get those values by calling method substring and building out the full date.

            // First we get the full date
            String fullDate = currentNews.getmDate();

            // Year:
            String year = fullDate.substring(0, 4);
            // Month:
            String month = fullDate.substring(5, 7);
            // Day:
            String day = fullDate.substring(8, 10);

            // I'm gonna format the date in american date format [mm-dd-yyyy]:
            String finalDate = month + ". " + day + ". " + year;

            dateTextView.setText(String.format("%s %s",
                    getContext().getString(R.string.date),
                    finalDate));
        } else {
            // Hide the visibility of the TextViews for the date section
            dateTextView.setVisibility(View.GONE);
        }

        return listItemView;

    }

}
