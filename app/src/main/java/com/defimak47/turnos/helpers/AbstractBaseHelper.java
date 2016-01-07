package com.defimak47.turnos.helpers;

import android.text.TextUtils;
import android.util.JsonReader;

import com.defimak47.turnos.model.BaseInfo;
import com.defimak47.turnos.utils.IsoDate;

import java.io.IOException;
import java.util.Date;

/**
 * Created by jzuriaga on 7/1/16.
 */
public abstract class AbstractBaseHelper<T extends BaseInfo> {

    public static final String JSON_ATOM_FEED_KEY = "feed";
    public static final String JSON_ATOM_FEED_UPDATED_KEY = "updated";
    public static final String JSON_ATOM_FEED_LINK_KEY = "link";
    public static final String JSON_ATOM_FEED_LINK_HREF_KEY = "href";
    public static final String JSON_ATOM_TEXT_KEY = "$t";
    public static final String JSON_ATOM_FEED_AUTHOR_KEY = "author";
    public static final String JSON_ATOM_AUTHOR_NAME_KEY = "name";
    public static final String JSON_ATOM_AUTHOR_EMAIL_KEY = "email";
    public static final String JSON_ATOM_FEED_TOTAL_RESULTS_KEY = "openSearch$totalResults";

    public static final String JSON_ATOM_FEED_ENTRY_KEY = "entry";
    public static final String JSON_ATOM_ENTRY_ID_KEY = "id";
    public static final String JSON_ATOM_ENTRY_UPDATED_KEY = "updated";

    protected void readFeed(JsonReader reader, T instance) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_FEED_UPDATED_KEY)) {
                instance.setLastupdated(getTextAsDate(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_FEED_LINK_KEY)) {
                instance.setLinkToSource(readLinkToSource(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_FEED_AUTHOR_KEY)) {
                readAuthor(reader, instance);
            } else if (TextUtils.equals(name, JSON_ATOM_FEED_TOTAL_RESULTS_KEY)) {
                instance.setTotalResults(getTextAsInt(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_FEED_ENTRY_KEY)) {
                readFeedEntries(reader, instance);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    protected void readFeedEntries(JsonReader reader, T instance) throws IOException {
        reader.beginArray();
        while(reader.hasNext()) {
            readEntryRow(reader, instance);
        }
        reader.endArray();
    }

    protected abstract void readEntryRow(JsonReader reader, T instance) throws IOException;

    protected void readAuthor(JsonReader reader, T instance) throws IOException {
        String authorName = null;
        String authorEmail = null;
        reader.beginArray();
        while(reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                if (TextUtils.isEmpty(authorName) || TextUtils.isEmpty(authorEmail)) {
                    String name = reader.nextName();
                    if (TextUtils.equals(name, JSON_ATOM_AUTHOR_NAME_KEY)) {
                        authorName = getTextAsString(reader);
                    } else if (TextUtils.equals(name, JSON_ATOM_AUTHOR_EMAIL_KEY)) {
                        authorEmail = getTextAsString(reader);
                    } else {
                        reader.skipValue();
                    }
                }
            }
            reader.endObject();
        }
        reader.endArray();

        instance.setAuthorName(authorName);
        instance.setAuthorEmail(authorEmail);
    }

    protected String readLinkToSource(JsonReader reader) throws IOException {
        String href = null;
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (TextUtils.equals(name, JSON_ATOM_FEED_LINK_HREF_KEY) && TextUtils.isEmpty(href)) {
                    href=reader.nextString();
                    break;
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();
        return href;
    }

    protected String getTextAsString(JsonReader reader) throws IOException {
        String result = null;
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_TEXT_KEY)) {
                result = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return result;
    }

    protected Date getTextAsDate(JsonReader reader) throws IOException {
        String stringDate = getTextAsString(reader);
        return IsoDate.stringToDate(stringDate, IsoDate.DATE_TIME);
    }

    protected Integer getTextAsInt (JsonReader reader) throws IOException {
        String stringInt = getTextAsString(reader);
        return Integer.parseInt(stringInt);
    }

}
