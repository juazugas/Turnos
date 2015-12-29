package com.defimak47.turnos.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.ConnectException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by jzuriaga on 29/12/15.
 */
public final class NetworkUtils {

    private final static String LOG_TAG = NetworkUtils.class.getSimpleName();

    /**
     * Hide constructor.
     */
    private NetworkUtils ( ) {
        /* no-op constructor. */
    }

    /**
     * Helper method to check the network connection.
     */
    public static boolean isNetworkConnected (Context context ) {

        boolean status = false;
        try {
            ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            } else {
                status = false;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "isNetworkConnected -- "+e.getMessage(), e);
            return false;
        }
        return status;
    }

    /**
     * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection} This has been created to
     * aid testing on a local box, not for use on production.
     */
    public static void disableSSLCertificateChecking() throws ConnectException {

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
            Log.e(LOG_TAG, "disableSSLCertificateChecking - KeyManagement " + e.getMessage(), e);
//            e.printStackTrace();
            throw new ConnectException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "disableSSLCertificateChecking - NoSuchAlgorithm " + e.getMessage(), e);
//            e.printStackTrace();
            throw new ConnectException(e.getMessage());
        }
    }


}
