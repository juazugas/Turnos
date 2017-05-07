package com.defimak47.turnos.helpers;

import android.app.Activity;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.R;
import com.defimak47.turnos.model.ContactInfo;
import com.defimak47.turnos.view.MainActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = { 19, 21, 23 } )
public class ContactInfoHelperTest {

    private static final String DUMMY_JSON = "[{\"login\":\"dummy\",\"name\":\"Dummy Dummie\",\"alias\":\"dum\",\"position\":\"Dummy Position\"},"+
                                              "{\"login\":\"redummy\",\"name\":\"Name Dummie\",\"alias\":\"red\",\"position\":\"Red Position\"}]";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReadContactInfoArrayRawResources() throws Exception {
        ContactInfoHelper helper = new ContactInfoHelper();
        assertNotNull(helper);

        InputStream in = getRawResourceInputStream();
        List<ContactInfo> contacts = helper.readContactInfoArray(in);

        assertNotNull(contacts);
        assertTrue(contacts.size() > 0);
        assertNotNull(contacts.get(0).getLogin());
    }

    @Test
    public void testReadContactInfoArrayDummy ()  throws Exception {
        ContactInfoHelper helper = new ContactInfoHelper();

        InputStream in = getDummyInputStream();
        List<ContactInfo> contacts = helper.readContactInfoArray(in);

        assertNotNull(contacts);
        assertTrue(contacts.size() == 2);
        assertEquals(contacts.get(0).getLogin(), "dummy");
        assertEquals(contacts.get(0).getAlias(), "dum");
        assertEquals(contacts.get(1).getLogin(), "redummy");
        assertEquals(contacts.get(1).getAlias(), "red");
    }

    @Ignore
    private InputStream getRawResourceInputStream () {
        Activity activity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(activity);
        InputStream in = activity.getResources().openRawResource(R.raw.lusers);
        assertNotNull(in);
        return in;
    }

    @Ignore
    private InputStream getDummyInputStream () throws Exception {
        return new ByteArrayInputStream(DUMMY_JSON.getBytes());
    }

}