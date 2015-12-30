package com.defimak47.turnos.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.defimak47.turnos.R;
import com.defimak47.turnos.sync.TurnosSyncAdapter;

/**
 * Created by jzuriaga on 30/12/15.
 */
public final class PreferenceFacade {

    /**
     * Hide constructor.
     */
    private PreferenceFacade () {
        /* no-op constructor. */
    }

    /**
     * Obtain the last synchro date saved in preferences.
     * @param context
     * @return
     */
    public static String getLastSync(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_last_synchro), null);
    }

    /**
     * Set the last synchro moment.
     * @param context
     * @param lastSync
     */
    public static void setLastSync(Context context, String lastSync) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(context.getString(R.string.pref_last_synchro), lastSync).commit();
    }

    /**
     * Obtain the preferred sheet
     * @param context
     * @return
     */
    public static String getSheet(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sheet_key),
                context.getString(R.string.pref_sheet_default));
    }

    /**
     * Sets the location status.
     * @param context
     * @param status
     */
    public static void setLocationStatus(Context context, @TurnosSyncAdapter.LocationStatus int status) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt(context.getString(R.string.pref_location_status_key,status ), status).commit();
    }

    /**
     * Obtain the status saved in preferences.
     * @param context
     * @return
     */
    @SuppressWarnings("ResourceType")
    @TurnosSyncAdapter.LocationStatus
    public static int getLocationStatus (Context context) {
        SharedPreferences prefs  = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(context.getString(R.string.pref_location_status_key),
                TurnosSyncAdapter.LOCATION_STATUS_UNKNOWN);
    }

    /**
     * Sets the status preference to unknown.
     * @param context
     */
    public static void resetLocationStatus (Context context) {
        SharedPreferences prefs  = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt(context.getString(R.string.pref_location_status_key),
                TurnosSyncAdapter.LOCATION_STATUS_UNKNOWN)
                .apply();
    }


}
