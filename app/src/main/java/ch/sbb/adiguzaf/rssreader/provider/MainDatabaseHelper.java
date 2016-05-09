package ch.sbb.adiguzaf.rssreader.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_COLUMN_DESCRIPTION;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_COLUMN_ID;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_COLUMN_LINK;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_COLUMN_PUBLISHED_DATE;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_COLUMN_TITLE;
import static ch.sbb.adiguzaf.rssreader.provider.FeedsContract.FEEDS_TABLE;

/**
 * The database helper object to facilitate database related management tasks.
 *
 * @author u221867
 * @since 06.05.2016
 */
final class MainDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "rssfeeds";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE = "CREATE TABLE " +
            FEEDS_TABLE + "( " +
            FEEDS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            FEEDS_COLUMN_TITLE + " TEXT," +
            FEEDS_COLUMN_DESCRIPTION + " TEXT," +
            FEEDS_COLUMN_PUBLISHED_DATE + " INTEGER," +
            FEEDS_COLUMN_LINK + " TEXT )";

    public MainDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ignore
    }
}