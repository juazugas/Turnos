package com.defimak47.turnos.helpers;

import android.text.TextUtils;
import android.util.JsonReader;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.model.StuffInfo;
import com.defimak47.turnos.model.StuffRecord;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jzuriaga on 03/10/15.
 */
public class StuffInfoHelper extends AbstractBaseHelper<StuffInfo> {

    public static final String MY_TURNOS_GOOGLE_SHEET_KEY = BuildConfig.TURNOS_GOOGLE_SHEET_KEY;
    public static final String HTTP_STATIC_STUFF_URL = "https://spreadsheets.google.com/feeds/list/" + MY_TURNOS_GOOGLE_SHEET_KEY+ "/o39u79f/public/full?alt=json";
    public static final String CONTACT_FILE_NAME = "stuff.json";
    public static final String HTTP_IMAGE_URI_TEMPLATE = "http://www.edicomgroup.com/dms/team/%s.jpg";
    public static final String MAIL_EDICOMGROUP_TEMPLATE = "%s@edicomgroup.com";


    public static final String JSON_ATOM_ENTRY_LOGIN_KEY = "gsx$login";
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
            readStuffInfo(reader, stuffinfo);
        // } catch (Exception io) {
        //    Log.w("ContactInfoHelper", "readStuffInfo  " + io.getMessage(), io);
        } finally {
            reader.close();
        }
        return stuffinfo;
    }

    protected void readStuffInfo(JsonReader reader, StuffInfo stuff) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_FEED_KEY)) {
                readFeed(reader, stuff);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    protected void readEntryRow(JsonReader reader, StuffInfo stuff) throws IOException {
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
        stuff.addStuff(record);
    }

}
