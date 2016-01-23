package com.defimak47.turnos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

/**
 * Collection of utils for manipulate graphics in Android.
 *
 */
public final class GraphicUtils {

    /**
     * Hide default constructor.
     */
    private GraphicUtils () {
        /* no-op constructor. */
    }

    /**
     * Converts a bitmap in a rounded drawable.
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static RoundedBitmapDrawable getRoundedImageDrawable (Context context, Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        return roundedBitmapDrawable;
    }

    public static void getRoundedImageView (ImageView viewImage, Context context, Bitmap bitmap) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Paint paintBorder = new Paint();
            paintBorder.setColor(Color.WHITE);
            paintBorder.setAntiAlias(true);
            paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
            viewImage.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, paintBorder);
        }
        viewImage.setImageDrawable(getRoundedImageDrawable(context, bitmap));
    }

}
