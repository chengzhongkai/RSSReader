package ch.sbb.adiguzaf.rssreader.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

class MainActivityLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private CursorAdapter cursorAdapter;

    public MainActivityLoaderCallback(Context context, CursorAdapter cursorAdapter) {
        this.context = context;
        this.cursorAdapter = cursorAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, FeedsContract.FEEDS_PROVIDER_URI,
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