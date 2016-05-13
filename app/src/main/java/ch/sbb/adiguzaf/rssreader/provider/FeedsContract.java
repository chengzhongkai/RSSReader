package ch.sbb.adiguzaf.rssreader.provider;

import android.net.Uri;

import ch.sbb.adiguzaf.rssreader.RSS2Constants;

/**
 * The contract class for the feed provider.
 *
 * @author Kerem Adıgüzel
 * @since 08.05.2016
 */
public final class FeedsContract {
    static final String AUTHORITY = "ch.sbb.adiguzaf.rssreader.provider";
    private static final String CONTENT_BASE = String.format("content://%s/", AUTHORITY);

    // since Android is on a linux like system, everything is case sensitive
    public static final String FEEDS_TABLE = "feeds";
    public static final Uri FEEDS_PROVIDER_URI = Uri.parse(CONTENT_BASE + FEEDS_TABLE);
    public static final Uri FEEDS_PROVIDER_ID_URI = Uri.parse(CONTENT_BASE + FEEDS_TABLE + "/#");

    public static final String FEEDS_COLUMN_ID = "_id";
    public static final String FEEDS_COLUMN_TITLE = RSS2Constants.ITEM_TITLE_TAG;
    public static final String FEEDS_COLUMN_DESCRIPTION = RSS2Constants.ITEM_DESCRIPTION_TAG;
    public static final String FEEDS_COLUMN_LINK = RSS2Constants.ITEM_LINK_TAG;
    public static final String FEEDS_COLUMN_PUBLISHED_DATE = RSS2Constants.ITEM_PUBLISHED_DATE_TAG;

    private static final String TYPE_SUFFIX = AUTHORITY + "." + FEEDS_TABLE;
    public static final String FEEDS_TABLE_TYPE = "vnd.android.cursor.dir/vnd." + TYPE_SUFFIX;
    public static final String FEEDS_ITEM_TYPE = "vnd.android.cursor.item/vnd." + TYPE_SUFFIX;
}