package com.defimak47.turnos.view;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.defimak47.turnos.R;
import com.defimak47.turnos.adapter.ShiftAdapter;
import com.defimak47.turnos.helpers.ShiftInfoHelper;
import com.defimak47.turnos.model.Shift;
import com.defimak47.turnos.model.ShiftInfo;
import com.defimak47.turnos.model.ShiftRecord;
import com.defimak47.turnos.sync.TurnosSyncAdapter;
import com.defimak47.turnos.utils.IOUtils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ShiftActivity extends AppCompatActivity
                        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String EXTRA_SEARCH = "__com_defimak47_turnos_extra_searh__";
    private static final String SHIFT_FILE_NAME = "shift.json";

    private ShiftInfo shiftinfo;
    private List<Shift> shifts;

    private RecyclerView recList;

    private LinearLayoutManager linearLayoutManager;

    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     *
     * <p>This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    private Object mSyncObserverHandle;

    /**
     * Options menu used to populate ActionBar.
     */
    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);

        TurnosSyncAdapter.initializeSyncAdapter(this);

        loadShifts();
        recList = (RecyclerView) findViewById(R.id.shiftList);
        recList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(linearLayoutManager);

        recList.setAdapter(new ShiftAdapter(this, shifts));
        final int bottom = recList.getPaddingBottom();
        final int left = recList.getPaddingLeft();
        final int right = recList.getPaddingRight();
        final int top = recList.getPaddingTop();
        recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int currentFirstVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                //linearLayoutManager.findFirstVisibleItemPosition();

                int paddingTop = top ;

                if (this.mLastFirstVisibleItem > 0 && currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                    ShiftActivity.this.getSupportActionBar().hide();
                    paddingTop = bottom;
                    recyclerView.setPadding(left, paddingTop, right, bottom);
                } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                    ShiftActivity.this.getSupportActionBar().show();
                    paddingTop = top;
                    recyclerView.setPadding(left, paddingTop, right, bottom);
                }

                this.mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });

        String extraSearch = getIntentExtraSearch();
        if (!TextUtils.isEmpty(extraSearch)) {
            filterShiftAdapter(extraSearch);
        } else {
            scrollToShift();
        }
    }

    private String getIntentExtraSearch () {
        String result = null;
        if (hasIntentExtraSearch()) {
            result = getIntent().getExtras().getString(EXTRA_SEARCH);
        }
        return result;
    }

    private boolean hasIntentExtraSearch () {
        return null!=getIntent().getExtras() && getIntent().getExtras().containsKey(EXTRA_SEARCH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shift, menu);
        mOptionsMenu = menu;

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(ShiftActivity.this);
        searchView.setOnCloseListener(ShiftActivity.this);
        searchView.setIconifiedByDefault(true); // iconify the widget; collapse it by default
        if (hasIntentExtraSearch()) {
            searchView.setQuery(getIntentExtraSearch(), true);
            searchView.setIconified(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==  R.id.action_team) {
           goTeamActivity();
        } else if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_current) {
            scrollToShift();
        } else if (id == R.id.action_refresh) {
            TurnosSyncAdapter.syncImmediately(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        filterShiftAdapter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            flushFilterAdapter();
        } else if (newText.length() >2) {
            filterShiftAdapter(newText);
        }
        return true;
    }

    @Override
    public boolean onClose() {
        flushFilterAdapter();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    private void flushFilterAdapter () {
        ((ShiftAdapter)recList.getAdapter()).flushFilter();
    }

    private void filterShiftAdapter (String query) {
        ((ShiftAdapter)recList.getAdapter()).setFilter(query);
    }

    /**
     * Load the shift list.
     */
    private void loadShifts() {
        if (null==shifts) {
            shifts = new ArrayList<>();
        } else {
            shifts.clear();
        }
        ShiftInfoHelper helper = new ShiftInfoHelper();
        try {
            InputStream in = getShiftResource();
            shiftinfo = helper.readShiftInfo(in);
            for (ShiftRecord shiftRecord : shiftinfo.getShift()) {
                shifts.add(shiftRecord);
            }
            in.close();
        } catch (IOException|JSONException io) {
            Log.w("ShiftActivity", "loadShifts " + io.getMessage(), io);
        }
    }

    private InputStream getShiftResource() throws IOException {
        if (!internalFileExists(SHIFT_FILE_NAME)) {
            InputStream origin = getResources().openRawResource(R.raw.shift);
            OutputStream targout = openFileOutput(SHIFT_FILE_NAME, Context.MODE_PRIVATE);
            IOUtils.copy(origin, targout);
            origin.close();
            targout.close();
        }
        return openFileInput(SHIFT_FILE_NAME);
    }

    private boolean internalFileExists(String shiftFileName) {
        File file = getFileStreamPath(shiftFileName);
        return file.exists();
    }

    private void scrollToShift() {
        if (!shifts.isEmpty()) {
            int position = -1;
            Calendar now = Calendar.getInstance();
            int weekOfYear = now.get(Calendar.WEEK_OF_YEAR);
            int year = now.get(Calendar.YEAR);
            if (weekOfYear == 1 && now.get(Calendar.WEEK_OF_MONTH)>2) { // Its december last week of month
                year = year + 1;
            }
            for (int i = 0 ; i<shifts.size(); i++) {
                Shift shift = shifts.get(i);
                if (shift.getWeek()==weekOfYear && shift.getYear()==year) {
                    position = i;
                    break;
                }
            }
            if (position>0 && linearLayoutManager.canScrollVertically()) {
                linearLayoutManager.scrollToPositionWithOffset(position, 20);
            }
        }
    }

    private void goTeamActivity ( ) {
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Set the state of the Refresh button. If a sync is active, turn on the ProgressBar widget.
     * Otherwise, turn it off.
     *
     * @param refreshing True if an active sync is occuring, false otherwise
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.action_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    /**
     * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {

        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(final int which) {
            ShiftActivity.this.runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {

                    Context context = ShiftActivity.this;

                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = TurnosSyncAdapter.getSyncAccount(context);
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        setRefreshActionButtonState(false);
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, TurnosSyncAdapter.getSyncAuthority(context));
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, TurnosSyncAdapter.getSyncAuthority(context));
                    setRefreshActionButtonState(syncActive || syncPending);
                    if ( which != 0 && !(syncActive || syncPending)) {
                        loadShifts();
                        scrollToShift();
                    }
                }
            });
        }
    };

}
