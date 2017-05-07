package com.defimak47.turnos.helpers;

import android.text.TextUtils;
import android.util.JsonReader;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.model.PhotoInfo;
import com.defimak47.turnos.model.PhotoRecord;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jzuriaga on 7/5/17.
 */

class PhotoInfoHelper extends AbstractBaseHelper<PhotoInfo>{

    public static final String MY_TURNOS_GOOGLE_SHEET_KEY = BuildConfig.TURNOS_GOOGLE_SHEET_KEY;
    public static final String HTTP_STATIC_SHIFT_URL = "https://spreadsheets.google.com/feeds/list/" + MY_TURNOS_GOOGLE_SHEET_KEY+ "/oqea05o/public/full?alt=json";
    public static final String SHIFT_FILE_NAME = "photo.json";

    public static final String JSON_ATOM_ENTRY_LOGIN_KEY = "gsx$login";
    public static final String JSON_ATOM_ENTRY_URL_KEY = "gsx$url";

    public PhotoInfo readPhotoInfo (InputStream in) throws JSONException, IOException {
        PhotoInfo photoInfo = new PhotoInfo();
        if (null==in) {
            return photoInfo;
        }
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            readPhotoInfo(reader, photoInfo);
            // } catch (Exception io) {
            //    Log.w("ContactInfoHelper", "readPhotoInfo  " + io.getMessage(), io);
        } finally {
            reader.close();
        }
        return photoInfo;
    }

    private void readPhotoInfo(JsonReader reader, PhotoInfo photo)  throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_FEED_KEY)) {
                readFeed(reader, photo);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    protected void readEntryRow(JsonReader reader, PhotoInfo photo) throws IOException {
        PhotoRecord record = new PhotoRecord();
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_ENTRY_ID_KEY)) {
                record.setId(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_UPDATED_KEY)) {
                record.setLastupdated(getTextAsDate(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_LOGIN_KEY)) {
                record.setLogin(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_URL_KEY)) {
                record.setUrl(getTextAsString(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        photo.addPhoto(record);
    }

}
