package com.defimak47.turnos.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by jzuriaga on 29/12/15.
 */
public class TurnosAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private TurnosAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new TurnosAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
