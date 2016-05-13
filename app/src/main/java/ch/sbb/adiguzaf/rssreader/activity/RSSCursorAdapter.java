package ch.sbb.adiguzaf.rssreader.activity;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

import static ch.sbb.adiguzaf.rssreader.R.id;
import static ch.sbb.adiguzaf.rssreader.R.layout;

class RSSCursorAdapter extends CursorAdapter {

    public RSSCursorAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layout.item_feeds, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int descIndex = cursor.getColumnIndex(FeedsContract.FEEDS_COLUMN_DESCRIPTION);
        int titleIndex = cursor.getColumnIndex(FeedsContract.FEEDS_COLUMN_TITLE);
        int pubDateIndex = cursor.getColumnIndex(FeedsContract.FEEDS_COLUMN_PUBLISHED_DATE);

        String title = cursor.getString(titleIndex);
        String description = cursor.getString(descIndex);

        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTimeInMillis(cursor.getLong(pubDateIndex));
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String pubDate = dateFormat.format(cal.getTime());

        // performance-2do: use the ViewHolder pattern
        TextView titleText = (TextView) view.findViewById(id.feeds_title);
        String text = title + ", erschienen am " + pubDate + "\n";
        titleText.setText(text);
        TextView contentText = (TextView) view.findViewById(id.feeds_content);
        contentText.setText(description);

        Log.d("RSSCursorAdapter", "Content bound");
    }
}