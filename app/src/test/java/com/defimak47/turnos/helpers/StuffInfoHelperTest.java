package com.defimak47.turnos.helpers;

import android.app.Activity;

import com.defimak47.turnos.BuildConfig;
import com.defimak47.turnos.R;
import com.defimak47.turnos.model.StuffInfo;
import com.defimak47.turnos.utils.IsoDate;
import com.defimak47.turnos.view.MainActivity;

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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = { 19, 21, 23 })
public class StuffInfoHelperTest {

    public static final String LINK_TO_SOURCE = "https://docs.google.com/spreadsheets/d/PUBLIC_KEY/pubhtml";
    public static final String UPDATED        = "2015-09-29T08:53:38.068Z";
    public static final String AUTHOR_NAME    = "parent";
    public static final String AUTHOR_EMAIL   = "parent@example.com";
    public static final String HTTP_LINK_TO_SOURCE = "https://docs.google.com/spreadsheets/d/%s/pubhtml";
    private static final String MY_TURNOS_GOOGLE_SHEET_KEY = BuildConfig.TURNOS_GOOGLE_SHEET_KEY;

    private static final String DUMMY_JSON =
            "{\"version\":\"1.0\",\"encoding\":\"UTF-8\",\"feed\":{\"xmlns\":\"http://www.w3.org/2005/Atom\"," +
             "\"xmlns$openSearch\":\"http://a9.com/-/spec/opensearchrss/1.0/\",\"xmlns$gsx\":\"http://schemas.google.com/spreadsheets/2006/extended\"," +
             "\"id\":{\"$t\":\"https://spreadsheets.google.com/feeds/list/PUBLIC_KEY/o39u79f/public/full\"}," +
             "\"updated\":{\"$t\":\""+UPDATED+"\"},\"category\":[{\"scheme\":\"http://schemas.google.com/spreadsheets/2006\"," +
             "\"term\":\"http://schemas.google.com/spreadsheets/2006#list\"}],\"title\":{\"type\":\"text\",\"$t\":\"stuff\"},\"link\":[{\"rel\":\"alternate\"," +
             "\"type\":\"application/atom+xml\",\"href\":\""+ LINK_TO_SOURCE +"\"},{\"rel\":\"http://schemas.google.com/g/2005#feed\",\"type\":\"application/atom+xml\","+
             "\"href\":\"https://spreadsheets.google.com/feeds/list/PUBLIC_KEY/o39u79f/public/full\"}]," +
             "\"author\":[{\"name\":{\"$t\":\"parent\"},\"email\":{\"$t\":\"parent@example.com\"}}],\"openSearch$totalResults\":{\"$t\":\"2\"}," +
             "\"openSearch$startIndex\":{\"$t\":\"1\"},\"entry\":[{\"id\":{\"$t\":\"https://spreadsheets.google.com/feeds/list/PUBLIC_KEY/" +
             "39u79f/public/full/cokwr\"},\"updated\":{\"$t\":\"2015-09-29T08:53:38.068Z\"},\"category\":[{\"scheme\":\"http://schemas.google.com/spreadsheets/2006\"," +
             "\"term\":\"http://schemas.google.com/spreadsheets/2006#list\"}],\"title\":{\"type\":\"text\",\"$t\":\"dummy\"},\"content\":{\"type\":\"text\"," +
             "\"$t\":\"name: Dummy Dummie, alias: dummi, position: Dummy position\"},\"link\":[{\"rel\":\"self\",\"type\":\"application/atom+xml\"," +
             "\"href\":\"https://spreadsheets.google.com/feeds/list/PUBLIC_KEY/o39u79f/public/full/cokwr\"}]," +
             "\"gsx$login\":{\"$t\":\"dummy\"},\"gsx$name\":{\"$t\":\"Dummy Dummie\"},\"gsx$alias\":{\"$t\":\"dummi\"},\"gsx$position\":{\"$t\":\"Dummy position\"}}," +
             "{\"id\":{\"$t\":\"https://spreadsheets.google.com/feeds/list/PUBLIC_KEY/o39u79f/public/full/cpzh4\"}," +
             "\"updated\":{\"$t\":\"2015-09-29T08:53:38.068Z\"},\"category\":[{\"scheme\":\"http://schemas.google.com/spreadsheets/2006\",\"term\":\"http://schemas.google.com/" +
             "spreadsheets/2006#list\"}],\"title\":{\"type\":\"text\",\"$t\":\"redummy\"},\"content\":{\"type\":\"text\",\"$t\":\"name: Name Dummie, alias: red, position: Red position\"}," +
             "\"link\":[{\"rel\":\"self\",\"type\":\"application/atom+xml\",\"href\":\"https://spreadsheets.google.com/feeds/list/PUBLIC_KEY/" +
             "o39u79f/public/full/cpzh4\"}],\"gsx$login\":{\"$t\":\"redummy\"},\"gsx$name\":{\"$t\":\"Name Dummie\"},\"gsx$alias\":{\"$t\":\"red\"}," +
             "\"gsx$position\":{\"$t\":\"Red position\"}}]}}";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReadStuffInfoArrayRawResources() throws Exception {
        StuffInfo stuffinfo = null;
        InputStream in = null;
        Date lastupdated = null;
        _given: {
            in = getRawResourceInputStream();
            lastupdated = IsoDate.stringToDate(UPDATED, IsoDate.DATE_TIME);
        }

        _then: {
            stuffinfo = new StuffInfoHelper().readStuffInfo(in);
        }

        _expect: {
            assertNotNull(stuffinfo);
            assertEquals(lastupdated, stuffinfo.getLastupdated());
            String httpLinkToSource = String.format(HTTP_LINK_TO_SOURCE, MY_TURNOS_GOOGLE_SHEET_KEY);
            assertEquals(httpLinkToSource, stuffinfo.getLinkToSource());
            assertEquals("juazuri", stuffinfo.getAuthorName());
            assertEquals("juazuri@gmail.com", stuffinfo.getAuthorEmail());
            assertEquals(stuffinfo.getStuff().get(0).getLogin(), "sbellosta");
            assertEquals(stuffinfo.getStuff().get(0).getAlias(), "santi");
            assertEquals(stuffinfo.getStuff().get(1).getLogin(), "jvalero");
            assertEquals(stuffinfo.getStuff().get(1).getAlias(), "juanma");
        }
    }

    @Test
    public void testReadStuffInfoHttpSpreadSheetResource() throws Exception {
        StuffInfo stuffinfo = null;
        InputStream in = null;
        Date lastupdated = null;
        _given: {
            in = getHttpSpreadSheetInputStream();
            lastupdated = IsoDate.stringToDate(UPDATED, IsoDate.DATE_TIME);
        }

        _then: {
            stuffinfo = new StuffInfoHelper().readStuffInfo(in);
        }

        _expect: {
            assertNotNull(stuffinfo);
//            assertEquals(lastupdated, stuffinfo.getLastupdated());
            String httpLinkToSource = String.format(HTTP_LINK_TO_SOURCE, MY_TURNOS_GOOGLE_SHEET_KEY);
            assertEquals(httpLinkToSource, stuffinfo.getLinkToSource());
            assertEquals("juazuri", stuffinfo.getAuthorName());
            assertEquals("juazuri@gmail.com", stuffinfo.getAuthorEmail());
            assertEquals(stuffinfo.getStuff().get(0).getLogin(), "sbellosta");
            assertEquals(stuffinfo.getStuff().get(0).getAlias(), "santi");
            assertEquals(stuffinfo.getStuff().get(1).getLogin(), "jvalero");
            assertEquals(stuffinfo.getStuff().get(1).getAlias(), "juanma");
        }
    }

    @Test
    public void testReadStuffInfoDummy ()  throws Exception {
        StuffInfo stuffinfo = null;
        InputStream in = null;
        Date lastupdated = null;
        _given:
        {
            in = getDummyInputStream();
            lastupdated = IsoDate.stringToDate(UPDATED, IsoDate.DATE_TIME);
        }

        _then: {
            stuffinfo = new StuffInfoHelper().readStuffInfo(in);
        }

        _expect: {
            assertNotNull(stuffinfo);
            assertEquals(lastupdated, stuffinfo.getLastupdated());
            assertEquals(LINK_TO_SOURCE, stuffinfo.getLinkToSource());
            assertEquals(AUTHOR_NAME, stuffinfo.getAuthorName());
            assertEquals(AUTHOR_EMAIL, stuffinfo.getAuthorEmail());
            assertEquals(stuffinfo.getStuff().get(0).getLogin(), "dummy");
            assertEquals(stuffinfo.getStuff().get(0).getAlias(), "dummi");
            assertEquals(stuffinfo.getStuff().get(1).getLogin(), "redummy");
            assertEquals(stuffinfo.getStuff().get(1).getAlias(), "red");
        }
    }

    @Ignore
    private InputStream getRawResourceInputStream () throws IOException {
        Activity activity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(activity);
        InputStream in = activity.getResources().openRawResource(R.raw.stuff);
        if (null==in) {
            in = Fs.fileFromPath("../app/build/intermediates/res/merged/debug/raw/stuff.json").getInputStream();
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
                "https://spreadsheets.google.com/feeds/list/%s/o39u79f/public/full?alt=json";
        urlString = String.format(urlString, MY_TURNOS_GOOGLE_SHEET_KEY);
        // Making HTTP request
        try {
            // Create a connection from url
            URL url = new URL(urlString);
            disableSSLCertificateChecking();
            in = url.openConnection().getInputStream();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    /**
     * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection} This has been created to
     * aid testing on a local box, not for use on production.
     */
    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}