package ch.sbb.adiguzaf.rssreader;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_DESCRIPTION_TAG;
import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_LINK_TAG;
import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_PUBLISHED_DATE_TAG;
import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_TAG;
import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_TITLE_TAG;

public class RSSFeedService extends IntentService {

    private static final String mName = "RSSFeedService";
    private static final String DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
    private String address;

    public RSSFeedService() {
        super(mName);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        address = intent.getStringExtra(MainActivity.EXTRA_RSS_URL);
        Log.d(mName, "Address is: " + address);

        // clear db
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(FeedsContract.FEEDS_PROVIDER_URI, null, null);

        try {
            NodeList channelChildNodes = getChannelChildNodes();

            for (int i = 0; i < channelChildNodes.getLength(); i++) {
                Node channelChildNode = channelChildNodes.item(i);
                if (ITEM_TAG.equals(channelChildNode.getNodeName())) {
                    NodeList item = channelChildNode.getChildNodes();
                    ContentValues values = processRSSItem(item);
                    contentResolver.insert(FeedsContract.FEEDS_PROVIDER_URI, values);
                    Log.d(mName, "Feed is inserted: " + values);
                }
            }

            showToastAfterwards(R.string.feeds_loaded);

        } catch (Exception e) {
            Log.e(mName, "an error occurred", e);
            showToastAfterwards(R.string.feeds_not_loaded);
        }
    }

    private void showToastAfterwards(final int resId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RSSFeedService.this, resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Processes the child nodes in the <i>item</i> tag and puts the values into the content
     * provider.
     *
     * @param items           the child nodes of the <i>item</i> tag
     * @throws ParseException if the format of the publication date didn't conform to
     *                        {@link #DATE_FORMAT_PATTERN}
     */
    private ContentValues processRSSItem(NodeList items)
            throws ParseException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            switch (item.getNodeName()) {
                case ITEM_TITLE_TAG:
                    values.put(FeedsContract.FEEDS_COLUMN_TITLE, item.getTextContent());
                    break;
                case ITEM_DESCRIPTION_TAG:
                    values.put(FeedsContract.FEEDS_COLUMN_DESCRIPTION, item.getTextContent());
                    break;
                case ITEM_PUBLISHED_DATE_TAG:
                    values.put(FeedsContract.FEEDS_COLUMN_PUBLISHED_DATE,
                            parseDate(item.getTextContent()));
                    break;
                case ITEM_LINK_TAG:
                    values.put(FeedsContract.FEEDS_COLUMN_LINK, item.getTextContent());
                    break;
            }
        }
        return values;
    }

    private Long parseDate(String date) throws ParseException {
        return dateFormat.parse(date).getTime();
    }

    private NodeList getChannelChildNodes() throws ParserConfigurationException, SAXException,
            IOException {
        Element rootElement = getXMLDocument().getDocumentElement();
        // "Channel" child node is always at the root within the "rss" node
        Node channel = rootElement.getChildNodes().item(1);
        return channel.getChildNodes();
    }

    private Document getXMLDocument() throws SAXException, IOException,
            ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        return documentBuilder.parse(getInputStream());
    }

    private InputStream getInputStream() throws IOException {
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getInputStream();
    }
}