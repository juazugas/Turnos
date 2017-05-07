package com.defimak47.turnos.helpers;

import android.app.Activity;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.R;
import com.defimak47.turnos.model.ShiftInfo;
import com.defimak47.turnos.utils.IsoDate;
import com.defimak47.turnos.utils.NetworkUtils;
import com.defimak47.turnos.view.ShiftActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = { 19, 21, 23})
public class ShiftInfoHelperTest {

    public static final String LINK_TO_SOURCE = "https://docs.google.com/spreadsheets/d/PUBLIC_KEY/pubhtml";
    public static final String UPDATED        = "2015-09-29T08:53:38.068Z";
    public static final String AUTHOR_NAME    = "memo";
    public static final String AUTHOR_EMAIL   = "memo@example.com";
    public static final String HTTP_LINK_TO_SOURCE = "https://docs.google.com/spreadsheets/d/%s/pubhtml";
    private static final String MY_TURNOS_GOOGLE_SHEET_KEY = BuildConfig.TURNOS_GOOGLE_SHEET_KEY;

    private static final String DUMMY_JSON =
            "{\"version\":\"1.0\",\"encoding\":\"UTF-8\",\"feed\":{\"xmlns\":\"http://www.w3.org/2005/Atom\",\"xmlns$openSearch\":\"http://a9.com/-/spec/opensearchrss/1.0/\",\"xmlns$gsx\":\"http://schemas.google.com/spreadsheets/2006/extended\",\"id\":{\"$t\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/od6/public/full\"}\n" +
                    ",\"updated\":{\"$t\":\"2015-09-29T08:53:38.068Z\"}\n" +
                    ",\"category\":[{\"scheme\":\"http://schemas.google.com/spreadsheets/2006\",\"term\":\"http://schemas.google.com/spreadsheets/2006#list\"}]\n" +
                    ",\"title\":{\"type\":\"text\",\"$t\":\"shifts\"}\n" +
                    ",\"link\":[{\"rel\":\"alternate\",\"type\":\"application/atom+xml\",\"href\":\"https://docs.google.com/spreadsheets/d/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/pubhtml\"}\n" +
                    ",{\"rel\":\"http://schemas.google.com/g/2005#feed\",\"type\":\"application/atom+xml\",\"href\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/od6/public/full\"}\n" +
                    ",{\"rel\":\"http://schemas.google.com/g/2005#post\",\"type\":\"application/atom+xml\",\"href\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/od6/public/full\"}\n" +
                    ",{\"rel\":\"self\",\"type\":\"application/atom+xml\",\"href\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/od6/public/full?alt\u003djson\"}]\n" +
                    ",\"author\":[{\"name\":{\"$t\":\"memo\"}\n" +
                    ",\"email\":{\"$t\":\"memo@example.com\"}}]\n" +
                    ",\"openSearch$totalResults\":{\"$t\":\"64\"}\n" +
                    ",\"openSearch$startIndex\":{\"$t\":\"1\"}\n" +
                    ",\"entry\":[{\"id\":{\"$t\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/od6/public/full/cokwr\"}\n" +
                    ",\"updated\":{\"$t\":\"2015-12-17T17:30:00.832Z\"}\n" +
                    ",\"category\":[{\"scheme\":\"http://schemas.google.com/spreadsheets/2006\",\"term\":\"http://schemas.google.com/spreadsheets/2006#list\"}]\n" +
                    ",\"title\":{\"type\":\"text\",\"$t\":\"2015\"}\n" +
                    ",\"content\":{\"type\":\"text\",\"$t\":\"week: 1, date: 29/12/2014, sprint: #1, eu: dummy, mx1: newbie, mx2: geeky\"}\n" +
                    ",\"link\":[{\"rel\":\"self\",\"type\":\"application/atom+xml\",\"href\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/od6/public/full/cokwr\"}]\n" +
                    ",\"gsx$year\":{\"$t\":\"2015\"}\n" +
                    ",\"gsx$week\":{\"$t\":\"1\"}\n" +
                    ",\"gsx$date\":{\"$t\":\"2014-12-29\"}\n" +
                    ",\"gsx$sprint\":{\"$t\":\"#1\"}\n" +
                    ",\"gsx$eu\":{\"$t\":\"dummy\"}\n" +
                    ",\"gsx$mx1\":{\"$t\":\"newbie\"}\n" +
                    ",\"gsx$mx2\":{\"$t\":\"geeky\"}}\n" +
                    ",{\"id\":{\"$t\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/od6/public/full/cpzh4\"}\n" +
                    ",\"updated\":{\"$t\":\"2015-12-17T17:30:00.832Z\"}\n" +
                    ",\"category\":[{\"scheme\":\"http://schemas.google.com/spreadsheets/2006\",\"term\":\"http://schemas.google.com/spreadsheets/2006#list\"}]\n" +
                    ",\"title\":{\"type\":\"text\",\"$t\":\"2015\"}\n" +
                    ",\"content\":{\"type\":\"text\",\"$t\":\"week: 2, date: 5/01/2015, sprint: #1, eu: fulano, mx1: mengano, mx2: zutano\"}\n" +
                    ",\"link\":[{\"rel\":\"self\",\"type\":\"application/atom+xml\",\"href\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/od6/public/full/cpzh4\"}]\n" +
                    ",\"gsx$year\":{\"$t\":\"2015\"}\n" +
                    ",\"gsx$week\":{\"$t\":\"2\"}\n" +
                    ",\"gsx$date\":{\"$t\":\"2015-01-05\"}\n" +
                    ",\"gsx$sprint\":{\"$t\":\"#1\"}\n" +
                    ",\"gsx$eu\":{\"$t\":\"fulano\"}\n" +
                    ",\"gsx$mx1\":{\"$t\":\"mengano\"}\n" +
                    ",\"gsx$mx2\":{\"$t\":\"zutano\"}}\n]}}";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReadShiftInfoArrayRawResources() throws Exception {
        ShiftInfo shiftinfo = null;
        InputStream in = null;
        Date lastupdated = null;
        _given: {
            in = getRawResourceInputStream();
            lastupdated = IsoDate.stringToDate(UPDATED, IsoDate.DATE_TIME);
        }

        _then: {
            shiftinfo = new ShiftInfoHelper().readShiftInfo(in);
        }

        _expect: {
            assertNotNull(shiftinfo);
            assertEquals(lastupdated, shiftinfo.getLastupdated());
            String httpLinkToSource = String.format(HTTP_LINK_TO_SOURCE, MY_TURNOS_GOOGLE_SHEET_KEY);
            assertEquals(httpLinkToSource, shiftinfo.getLinkToSource());
            assertEquals("memo", shiftinfo.getAuthorName());
            assertEquals("memo@gmail.com", shiftinfo.getAuthorEmail());
            assertEquals(shiftinfo.getShift().get(0).getYear(), 2015);
            assertEquals(shiftinfo.getShift().get(0).getWeek(), 1);
            assertEquals(shiftinfo.getShift().get(0).getImasdEu(), "ialonso");
            assertEquals(shiftinfo.getShift().get(0).getImasdMx1(), "rtribaldos");
            assertEquals(shiftinfo.getShift().get(1).getYear(), 2015);
            assertEquals(shiftinfo.getShift().get(1).getWeek(), 2);
            assertEquals(shiftinfo.getShift().get(1).getImasdEu(), "oalbert");
            assertEquals(shiftinfo.getShift().get(1).getImasdMx1(), "jescobar");
        }
    }

    @Test
    public void testReadShiftInfoHttpSpreadSheetResource() throws Exception {
        ShiftInfo shiftinfo = null;
        InputStream in = null;
        Date lastupdated = null;
        _given: {
            in = getHttpSpreadSheetInputStream();
            lastupdated = IsoDate.stringToDate(UPDATED, IsoDate.DATE_TIME);
        }

        _then: {
            shiftinfo = new ShiftInfoHelper().readShiftInfo(in);
        }

        _expect: {
            assertNotNull(shiftinfo);
//            assertEquals(lastupdated, shiftinfo.getLastupdated());
            String httpLinkToSource = String.format(HTTP_LINK_TO_SOURCE, MY_TURNOS_GOOGLE_SHEET_KEY);
            assertEquals(httpLinkToSource, shiftinfo.getLinkToSource());
            assertEquals("juazuri", shiftinfo.getAuthorName());
            assertEquals("juazuri@gmail.com", shiftinfo.getAuthorEmail());
            assertEquals(shiftinfo.getShift().get(0).getYear(), 2015);
            assertEquals(shiftinfo.getShift().get(0).getWeek(), 1);
            assertEquals(shiftinfo.getShift().get(0).getImasdEu(), "ialonso");
            assertEquals(shiftinfo.getShift().get(0).getImasdMx1(), "rtribaldos");
            assertEquals(shiftinfo.getShift().get(1).getYear(), 2015);
            assertEquals(shiftinfo.getShift().get(1).getWeek(), 2);
            assertEquals(shiftinfo.getShift().get(1).getImasdEu(), "oalbert");
            assertEquals(shiftinfo.getShift().get(1).getImasdMx1(), "jescobar");
        }
    }

    @Test
    public void testReadShiftInfoDummy ()  throws Exception {
        ShiftInfo shiftinfo = null;
        InputStream in = null;
        Date lastupdated = null;
        _given:
        {
            in = getDummyInputStream();
            lastupdated = IsoDate.stringToDate(UPDATED, IsoDate.DATE_TIME);
        }

        _then: {
            shiftinfo = new ShiftInfoHelper().readShiftInfo(in);
        }

        _expect: {
            assertNotNull(shiftinfo);
            assertEquals(lastupdated, shiftinfo.getLastupdated());
            String httpLinkToSource = String.format(HTTP_LINK_TO_SOURCE, MY_TURNOS_GOOGLE_SHEET_KEY);
            assertEquals(httpLinkToSource, shiftinfo.getLinkToSource());
            assertEquals(AUTHOR_NAME, shiftinfo.getAuthorName());
            assertEquals(AUTHOR_EMAIL, shiftinfo.getAuthorEmail());
            assertEquals(shiftinfo.getShift().get(0).getYear(), 2015);
            assertEquals(shiftinfo.getShift().get(0).getWeek(), 1);
            assertEquals(shiftinfo.getShift().get(0).getImasdEu(), "dummy");
            assertEquals(shiftinfo.getShift().get(0).getImasdMx1(), "newbie");
            assertEquals(shiftinfo.getShift().get(1).getYear(), 2015);
            assertEquals(shiftinfo.getShift().get(1).getWeek(), 2);
            assertEquals(shiftinfo.getShift().get(1).getImasdEu(), "fulano");
            assertEquals(shiftinfo.getShift().get(1).getImasdMx1(), "mengano");
        }
    }

    @Ignore
    private InputStream getRawResourceInputStream () throws IOException {
        Activity activity = Robolectric.setupActivity(ShiftActivity.class);
        assertNotNull(activity);
        InputStream in = activity.getResources().openRawResource(R.raw.shift);
        if (null==in) {
            in = Fs.fileFromPath("../app/build/intermediates/res/merged/debug/raw/shift.json").getInputStream();
        }
        assertNotNull(in);
        return in;
    }

    @Ignore
    private InputStream getDummyInputStream () throws Exception {
        return new ByteArrayInputStream(DUMMY_JSON.getBytes());
    }

    @Ignore
    private InputStream getHttpSpreadSheetInputStream () {
        InputStream in = null;
        String urlString =
                "https://spreadsheets.google.com/feeds/list/%s/od6/public/full?alt=json";
        urlString = String.format(urlString, MY_TURNOS_GOOGLE_SHEET_KEY);
        // Making HTTP request
        try {
            // Create a connection from url
            URL url = new URL(urlString);
            NetworkUtils.disableSSLCertificateChecking();
            in = url.openConnection().getInputStream();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }


}