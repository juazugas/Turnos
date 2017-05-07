package com.defimak47.turnos.view;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.R;
import com.defimak47.turnos.shadows.FakeContentResolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by jzuriaga on 11/04/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = { 19, 21, 23 }, shadows = {FakeContentResolver.class})
public class ShiftActivityTest {

    @Before
    public void setup() {
//        ContentResolver mockResolver =  RuntimeEnvironment.application.getContentResolver();
//        final ShadowContentResolver shadowContentResolver = shadowOf(mockResolver);

    }

    @Test
    public void testActivityStart() {
        Activity activity = Robolectric.setupActivity(ShiftActivity.class);
        assertNotNull(activity);
    }

    @Test
    public void testActivityTitle() {
        Activity activity = Robolectric.setupActivity(ShiftActivity.class);
        assertEquals(activity.getTitle(), activity.getText(R.string.title_activity_shift));
    }

    @Test
    public void testActivityIsShown() {
        Activity activity = Robolectric.buildActivity(ShiftActivity.class).create().start().resume().visible().get();
        RecyclerView recList = (RecyclerView) activity.findViewById(R.id.shiftList);
        assertNotNull(recList);
        assertTrue(recList.getChildCount() > 0);
    }

    @Test
    public void testActivityAccessRawResource() {
        Activity activity = Robolectric.setupActivity(ShiftActivity.class);
        assertNotNull(activity);
        InputStream in = activity.getResources().openRawResource(R.raw.turnos);
        try {
            assertTrue(in.available() > 0);
        } catch (IOException ioe) {
            fail(ioe.getMessage());
        }
    }

    @Test
    public void testOnCreatePanelMenu() {
        SearchManager searchManager = (SearchManager) RuntimeEnvironment.application.getSystemService(Context.SEARCH_SERVICE);
        assertNotNull(searchManager);
    }

    @Test
    public void testActivityShouldInflateMenu() throws Exception {
        final ShiftActivity activity = Robolectric.setupActivity(ShiftActivity.class);

        final Menu menu = shadowOf(activity).getOptionsMenu();
        assertNotNull(menu);
        assertEquals(menu.findItem(R.id.action_settings).getTitle(),
                activity.getString(R.string.action_settings));
        assertEquals(menu.findItem(R.id.action_team).getTitle(),
                activity.getString(R.string.action_team));
    }

}