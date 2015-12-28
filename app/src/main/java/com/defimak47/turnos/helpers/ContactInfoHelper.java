package com.defimak47.turnos.helpers;

import android.text.TextUtils;
import android.util.Log;

import com.defimak47.turnos.model.ContactInfo;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzuriaga on 01/01/15.
 */
public class ContactInfoHelper {

    public static final String HTTP_IMAGE_URI_TEMPLATE = "http://www.edicomgroup.com/dms/team/%s.jpg";
    public static final String MAIL_EDICOMGROUP_TEMPLATE = "%s@edicomgroup.com";

    Gson gson = new Gson();

    public List<ContactInfo> readContactInfoArray (InputStream in) throws IOException {
        List<ContactInfo> contacts = new ArrayList<ContactInfo>();
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            contacts = readContactsArray(reader);
        } catch (IOException io) {
            Log.w("ContactInfoHelper", "readContactInfoArray  " + io.getMessage(), io);
        } finally {
            reader.close();
        }
        return contacts;
    }

    private List<ContactInfo> readContactsArray(JsonReader reader) throws IOException {
        List<ContactInfo> contacts = new ArrayList<ContactInfo>();

//        Type listType = new TypeToken<List<ContactInfo>>() {}.getType();
//        return gson.fromJson(reader, listType);
        reader.beginArray();
        while (reader.hasNext()) {
            contacts.add(readContactInfo(reader));
        }
        reader.endArray();
        return contacts;
    }

    private ContactInfo readContactInfo (JsonReader reader) throws IOException {
        ContactInfo contact = null;
        contact = gson.fromJson(reader, ContactInfo.class);
        if (TextUtils.isEmpty(contact.getEmail())) {
            contact.setEmail(String.format(MAIL_EDICOMGROUP_TEMPLATE, contact.getLogin()));
        }
        if (TextUtils.isEmpty(contact.getAlias())) {
            contact.setAlias(contact.getLogin());
        }
        return contact;

    }


}
