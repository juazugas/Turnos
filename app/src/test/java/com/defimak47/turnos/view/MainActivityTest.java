package com.defimak47.turnos.view;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class MainActivityTest {

    @Test
    public void testActivityStart () {
        Activity activity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(activity);
    }

    @Test
    public void testActivityTitle () {
        Activity activity = Robolectric.setupActivity(MainActivity.class);
        assertEquals(activity.getTitle(), activity.getText(R.string.app_name));
    }

    @Test
    public void testActivityIsCreated () {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        assertNotNull(activity);
        assertEquals(activity.getTitle(), activity.getText(R.string.app_name));
        assertNotNull(activity.findViewById(R.id.cardList));
    }

    @Test
    public void testActivityIsShown () {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().visible().get();
        RecyclerView recList = (RecyclerView) activity.findViewById(R.id.cardList);
        assertNotNull(recList);
        assertTrue(recList.getChildCount() > 0);
    }

    @Test
    public void testActivityAccessRawResource () {
        Activity activity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(activity);
        InputStream in = activity.getResources().openRawResource(R.raw.lusers);
        try {
            assertTrue(in.available() > 0);
        } catch (IOException ioe) {
            fail(ioe.getMessage());
        }
    }

    @Test
    public void testActivityShouldInflateMenu() throws Exception {
        final MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();

        final Menu menu = shadowOf(activity).getOptionsMenu();
        assertNotNull(menu);
        assertEquals(menu.findItem(R.id.action_settings).getTitle(),
                activity.getString(R.string.action_settings));
        assertEquals(menu.findItem(R.id.action_shifts).getTitle(),
                activity.getString(R.string.action_shifts));
    }

}