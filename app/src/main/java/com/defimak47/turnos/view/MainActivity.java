package com.defimak47.turnos.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.defimak47.turnos.R;
import com.defimak47.turnos.adapter.ContactAdapter;
import com.defimak47.turnos.helpers.StuffInfoHelper;
import com.defimak47.turnos.model.ContactInfo;
import com.defimak47.turnos.model.StuffInfo;
import com.defimak47.turnos.model.StuffRecord;
import com.defimak47.turnos.utils.IOUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
                       implements View.OnLongClickListener {


    private static final String STATIC_STUFF_URL = "https://spreadsheets.google.com/feeds/list/1D_7igYenGDjG-_nhh1miPuKRvVeR5-BpQxQR9YE6O-A/o39u79f/public/full?alt=json";
    private static final String CONTACT_FILE_NAME = "stuff.json";
    private RecyclerView recList;
    private List<ContactInfo> contacts;
    private StuffInfo stuffinfo;
    private ContactAdapter adapter;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setHideOnContentScrollEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);

        initIon();
        initContacts();

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setOnLongClickListener(this);

        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        adapter = new ContactAdapter(this, contacts);
        recList.setAdapter(adapter);
        final int bottom = recList.getPaddingBottom();
        final int left = recList.getPaddingLeft();
        final int right = recList.getPaddingRight();
        final int top = recList.getPaddingTop();
        recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;
            int accumulatedOffset = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int currentFirstVisibleItem = llm.findLastCompletelyVisibleItemPosition();

                int paddingTop = top ;
                if (this.mLastFirstVisibleItem > 0 && currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                    MainActivity.this.getSupportActionBar().hide();
                    paddingTop = bottom;
                    recyclerView.setPadding(left, paddingTop, right, bottom);
                } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                    MainActivity.this.getSupportActionBar().show();
                    paddingTop = top;
                    recyclerView.setPadding(left, paddingTop, right, bottom);
                }
                this.mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });
    }

    private void initIon() {
        Ion.getDefault(this).configure().setLogging("MainActivity.Ion", Log.INFO);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Ion.getDefault(this).cancelAll(this);
    }

    @Override
    public boolean onLongClick(View view) {
        View techView = view.findViewById(R.id.title);
        if (null!=techView) {
            String login = (String) techView.getTag();
            if (!TextUtils.isEmpty(login)) {
                Intent intent = new Intent(this, ShiftActivity.class);
                intent.putExtra(ShiftActivity.EXTRA_SEARCH, login);
                goShiftActivity(intent);
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings :
                return true;
            case R.id.action_shifts :
                goShiftActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initContacts () {
        contacts = new ArrayList<>();
        // initOnlineStuffResource();
        InputStream in = null; //
        try {
            in = getStuffRawResourceInputStream();
            processStuffResource(in);
        } catch (IOException e) {
            Log.e("MainActivity", "initContacts" + e.getMessage(), e);
        }
    }

    private void processStuffResource(InputStream in) {
        if (null==contacts) {
            contacts = new ArrayList<>();
        } else {
            contacts.clear();
        }
        try {
            stuffinfo = new StuffInfoHelper().readStuffInfo(in);
            for (StuffRecord stuffrecord : stuffinfo.getStuff()) {
                contacts.add(stuffrecord);
            }
            in.close();
        } catch (JSONException|IOException io ) {
            Log.e("MainActivity", "initContacts " + io.getMessage(), io);
        } finally {
        }
    }

    private InputStream getLusersRawResourceInputStream () {
        return getResources().openRawResource(R.raw.lusers);
    }

    private InputStream getStuffRawResourceInputStream () throws IOException {
        if (!internalFileExists(CONTACT_FILE_NAME)) {
            InputStream origin = getResources().openRawResource(R.raw.stuff);
            writeStuffRawResource(origin);
            origin.close();
        }
        return openFileInput(CONTACT_FILE_NAME);
    }

    private void writeStuffRawResource(InputStream origin) throws IOException {
        OutputStream targout = openFileOutput(CONTACT_FILE_NAME, Context.MODE_PRIVATE);
        IOUtils.copy(origin, targout);
        targout.close();
    }

    private InputStream updateStuffRawResource(InputStream result) {
        InputStream in = null;
        try {
            writeStuffRawResource(result);
            in = openFileInput(CONTACT_FILE_NAME);
        } catch (IOException e) {
            Log.e("MainActivity", "updateStuff" + e.getMessage(), e);
        }
        return in;
    }

    private boolean internalFileExists(String shiftFileName) {
        File file = getFileStreamPath(shiftFileName);
        return file.exists();
    }

    private void goShiftActivity ( ) {
        Intent intent = new Intent(this, ShiftActivity.class);
        if (NavUtils.shouldUpRecreateTask(this, intent)) {
            goShiftActivity(intent);
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    public void goShiftActivity (Intent intent) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }
        startActivity(intent);
    }

    private void initOnlineStuffResource() {
        if (null!=mProgress) {
            mProgress.setVisibility(View.VISIBLE);
        }
        setProgressBarIndeterminateVisibility(Boolean.TRUE);
        Ion.with(this)
                .load(getStuffUrl())
                .asInputStream()
                .setCallback(new OnlineStuffResourceFutureCallback(this));
    }

    public String getStuffUrl() {
        return STATIC_STUFF_URL;
    }

    private class OnlineStuffResourceFutureCallback implements FutureCallback<InputStream> {

        private Context mContext;

        public OnlineStuffResourceFutureCallback(Context context) {
            this.mContext = context;
        }

        @Override
        public void onCompleted(Exception e, InputStream result) {
            if (e != null) {
                Log.e("OnlineStuffCallback", "Error loading stuff", e);
                Toast.makeText(mContext, "Error loading stuff", Toast.LENGTH_LONG).show();
                return;
            }
            if (null!=result) {
                InputStream in = updateStuffRawResource(result);
                if (null!=in) {
                    processStuffResource(in);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("OnlineStuffCallback", "onCompleted null inputstream" );
                }
                mProgress.setVisibility(View.GONE);
                setProgressBarIndeterminateVisibility(Boolean.FALSE);
            }
        }
    }
}
