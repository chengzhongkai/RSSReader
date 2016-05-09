package ch.sbb.adiguzaf.rssreader;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

import static ch.sbb.adiguzaf.rssreader.R.id;
import static ch.sbb.adiguzaf.rssreader.R.layout;

public class MainActivity extends Activity {

    public static final String EXTRA_RSS_URL = "ch.sbb.adiguzaf.rssreader.RSSFeedURL";
    private CursorAdapter cursorAdapter = new RSSCursorAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_entry);

        ListView listView = (ListView) findViewById(R.id.rssList);
        listView.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, new ActivityLoaderCallback());
//        getContentResolver().registerContentObserver(FeedsContract.FEEDS_PROVIDER_ID_URI,
//                true, new RSSContentObserver());
    }

    public void loadRSSFeeds(View view) {
        EditText urlTextObject = (EditText) findViewById(id.rssUrl);
        String url = urlTextObject.getText().toString();

        if ("".equals(url)) { // use standard URL
            url = "http://www.sbb.ch/rssfeed.sbb.xml";
        }

        Intent rssFeedservice = new Intent(this, RSSFeedService.class);
        rssFeedservice.putExtra(EXTRA_RSS_URL, url);
        startService(rssFeedservice);
        Toast.makeText(this, R.string.feed_loading, Toast.LENGTH_SHORT).show();
    }

    private class ActivityLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this, FeedsContract.FEEDS_PROVIDER_URI,
                    null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            cursorAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            cursorAdapter.changeCursor(null);
        }
    }

    private class RSSCursorAdapter extends CursorAdapter {
        public RSSCursorAdapter() {
            super(MainActivity.this, null, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(MainActivity.this).inflate(layout.item_feeds, parent, false);
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
            TextView titleText = (TextView) findViewById(id.feeds_title);
            String text = title + ", erschienen am " + pubDate + "\n";
            titleText.setText(text);
            TextView contentText = (TextView) findViewById(id.feeds_content);
            contentText.setText(description);

            Log.d("RSSCursorAdapter", "Content bound");
        }
    }

//    private class RSSContentObserver extends ContentObserver {
//        public RSSContentObserver() {
//            super(new Handler());
//        }
//
//        @Override
//        public void onChange(boolean selfChange, Uri uri) {
//            ListView listView = (ListView) findViewById(id.rssList);
//            Cursor cursor = getContentResolver().query(
//                    FeedsContract.FEEDS_PROVIDER_URI, null, null, null, null);
//            CursorAdapter cursorAdapter = new RSSCursorAdapter(MainActivity.this, cursor, 0);
//            listView.setAdapter(cursorAdapter);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            onChange(selfChange, null);
//        }
//    }
}