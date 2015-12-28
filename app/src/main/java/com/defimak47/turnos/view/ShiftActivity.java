package com.defimak47.turnos.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import com.defimak47.turnos.helpers.ShiftHelper;
import com.defimak47.turnos.model.Shift;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ShiftActivity extends AppCompatActivity
                        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String EXTRA_SEARCH = "__com_defimak47_turnos_extra_searh__";

    private List<Shift> shifts;

    private RecyclerView recList;

    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);

        initShifts();
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

    private void flushFilterAdapter () {
        ((ShiftAdapter)recList.getAdapter()).flushFilter();
    }

    private void filterShiftAdapter (String query) {
        ((ShiftAdapter)recList.getAdapter()).setFilter(query);
    }

    /**
     * Init the shift list.
     */
    private void initShifts() {
        ShiftHelper helper = new ShiftHelper();
        try {
            InputStream in = getResources().openRawResource(R.raw.turnos);
            shifts = helper.readShiftArray(in);
            in.close();
        } catch (IOException io) {
            Log.w("ShiftActivity", "initShifts " + io.getMessage(), io);
            if (null==shifts) {
                shifts = new ArrayList<Shift>();
            }
        }
    }

    private void scrollToShift() {
        if (!shifts.isEmpty()) {
            int position = -1;
            Calendar now = Calendar.getInstance();
            for (int i = 0 ; i<shifts.size(); i++) {
                Shift shift = shifts.get(i);
                if (shift.getWeek()==now.get(Calendar.WEEK_OF_YEAR) && shift.getYear()==now.get(Calendar.YEAR)) {
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
}
