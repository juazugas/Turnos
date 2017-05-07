package com.defimak47.turnos.helpers;

import android.app.Activity;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.R;
import com.defimak47.turnos.model.PhotoInfo;
import com.defimak47.turnos.utils.IsoDate;
import com.defimak47.turnos.view.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Information about the API to access Worksheets
 * https://developers.google.com/sheets/api/v3/worksheets#retrieve_information_about_worksheets
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = { 19, 21, 23 })
public class PhotoInfoHelperTest {

    public static final String LINK_TO_SOURCE = "https://docs.google.com/spreadsheets/d/PUBLIC_KEY/pubhtml";
    public static final String UPDATED        = "2017-05-02T21:35:38.068Z";
    public static final String AUTHOR_NAME    = "memo";
    public static final String AUTHOR_EMAIL   = "memo@example.com";
    public static final String HTTP_LINK_TO_SOURCE = "https://docs.google.com/spreadsheets/d/%s/pubhtml";
    private static final String MY_TURNOS_GOOGLE_SHEET_KEY = BuildConfig.TURNOS_GOOGLE_SHEET_KEY;

    /* Access to https://spreadsheets.google.com/feeds/list/worsheetkey/oqea05o/public/full
     *
     */

    private static final String DUMMY_JSON = "{\"version\":\"1.0\",\"encoding\":\"UTF-8\",\"feed\":{\"xmlns\":\"http://www.w3.org/2005/Atom\"," +
            "\"xmlns$openSearch\":\"http://a9.com/-/spec/opensearchrss/1.0/\",\"xmlns$gsx\":\"http://schemas.google.com/spreadsheets/2006/extended\"," +
            "\"id\":{\"$t\":\"+LINK_TO_SOURCE+\"}," +
            "\"updated\":{\"$t\":\""+ UPDATED +"\"},\"category\":[{\"scheme\":\"http://schemas.google.com/spreadsheets/2006\"," +
            "\"term\":\"http://schemas.google.com/spreadsheets/2006#list\"}],\"title\":{\"type\":\"text\",\"$t\":\"photos\"}," +
            "\"link\":[{\"rel\":\"alternate\",\"type\":\"application/atom+xml\",\"href\":\""+LINK_TO_SOURCE+"\"}," +
            "{\"rel\":\"http://schemas.google.com/g/2005#feed\",\"type\":\"application/atom+xml\"," +
            "\"href\":\""+ LINK_TO_SOURCE +"\"}," +
            "{\"rel\":\"http://schemas.google.com/g/2005#post\",\"type\":\"application/atom+xml\",\"href\":\"https://spreadsheets.google.com" +
            "/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full\"},{\"rel\":\"self\",\"type\":\"application/atom+xml\"," +
            "\"href\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full?alt\\u003djson\"}]," +
            "\"author\":[{\"name\":{\"$t\":\""+ AUTHOR_NAME +"\"},\"email\":{\"$t\":\""+ AUTHOR_EMAIL +"\"}}],\"openSearch$totalResults\":{\"$t\":\"4\"}," +
            "\"openSearch$startIndex\":{\"$t\":\"1\"},\"entry\":[{\"id\":{\"$t\":\"https://spreadsheets.google.com/feeds/list/" +
            ""+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full/cokwr\"},\"updated\":{\"$t\":\"2017-05-03T20:40:06.902Z\"}," +
            "\"category\":[{\"scheme\":\"http://schemas.google.com/spreadsheets/2006\",\"term\":\"http://schemas.google.com/spreadsheets/2006#list\"}]," +
            "\"title\":{\"type\":\"text\",\"$t\":\"dummy\"},\"content\":{\"type\":\"text\",\"$t\":\"url: dummy://dummy.url\"},\"link\":[{\"rel\":\"self\",\"type\":\"application/atom+xml\"" +
            ",\"href\":\"https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full/cokwr\"}]," +
            "\"gsx$login\":{\"$t\":\"dummy\"},\"gsx$url\":{\"$t\":\"dummy://dummy.url\"}},{\"id\":{\"$t\":\"https://spreadsheets.google.com/feeds/list/" + MY_TURNOS_GOOGLE_SHEET_KEY +
            "/oqea05o/public/full/cpzh4\"},\"updated\":{\"$t\":\"2017-05-03T20:40:06.902Z\"},\"category\":[{\"scheme\":\"http://sche" +
            "mas.google.com/spreadsheets/2006\",\"term\":\"http://schemas.google.com/spreadsheets/2006#list\"}],\"title\":{\"type\":\"text\"," +
            "\"$t\":\"redummy\"},\"content\":{\"type\":\"text\",\"$t\":\"url: dummy://red.dummy.url\"},\"link\":[{\"rel\":\"self\",\"type\":\"application/atom+xml\",\"href\":\"https://spreadsheets." +
            "google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full/cpzh4\"}],\"gsx$login\":{\"$t\":\"redummy\"}," +
            "\"gsx$url\":{\"$t\":\"dummy://red.dummy.url\"}}]}}";

    /*
    Sample content

    <?xml version='1.0' encoding='UTF-8'?><feed xmlns='http://www.w3.org/2005/Atom' xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/' xmlns:gsx='http://schemas.google.com/spreadsheets/2006/extended'><id>https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full</id><updated>2017-05-03T20:00:44.980Z</updated><category scheme='http://schemas.google.com/spreadsheets/2006' term='http://schemas.google.com/spreadsheets/2006#list'/><title type='text'>photos</title><link rel='alternate' type='application/atom+xml' href='https://docs.google.com/spreadsheets/d/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/pubhtml'/><link rel='http://schemas.google.com/g/2005#feed' type='application/atom+xml' href='https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full'/><link rel='http://schemas.google.com/g/2005#post' type='application/atom+xml' href='https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full'/><link rel='self' type='application/atom+xml' href='https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full'/><author><name>juazuri</name><email>juazuri@gmail.com</email></author><openSearch:totalResults>1</openSearch:totalResults><openSearch:startIndex>1</openSearch:startIndex><entry><id>https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full/cokwr</id><updated>2017-05-03T20:00:44.980Z</updated><category scheme='http://schemas.google.com/spreadsheets/2006' term='http://schemas.google.com/spreadsheets/2006#list'/><title type='text'>pizquierdo</title><content type='text'>url: https://lh4.googleusercontent.com/0WVgCeGtg6ztTFrjtkekke5EYi5EG047cSmtK2hfs6Xw_AHV2qVsZ8iYrhN8jIoeqhOgIdN6fvIIxss</content><link rel='self' type='application/atom+xml' href='https://spreadsheets.google.com/feeds/list/"+MY_TURNOS_GOOGLE_SHEET_KEY+"/oqea05o/public/full/cokwr'/><gsx:login>pizquierdo</gsx:login><gsx:url>https://lh4.googleusercontent.com/0WVgCeGtg6ztTFrjtkekke5EYi5EG047cSmtK2hfs6Xw_AHV2qVsZ8iYrhN8jIoeqhOgIdN6fvIIxss</gsx:url></entry></feed>
     */


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReadStuffInfoDummy ()  throws Exception {
        PhotoInfo photoInfo = null;
        InputStream in = null;
        Date lastupdated = null;
        _given:
        {
            in = getDummyInputStream();
            lastupdated = IsoDate.stringToDate(UPDATED, IsoDate.DATE_TIME);
        }

        _when: {
            photoInfo = new PhotoInfoHelper().readPhotoInfo(in);
        }

        _then: {
            assertNotNull(photoInfo);
            assertEquals(lastupdated, photoInfo.getLastupdated());
            assertEquals(LINK_TO_SOURCE, photoInfo.getLinkToSource());
            assertEquals(AUTHOR_NAME, photoInfo.getAuthorName());
            assertEquals(AUTHOR_EMAIL, photoInfo.getAuthorEmail());
            assertEquals(photoInfo.getPhoto().get(0).getLogin(), "dummy");
            assertEquals(photoInfo.getPhoto().get(0).getUrl(), "dummy://dummy.url");
            assertEquals(photoInfo.getPhoto().get(1).getLogin(), "redummy");
            assertEquals(photoInfo.getPhoto().get(1).getUrl(), "dummy://red.dummy.url");
        }
    }

    private InputStream getRawResourceInputStream () throws IOException {
        Activity activity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(activity);
        InputStream in = activity.getResources().openRawResource(R.raw.stuff);
        if (null==in) {
            in = Fs.fileFromPath("../app/build/intermediates/res/merged/debug/raw/photo.json").getInputStream();
        }
        assertNotNull(in);
        return in;
    }

    private InputStream getDummyInputStream () throws Exception {
        return new ByteArrayInputStream(DUMMY_JSON.getBytes());
    }
}
