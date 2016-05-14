package ch.sbb.adiguzaf.rssreader.activity;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

public class RssContentObserver extends ContentObserver {

    private Context context;
    private final RecyclerView rssList;

    public RssContentObserver(Context context, RecyclerView rssList) {
        super(new Handler());
        this.context = context;
        this.rssList = rssList;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        rssList.setAdapter(new RssRecyclerAdapter(
                context.getContentResolver()
                        .query(FeedsContract.FEEDS_PROVIDER_URI, null, null, null, null)
        ));
    }
}
