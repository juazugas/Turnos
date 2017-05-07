package com.defimak47.turnos.view;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for Settings activity.
 *
 * @see com.defimak47.turnos.view.SettingsActivity
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SettingsActivityTest {

    @Test
    public void testActivityStart () {
        Activity activity = Robolectric.setupActivity(SettingsActivity.class);
        assertNotNull(activity);
    }

    @Test
    public void testActivityTitle () {
        Activity activity = Robolectric.setupActivity(SettingsActivity.class);
        assertEquals(activity.getTitle(), activity.getText(R.string.title_activity_settings));
    }

    @Test
    public void testActivityIsCreated () {
        Activity activity = Robolectric.buildActivity(SettingsActivity.class).create().get();
        assertNotNull(activity);
        assertEquals(activity.getTitle(), activity.getText(R.string.title_activity_settings));
        assertNotNull(activity.findViewById(R.id.cardList));
    }

    @Test
    public void testActivityIsShown () {
        Activity activity = Robolectric.buildActivity(SettingsActivity.class).create().start().resume().visible().get();
        RecyclerView recList = (RecyclerView) activity.findViewById(R.id.cardList);
        assertNotNull(recList);
        assertTrue(recList.getChildCount() > 0);
    }
}
