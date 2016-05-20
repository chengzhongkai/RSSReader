package ch.sbb.adiguzaf.rssreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ch.sbb.adiguzaf.rssreader.R;
import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initRecyclerView();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() {
        RecyclerView rssList = (RecyclerView) findViewById(R.id.rssList);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (super.onCreateOptionsMenu(menu)) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_url:
                startActivity(new Intent(this, UrlReaderActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}