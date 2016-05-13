package ch.sbb.adiguzaf.rssreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ch.sbb.adiguzaf.rssreader.service.RSSFeedService;

import static ch.sbb.adiguzaf.rssreader.R.id;
import static ch.sbb.adiguzaf.rssreader.R.layout;
import static ch.sbb.adiguzaf.rssreader.R.string;

public class MainActivity extends Activity {

    public static final String EXTRA_RSS_URL = "ch.sbb.adiguzaf.rssreader.RSSFeedURL";
    private static final String STANDARD_URL = "http://www.sbb.ch/rssfeed.sbb.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        CursorAdapter cursorAdapter = new RSSCursorAdapter(this);
        ListView listView = (ListView) findViewById(id.rssList);
        listView.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, new MainActivityLoaderCallback(this, cursorAdapter));
    }

    public void loadRSSFeeds(View view) {
        EditText urlTextObject = (EditText) findViewById(id.rssUrl);
        String url = urlTextObject.getText().toString();

        if ("".equals(url)) {
            url = STANDARD_URL;
        }

        Intent rssFeedservice = new Intent(this, RSSFeedService.class);
        rssFeedservice.putExtra(EXTRA_RSS_URL, url);
        startService(rssFeedservice);
        Toast.makeText(this, string.feed_loading, Toast.LENGTH_SHORT).show();
    }
}