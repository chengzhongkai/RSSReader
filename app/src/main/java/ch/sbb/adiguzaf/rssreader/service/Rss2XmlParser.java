package ch.sbb.adiguzaf.rssreader.service;

import android.content.ContentValues;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ch.sbb.adiguzaf.rssreader.provider.FeedsContract;

import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_DESCRIPTION_TAG;
import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_LINK_TAG;
import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_PUBLISHED_DATE_TAG;
import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_TAG;
import static ch.sbb.adiguzaf.rssreader.RSS2Constants.ITEM_TITLE_TAG;
import static java.util.Locale.ENGLISH;

public class Rss2XmlParser {

    private static final String DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, ENGLISH);

    public List<ContentValues> getFeeds(InputStream inputStream) throws IOException, SAXException,
            ParserConfigurationException, ParseException {
        NodeList channelChildNodes = getChannelChildNodes(inputStream);

        List<ContentValues> contentValuesList = new ArrayList<>();
        for (int i = 0; i < channelChildNodes.getLength(); i++) {
            Node channelChildNode = channelChildNodes.item(i);
            if (ITEM_TAG.equals(channelChildNode.getNodeName())) {
                NodeList item = channelChildNode.getChildNodes();
                contentValuesList.add(processRSSItem(item));
            }
        }
        return contentValuesList;
    }

    private NodeList getChannelChildNodes(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {
        Element rootElement = getXMLDocument(inputStream).getDocumentElement();
        // "Channel" child node is always at the root within the "rss" node
        Node channel = rootElement.getChildNodes().item(1);
        return channel.getChildNodes();
    }

    private Document getXMLDocument(InputStream inputStream) throws SAXException, IOException,
            ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        return documentBuilder.parse(inputStream);
    }

    /**
     * Processes the child nodes in the <i>item</i> tag and puts the values into a ContentValues
     * object.
     *
     * @param items the child nodes of the <i>item</i> tag
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
}