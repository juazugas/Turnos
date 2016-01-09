package com.defimak47.turnos.helpers;

import android.text.TextUtils;
import android.util.JsonReader;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.model.Shift;
import com.defimak47.turnos.model.ShiftInfo;
import com.defimak47.turnos.model.ShiftRecord;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by jzuriaga on 7/1/16.
 */
public class ShiftInfoHelper extends AbstractBaseHelper<ShiftInfo> {

    public static final String MY_TURNOS_GOOGLE_SHEET_KEY = BuildConfig.TURNOS_GOOGLE_SHEET_KEY;
    public static final String HTTP_STATIC_SHIFT_URL = "https://spreadsheets.google.com/feeds/list/" + MY_TURNOS_GOOGLE_SHEET_KEY+ "/od6/public/full?alt=json";
    public static final String SHIFT_FILE_NAME = "shift.json";


    public static final String JSON_ATOM_ENTRY_YEAR_KEY = "gsx$year";
    public static final String JSON_ATOM_ENTRY_WEEK_KEY = "gsx$week";
    public static final String JSON_ATOM_ENTRY_DATE_KEY = "gsx$date";
    public static final String JSON_ATOM_ENTRY_SPRINT_KEY = "gsx$sprint";
    public static final String JSON_ATOM_ENTRY_EURO_KEY = "gsx$eu";
    public static final String JSON_ATOM_ENTRY_MEX1_KEY = "gsx$mx1";
    public static final String JSON_ATOM_ENTRY_MEX2_KEY = "gsx$mx2";

    public ShiftInfo readShiftInfo (InputStream in) throws JSONException, IOException {
        ShiftInfo shiftinfo = new ShiftInfo();
        if (null==in) {
            return shiftinfo;
        }
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            readShiftInfo(reader, shiftinfo);
            // } catch (Exception io) {
            //    Log.w("ContactInfoHelper", "readShiftInfo  " + io.getMessage(), io);
        } finally {
            reader.close();
        }
        return shiftinfo;
    }

    protected void readShiftInfo(JsonReader reader, ShiftInfo shiftInfo) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_FEED_KEY)) {
                readFeed(reader, shiftInfo);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    protected void readEntryRow(JsonReader reader, ShiftInfo shiftInfo) throws IOException {
        ShiftRecord record = new ShiftRecord();
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (TextUtils.equals(name, JSON_ATOM_ENTRY_ID_KEY)) {
                record.setId(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_UPDATED_KEY)) {
                record.setLastupdated(getTextAsDate(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_YEAR_KEY)) {
                record.setYear(getTextAsInt(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_WEEK_KEY)) {
                record.setWeek(getTextAsInt(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_DATE_KEY)) {
                record.setStartDate(getTextAsDate(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_SPRINT_KEY)) {
                String sprintStr = getTextAsString(reader);
                int sprint = 0;
                if (null!=sprintStr) {
                    sprint = Integer.parseInt(sprintStr.replace("#", ""));
                }
                record.setSprint(sprint);
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_EURO_KEY)) {
                record.setImasdEu(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_MEX1_KEY)) {
                record.setImasdMx1(getTextAsString(reader));
            } else if (TextUtils.equals(name, JSON_ATOM_ENTRY_MEX2_KEY)) {
                record.setImasdMx2(getTextAsString(reader));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        shiftInfo.addShift(record);
    }

    public static boolean isCurrentShift (Shift shift) {
        Calendar now = Calendar.getInstance();
        int weekOfYear = now.get(Calendar.WEEK_OF_YEAR);
        int year = now.get(Calendar.YEAR);
        if (weekOfYear == 1 && now.get(Calendar.WEEK_OF_MONTH)>2) { // Its december last week of month
            year = year + 1;
        }
        return shift.getWeek()==weekOfYear && shift.getYear()==year;
    }
}
