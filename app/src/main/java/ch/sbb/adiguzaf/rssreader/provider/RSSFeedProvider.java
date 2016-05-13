package ch.sbb.adiguzaf.rssreader.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.AUTHORITY;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_COLUMN_PUBLISHED_DATE;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_ITEM_TYPE;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_PROVIDER_URI;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_TABLE;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_TABLE_TYPE;

/**
 * RSS Feed provider which encapsulates basic provider methods for the RSS feeds table.
 *
 * @author Kerem Adigüzel
 * @since 06.05.2016
 */
public class RSSFeedProvider extends ContentProvider {

    private static final int FEEDS_BASE = 100;
    private static final int FEEDS_COLUMN_ID = 101;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, FEEDS_TABLE, FEEDS_BASE);
        sURIMatcher.addURI(AUTHORITY, FEEDS_TABLE + "/#", FEEDS_COLUMN_ID);
    }

    private MainDatabaseHelper dbHelper;
    private SQLiteDatabase db; // Once opened successfully, the database is cached by the Android OS

    @Override
    public boolean onCreate() {
        dbHelper = new MainDatabaseHelper(getContext());
        // do not initialize db here (main UI thread), since its creation may have a long duration
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        db = dbHelper.getWritableDatabase();

        long id = db.insert(FEEDS_TABLE, null, values);

        boolean insertionWasSuccessful = id > 0;
        if (insertionWasSuccessful) {
            Uri uriWithId = ContentUris.withAppendedId(FEEDS_PROVIDER_URI, id);
            getContext().getContentResolver().notifyChange(uriWithId, null);
            return uriWithId;
        } // else
        throw new SQLException("Feed could not be added to " + uri + " with values " + values);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(FEEDS_TABLE);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FEEDS_COLUMN_ID:
                qb.appendWhere(FeedsContract.FEEDS_COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case FEEDS_BASE: // no filter
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri.toString());
        }

        if (sortOrder == null || "".equals(sortOrder)) {
            // By default sort on publication dates
            sortOrder = FEEDS_COLUMN_PUBLISHED_DATE;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // ignore for now
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        return db.delete(FEEDS_TABLE, selection, selectionArgs);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FEEDS_BASE:
                return FEEDS_TABLE_TYPE;
            case FEEDS_COLUMN_ID:
                return FEEDS_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri.toString());
        }
    }
}