package com.defimak47.turnos.helpers;

import android.util.Log;

import com.defimak47.turnos.model.Shift;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzuriaga on 02/01/15.
 */
public class ShiftHelper {

    public static final String JSON_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    Gson gson;

    public ShiftHelper () {
        gson = new GsonBuilder()
                .setDateFormat(JSON_DATE_FORMAT)
                .create();
    }

    public List<Shift> readShiftArray (InputStream in) throws IOException {
        List<Shift> shifts = new ArrayList<Shift>();
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            shifts = readShiftsArray(reader);
        } catch (Exception io) {
            Log.w("ShiftHelper", "readShiftArray  " + io.getMessage(), io);
        } finally {
            reader.close();
        }
        return shifts;
    }

    private List<Shift> readShiftsArray(JsonReader reader) throws IOException {
        List<Shift> shifts = new ArrayList<Shift>();

//        Type listType = new TypeToken<List<Shift>>() {}.getType();
//        return gson.fromJson(reader, listType);
        reader.beginArray();
        while (reader.hasNext()) {
            shifts.add(readShift(reader));
        }
        reader.endArray();
        return shifts;
    }

    private Shift readShift (JsonReader reader) throws IOException {
        Shift shift = null;
        shift = gson.fromJson(reader, Shift.class);
        return shift;

    }

}
