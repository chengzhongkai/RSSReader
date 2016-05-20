package ch.sbb.adiguzaf.rssreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.sbb.adiguzaf.rssreader.R;
import ch.sbb.adiguzaf.rssreader.service.RSSFeedService;

public class UrlReaderActivity extends AppCompatActivity {

    public static final String EXTRA_RSS_URL = "ch.sbb.adiguzaf.rssreader.RSSFeedURL";
    private static final String STANDARD_URL = "http://www.sbb.ch/rssfeed.sbb.xml";
//    private static final String STANDARD_URL = "http://www.srf.ch/news/bnf/rss/1890";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_reader);

        Toolbar toolbar = (Toolbar) findViewById(R.id.url_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_url_reader, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void loadRSSFeeds(View view) {
        EditText urlTextObject = (EditText) findViewById(R.id.rssUrl);
        String url = urlTextObject.getText().toString();

        if ("".equals(url)) {
            url = STANDARD_URL;
        } else if (!url.contains("http") || !url.contains("https")) {
            url = "http://" + url;
        }

        Intent rssFeedservice = new Intent(this, RSSFeedService.class);
        rssFeedservice.putExtra(EXTRA_RSS_URL, url);
        startService(rssFeedservice);
        Toast.makeText(this, R.string.feed_loading, Toast.LENGTH_SHORT).show();
        NavUtils.navigateUpFromSameTask(this);
    }
}