package com.defimak47.turnos.shadows;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncRequest;
import android.content.SyncStatusObserver;
import android.os.RemoteException;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowContentResolver;

/**
 * Created by jzuriaga on 7/5/17.
 */
@Implements(value = ContentResolver.class)
public class FakeContentResolver extends ShadowContentResolver {

    public FakeContentResolver() {
        super();
    }

    @Implementation
    public static void requestSync(SyncRequest request) {
//        validateSyncExtrasBundle(request.getExtras());
//        Status status = getStatus(account, authority, true);
//        status.syncRequests++;
//        status.syncExtras = extras;
        return;
    }

    @Implementation
    public static boolean isSyncPending(Account account, String authority) {
        ShadowContentResolver.Status status = getStatus(account, authority);
        return status != null && status.syncRequests > 0 && status.state > 1;
    }

    @Implementation
    public static boolean isSyncPendingAsUser(Account account, String authority, int userId) {
        return false;
    }

    @Implementation
    public static Object addStatusChangeListener(int mask, final SyncStatusObserver callback) {
        if (callback == null) {
            throw new IllegalArgumentException("you passed in a null callback");
        }
        ISyncStatusObserverShadow observer = new ISyncStatusObserverShadow() {
            public void onStatusChanged(int which) throws RemoteException {
                callback.onStatusChanged(which);
            }
        };
        return observer;
    }

    public interface ISyncStatusObserverShadow {

        public void onStatusChanged(int which) throws RemoteException;

    }

}
