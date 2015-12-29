package com.defimak47.turnos.helpers;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

import com.defimak47.turnos.model.StuffInfo;
import com.defimak47.turnos.model.StuffRecord;
import com.defimak47.turnos.utils.IsoDate;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jzuriaga on 03/10/15.
 */
public class StuffInfoHelper {


    public static final String HTTP_STATIC_STUFF_URL = "https://spreadsheets.google.com/feeds/list/1D_7igYenGDjG-_nhh1miPuKRvVeR5-BpQxQR9YE6O-A/o39u79f/public/full?alt=json";
    public static final String CONTACT_FILE_NAME = "stuff.json";
    public static final String HTTP_IMAGE_URI_TEMPLATE = "http://www.edicomgroup.com/dms/team/%s.jpg";
    public static final String MAIL_EDICOMGROUP_TEMPLATE = "%s@edicomgroup.com";

    public static final String JSON_ATOM_FEED_KEY = "feed";
    public static final String JSON_ATOM_FEED_UPDATED_KEY = "updated";
    public static final String JSON_ATOM_FEED_LINK_KEY = "link";
    public static final String JSON_ATOM_FEED_LINK_HREF_KEY = "href";
    public static final String JSON_ATOM_TEXT_KEY = "$t";
    public static final String JSON_ATOM_FEED_AUTHOR_KEY = "author";
    public static final String JSON_ATOM_AUTHOR_NAME_KEY = "name";
    public static final String JSON_ATOM_AUTHOR_EMAIL_KEY = "email";
    public static final String JSON_ATOM_FEED_TOTAL_RESULTS_KEY = "openSearch$totalResults";
    public static final String JSON_ATOM_ENTRY_LOGIN_KEY = "gsx$login";
    public static final String JSON_ATOM_FEED_ENTRY_KEY = "entry";
    public static final String JSON_ATOM_ENTRY_ID_KEY = "id";
    public static final String JSON_ATOM_ENTRY_UPDATED_KEY = "updated";
    public static final String JSON_ATOM_ENTRY_ALIAS_KEY = "gsx$alias";
    public static final String JSON_ATOM_ENTRY_NAME_KEY = "gsx$name";
    public static final String JSON_ATOM_ENTRY_POSITION_KEY = "gsx$position";

    public StuffInfo readStuffInfo (InputStream in) throws JSONException, IOException {
        StuffInfo stuffinfo = new StuffInfo();
        if (null==in) {
            return stuffinfo;
        }
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            stuffinfo = readStuffInfo(reader);
        // } catch (Exception io) {
        //    Log.w("ContactInfoHelper", "readStuffInfo  " + io.getMessage(), io);
        } finally {
            reader.close();
        }
        return stuffinfo;
    }

    private StuffInfo readStuffInfo(JsonReader reader) throws IOException {
        StuffInfo stuff = new StuffInfo();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_FEED_KEY)) {
                stuff = readFeed(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return stuff;
    }

    private StuffInfo readFeed(JsonReader reader) throws IOException {
        StuffInfo stuff = new StuffInfo();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_FEED_UPDATED_KEY)) {
                stuff.setLastupdated(getTextAsDate(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_FEED_LINK_KEY)) {
                stuff.setLinkToSourde(readLinkToSource(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_FEED_AUTHOR_KEY)) {
                readAuthor(reader, stuff);
            } else if (TextUtils.equals(name, JSON_ATOM_FEED_TOTAL_RESULTS_KEY)) {
                stuff.setTotalResults(getTextAsInt(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_FEED_ENTRY_KEY)) {
                stuff.setStuff(readFeedEntries(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return stuff;
    }

    private List<StuffRecord> readFeedEntries(JsonReader reader) throws IOException {
        List<StuffRecord> records = new ArrayList<>();
        reader.beginArray();
        while(reader.hasNext()) {
            StuffRecord record = readEntryRow(reader);
            records.add(record);
        }
        reader.endArray();
        return records;
    }

    private StuffRecord readEntryRow(JsonReader reader) throws IOException {
        StuffRecord record = new StuffRecord();
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_ENTRY_ID_KEY)) {
                record.setId(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_UPDATED_KEY)) {
                record.setLastupdated(getTextAsDate(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_LOGIN_KEY)) {
                record.setLogin(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_ALIAS_KEY)) {
                record.setAlias(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_NAME_KEY)) {
                record.setName(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_POSITION_KEY)) {
                record.setPosition(getTextAsString(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        if (TextUtils.isEmpty(record.getEmail())) {
            record.setEmail(String.format(MAIL_EDICOMGROUP_TEMPLATE, record.getLogin()));
        }
        return record;
    }

    private void readAuthor(JsonReader reader, StuffInfo stuff) throws IOException {
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

        stuff.setAuthorName(authorName);
        stuff.setAuthorEmail(authorEmail);
    }

    private String readLinkToSource(JsonReader reader) throws IOException {
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

    private String getTextAsString(JsonReader reader) throws IOException {
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

    private Date getTextAsDate(JsonReader reader) throws IOException {
        String stringDate = getTextAsString(reader);
        return IsoDate.stringToDate(stringDate, IsoDate.DATE_TIME);
    }

    private Integer getTextAsInt (JsonReader reader) throws IOException {
        String stringInt = getTextAsString(reader);
        return Integer.parseInt(stringInt);
    }


    // List<StuffRecord> records = new ArrayList<>();


}
