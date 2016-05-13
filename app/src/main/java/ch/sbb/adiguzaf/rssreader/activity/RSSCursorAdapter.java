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

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

import static ch.sbb.adiguzaf.rssreader.R.id;
import static ch.sbb.adiguzaf.rssreader.R.layout;
import static ch.sbb.adiguzaf.rssreader.R.string;

class RSSCursorAdapter extends CursorAdapter {

    private static final String mName = RSSCursorAdapter.class.getSimpleName();
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance();

    // the ViewHolder pattern is used to avoid repeated resource lookups
    private static class ViewHolder {
        TextView titleText;
        TextView contentText;
    }

    public RSSCursorAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(layout.item_feeds, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.titleText = (TextView) view.findViewById(id.feeds_title);
        viewHolder.contentText = (TextView) view.findViewById(id.feeds_content);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int descIndex = cursor.getColumnIndexOrThrow(FeedsContract.FEEDS_COLUMN_DESCRIPTION);
        int titleIndex = cursor.getColumnIndexOrThrow(FeedsContract.FEEDS_COLUMN_TITLE);
        int pubDateIndex = cursor.getColumnIndexOrThrow(FeedsContract.FEEDS_COLUMN_PUBLISHED_DATE);

        String title = cursor.getString(titleIndex);
        String pubDate = getPubDate(cursor, pubDateIndex);
        String text = title + "\n" +
                context.getString(string.published_on) + pubDate;
        String description = cursor.getString(descIndex);

        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.titleText.setText(text);
        viewHolder.contentText.setText(description);

        Log.d(mName, "Content bound");
    }

    private String getPubDate(Cursor cursor, int pubDateIndex) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(pubDateIndex));
        return dateFormat.format(cal.getTime());
    }
}