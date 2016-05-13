package ch.sbb.adiguzaf.rssreader.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import ch.sbb.adiguzaf.rssreader.R;
import ch.sbb.adiguzaf.rssreader.activity.MainActivity;
import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

public class RSSFeedService extends IntentService {

    private static final String mName = RSSFeedService.class.getSimpleName();

    public RSSFeedService() {
        super(mName);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String address = intent.getStringExtra(MainActivity.EXTRA_RSS_URL);
        Log.d(mName, "Address is: " + address);

        // make sure, old entries are cleared in db
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(FeedsContract.FEEDS_PROVIDER_URI, null, null);

        Rss2XmlParser rss2XmlParser = new Rss2XmlParser();
        try {
            ContentValues[] contentValuesArray = rss2XmlParser.getFeeds(getInputStream(address));

            contentResolver.bulkInsert(FeedsContract.FEEDS_PROVIDER_URI, contentValuesArray);
            Log.d(mName, "Feeds are inserted: " + Arrays.toString(contentValuesArray));

            showToast(R.string.feeds_loaded);

        } catch (Exception e) {
            Log.e(mName, "an error occurred", e);
            showToast(R.string.feeds_not_loaded);
        }
    }

    private void showToast(final int resId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RSSFeedService.this, resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private InputStream getInputStream(String address) throws IOException {
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getInputStream();
    }
}