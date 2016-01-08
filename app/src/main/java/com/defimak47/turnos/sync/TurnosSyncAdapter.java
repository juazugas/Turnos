package com.defimak47.turnos.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.util.Log;

import com.defimak47.turnos.R;
import com.defimak47.turnos.helpers.ShiftInfoHelper;
import com.defimak47.turnos.helpers.StuffInfoHelper;
import com.defimak47.turnos.prefs.PreferenceFacade;
import com.defimak47.turnos.utils.IOUtils;
import com.defimak47.turnos.utils.IsoDate;
import com.defimak47.turnos.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.ConnectException;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jzuriaga on 29/12/15.
 */
public class TurnosSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = TurnosSyncAdapter.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ LOCATION_STATUS_OK, LOCATION_STATUS_SERVER_DOWN, LOCATION_STATUS_SERVER_INVALID, LOCATION_STATUS_UNKNOWN, LOCATION_STATUS_INVALID})
    public @interface LocationStatus {}

    public static final int LOCATION_STATUS_OK = 0;
    public static final int LOCATION_STATUS_SERVER_DOWN = 1;
    public static final int LOCATION_STATUS_SERVER_INVALID = 2;
    public static final int LOCATION_STATUS_UNKNOWN = 3;
    public static final int LOCATION_STATUS_INVALID = 4;

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/12;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int TURNOS_NOTIFICATION_ID = 3004;

    public TurnosSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public TurnosSyncAdapter( Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        // String locationQuery = Utility.getPreferredLocation(getContext());

        PreferenceFacade.setLastSync(getContext(), IsoDate.dateToString(new Date(), IsoDate.DATE_TIME));

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        try {
            NetworkUtils.disableSSLCertificateChecking();

            syncTargetJson(ShiftInfoHelper.HTTP_STATIC_SHIFT_URL, ShiftInfoHelper.SHIFT_FILE_NAME);

            InputStream origin = getContext().openFileInput(ShiftInfoHelper.SHIFT_FILE_NAME);
            int results = new ShiftInfoHelper().readShiftInfo(origin).getTotalResults();
            Log.i(LOG_TAG, "Sync Shift Info Complete. " + results + " Inserted");

            syncTargetJson(StuffInfoHelper.HTTP_STATIC_STUFF_URL, StuffInfoHelper.CONTACT_FILE_NAME);

            origin = getContext().openFileInput(StuffInfoHelper.CONTACT_FILE_NAME);
            results = new StuffInfoHelper().readStuffInfo(origin).getTotalResults();
            Log.i(LOG_TAG, "Sync Contact Info Complete. " + results + " Inserted");

            PreferenceFacade.setLocationStatus(getContext(), LOCATION_STATUS_OK);
        } catch (ConnectException ce) {
            Log.e(LOG_TAG, "Error connecting");
            PreferenceFacade.setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            PreferenceFacade.setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            PreferenceFacade.setLocationStatus(getContext(), LOCATION_STATUS_SERVER_INVALID);
        }
    }

    private void syncTargetJson(final String targetUrl, final String targetFileName) throws IOException {
        HttpsURLConnection urlConnection = null;
        OutputStream targetOut = null;
        InputStream inputStream = null;

        try {
            targetOut = getContext().openFileOutput(targetFileName, Context.MODE_PRIVATE);

            Uri builtUri = Uri.parse(targetUrl).buildUpon().build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            inputStream = urlConnection.getInputStream();
            IOUtils.copy(inputStream, targetOut);

        } finally {
            if (null!=inputStream) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            if (targetOut != null) {
                try {
                    targetOut.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        TurnosSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


}
