package com.defimak47.turnos.helpers;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import com.defimak47.turnos.R;
import com.defimak47.turnos.model.Shift;
import com.defimak47.turnos.view.ShiftActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLooper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.robolectric.Shadows.shadowOf;

/**
 * Created by jzuriaga on 02/05/15.
 */
public class ShiftHelperTest {

    public static final String DUMMY_JSON =
            "[{\"index\":0,\"year\":\"2015\",\"week\":\"1\",\"start_date\":\"29/12/2014 16:00:00\",\"sprint\":\"1\",\"pairing\":\"1\","+
            "\"P1_IMASDMX1\":\"rtribaldos\",\"P1_IMASDMX2\":\"jvalbert\",\"P2_IMASDMX1\":\"rtribaldos\",\"P2_IMASDMX2\":\"jvalbert\"}," +
            "{\"index\":1,\"year\":\"2015\",\"week\":\"2\",\"start_date\":\"05/01/2015 16:00:00\",\"sprint\":\"1\",\"pairing\":\"2\","+
            "\"P1_IMASDMX1\":\"jescobar\",\"P1_IMASDMX2\":\"igalarza\",\"P2_IMASDMX1\":\"jescobar\",\"P2_IMASDMX2\":\"igalarza\"}," +
            "{\"index\":2,\"year\":\"2015\",\"week\":\"3\",\"start_date\":\"12/01/2015 16:00:00\",\"sprint\":\"2\",\"pairing\":\"3\","+
            "\"P1_IMASDMX1\":\"fjruiz\",\"P1_IMASDMX2\":\"jcmontero\",\"P2_IMASDMX1\":\"fjruiz\",\"P2_IMASDMX2\":\"jcmontero\"},"+
            "{\"index\":3,\"year\":\"2015\",\"week\":\"4\",\"start_date\":\"19/01/2015 16:00:00\",\"sprint\":\"2\",\"pairing\":\"4\","+
            "\"P1_IMASDMX1\":\"jzuriaga\",\"P1_IMASDMX2\":\"jca\\u00f1adillas\",\"P2_IMASDMX1\":\"jzuriaga\",\"P2_IMASDMX2\":\"jca\\u00f1adillas\"},"+
            "{\"index\":4,\"year\":\"2015\",\"week\":\"5\",\"start_date\":\"26/01/2015 16:00:00\",\"sprint\":\"3\",\"pairing\":\"5\","+
            "\"P1_IMASDMX1\":\"igalarza\",\"P1_IMASDMX2\":\"rtribaldos\",\"P2_IMASDMX1\":\"igalarza\",\"P2_IMASDMX2\":\"rtribaldos\"}]";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReadShiftArray() throws Exception {
        ShiftHelper helper = new ShiftHelper();
        assertNotNull(helper);

        InputStream in = getRawResourceInputStream();
        List<Shift> shifts = helper.readShiftArray(in);

        assertNotNull(shifts);
        assertTrue(shifts.size() > 0);
        assertNotNull(shifts.get(0).getIndex());
    }

    @Test
    public void testReadShiftArrayDummy()  throws Exception {
        ShiftHelper helper = new ShiftHelper();

        InputStream in = getDummyInputStream();
        List<Shift> shifts = helper.readShiftArray(in);

        assertNotNull(shifts);
        assertTrue(shifts.size() == 5);
        assertEquals(shifts.get(0).getIndex(), 0);
        assertEquals(shifts.get(0).getYear(), 2015);
        assertEquals(shifts.get(1).getIndex(), 1);
        assertEquals(shifts.get(1).getImasdMx1(), "jescobar");
    }

    @Ignore
    private InputStream getRawResourceInputStream () {
        ShiftActivity activity = Robolectric.buildActivity(ShiftActivity.class).create().get();
        assertNotNull(activity);
        InputStream in = activity.getResources().openRawResource(R.raw.turnos);
        assertNotNull(in);
        return in;
    }

    @Ignore
    private InputStream getDummyInputStream () throws Exception {
        return new ByteArrayInputStream(DUMMY_JSON.getBytes());
    }

}