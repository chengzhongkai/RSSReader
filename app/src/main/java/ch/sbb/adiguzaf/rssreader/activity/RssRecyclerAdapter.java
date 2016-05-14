package ch.sbb.adiguzaf.rssreader.activity;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

import static ch.sbb.adiguzaf.rssreader.R.id;
import static ch.sbb.adiguzaf.rssreader.R.layout;
import static ch.sbb.adiguzaf.rssreader.R.string;

class RssRecyclerAdapter extends RecyclerView.Adapter<RssRecyclerAdapter.ViewHolder> {

    private static final String className = RssRecyclerAdapter.class.getSimpleName();
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance();

    private Cursor cursor;

    RssRecyclerAdapter(@NonNull final Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(layout.item_feeds, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int descIndex = cursor.getColumnIndexOrThrow(FeedsContract.FEEDS_COLUMN_DESCRIPTION);
            int titleIndex = cursor.getColumnIndexOrThrow(FeedsContract.FEEDS_COLUMN_TITLE);
            int pubDateIndex = cursor.getColumnIndexOrThrow(FeedsContract.FEEDS_COLUMN_PUBLISHED_DATE);

            String title = cursor.getString(titleIndex);
            String pubDate = getPubDate(cursor.getLong(pubDateIndex));
            String text = title + "\n" +
                    holder.itemView.getContext().getString(string.published_on) + pubDate;
            String description = cursor.getString(descIndex);

            holder.titleText.setText(text);
            holder.contentText.setText(description);

            Log.d(className, "Content bound");
        } else {
            Log.d(className, "Cursor did not move to position " + position);
        }
    }

    private String getPubDate(long timeInMs) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMs);
        return dateFormat.format(cal.getTime());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView contentText;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(id.feeds_title);
            contentText = (TextView) itemView.findViewById(id.feeds_content);
        }
    }
}