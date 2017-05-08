package com.defimak47.turnos.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.defimak47.turnos.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by jzuriaga on 7/5/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = { 23 })
public class ImageResourceHelperTest {

    private ImageResourceHelper helper;

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application.getBaseContext();
        helper = new ImageResourceHelper(context);
    }

    @Test
    public void getImageResourceNotExists () {
        Bitmap result = null;
        _given: {

        }

        _when: {
            result = helper.getImageResource("dummy");
        }

        _then: {
            assertNull(result);
        }
    }

    @Test
    public void loadImageResource () throws Exception {
        ImageView imageView = new ImageView(context);
        String login = "dummy";
        String url = "https://lh6.googleusercontent.com/88L5arJ6Z17IBNiA2izaSDq2QUaeYgspCit2Hwd-F17CKZQmZkPkcDkBIopYFCcwvPoMEaSTKEnBkCg";
        _given: {

        }

        _when: {
            helper.loadImageResource(login, url, imageView);
        }

        _then: {
            Thread.sleep(500);
            assertTrue(context.getFileStreamPath("img_dummy.jpg").exists());
        }

    }

}
