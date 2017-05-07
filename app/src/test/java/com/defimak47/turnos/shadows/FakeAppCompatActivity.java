package com.defimak47.turnos.shadows;

import android.support.v7.app.AppCompatActivity;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowActivity;

/**
 * Created by jzuriaga on 7/5/17.
 */
@Implements(AppCompatActivity.class)
public class FakeAppCompatActivity extends ShadowActivity {

    public FakeAppCompatActivity() {
        super();
    }
}
