package com.defimak47.turnos.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.defimak47.turnos.R;
import com.defimak47.turnos.utils.IOUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jzuriaga on 7/5/17.
 */

public class ImageResourceHelper {

    public final static String IMAGE_NAME_TEMPLATE = "img_%s.jpg";

    private Context context;

    public ImageResourceHelper(Context context) {
        this.context = context;
    }

    public Bitmap getImageResource(String login) {
        String filename = getImageFileName(login);
        try {
            if (internalFileExists(filename)) {
                return BitmapFactory.decodeStream(context.openFileInput(filename));
            }
            return BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.portrait));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadImageResource(final String login, String url, final ImageView viewImage) {
        final String filename = getImageFileName(login);
        if(TextUtils.isEmpty(url)) {
            viewImage.setImageBitmap(getImageResource(login));
            return;
        }
        if (!internalFileExists(filename)) {
            Ion.with(getContext())
                    .load(url)
                    .setLogging("ImageResourceHelper", Log.INFO)
                    .asInputStream()
                    .setCallback(new FutureCallback<InputStream>() {
                        @Override
                        public void onCompleted(Exception e, InputStream result) {
                            if (null == e) {
                                writeImageRawResource(filename, result);
                                viewImage.setImageBitmap(getImageResource(login));
                            }
                        }
                    });
        } else {
            viewImage.setImageBitmap(getImageResource(login));
        }
    }

    private String getImageFileName(String login) {
        return String.format(IMAGE_NAME_TEMPLATE, login);
    }

    private InputStream updateImageRawResource(String filename, InputStream result) {
        InputStream in = null;
        try {
            writeImageRawResource(filename, result);
            in = context.openFileInput(filename);
        } catch (IOException e) {
            Log.e("ImageResourceHelper", "updateImageResource" + e.getMessage(), e);
        }
        return in;
    }

    private void writeImageRawResource(String filename, InputStream origin) {
        try {
            OutputStream targout = context.openFileOutput(filename, Context.MODE_PRIVATE);
            IOUtils.copy(origin, targout);
            targout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean internalFileExists(String filename) {
        File file = context.getFileStreamPath(filename);
        return file.exists();
    }

    private void createJpegFileFromBitmap(String filename, Bitmap bitmap) {
        try {
            OutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return context;
    }
}
