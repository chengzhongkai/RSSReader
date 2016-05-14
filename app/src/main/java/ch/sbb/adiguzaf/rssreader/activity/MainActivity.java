package ch.sbb.adiguzaf.rssreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;
import ch.sbb.adiguzaf.rssreader.service.RSSFeedService;

import static ch.sbb.adiguzaf.rssreader.R.id;
import static ch.sbb.adiguzaf.rssreader.R.layout;
import static ch.sbb.adiguzaf.rssreader.R.string;

public class MainActivity extends Activity {

    public static final String EXTRA_RSS_URL = "ch.sbb.adiguzaf.rssreader.RSSFeedURL";
    private static final String STANDARD_URL = "http://www.sbb.ch/rssfeed.sbb.xml";
//    private static final String STANDARD_URL = "http://www.srf.ch/news/bnf/rss/1890";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        RecyclerView rssList = (RecyclerView) findViewById(id.rssList);
        rssList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rssList.setLayoutManager(linearLayoutManager);

        RssRecyclerAdapter rssAdapter = new RssRecyclerAdapter(
                getContentResolver().query(FeedsContract.FEEDS_PROVIDER_URI, null, null, null, null)
        );
        rssList.setAdapter(rssAdapter);
        getContentResolver().registerContentObserver(FeedsContract.FEEDS_PROVIDER_URI, true,
                new RssContentObserver(this, rssList));
    }

    public void loadRSSFeeds(View view) {
        EditText urlTextObject = (EditText) findViewById(id.rssUrl);
        String url = urlTextObject.getText().toString();

        if ("".equals(url)) {
            url = STANDARD_URL;
        } else if (!url.contains("http") || !url.contains("https")) {
            url = "http://" + url;
        }

        Intent rssFeedservice = new Intent(this, RSSFeedService.class);
        rssFeedservice.putExtra(EXTRA_RSS_URL, url);
        startService(rssFeedservice);
        Toast.makeText(this, string.feed_loading, Toast.LENGTH_SHORT).show();
    }
}