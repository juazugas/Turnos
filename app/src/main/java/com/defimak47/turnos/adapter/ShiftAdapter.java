package com.defimak47.turnos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.defimak47.turnos.R;
import com.defimak47.turnos.helpers.ContactInfoHelper;
import com.defimak47.turnos.helpers.ShiftInfoHelper;
import com.defimak47.turnos.model.Shift;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jzuriaga on 27/12/14.
 */
public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder> {

    public static final String CALENDAR_EVENT_CONTENT_TYPE = "vnd.android.cursor.item/event";

    public static final String WEEK_TEMPLATE = "Week %d-%d";
    public static final String SPRINT_TEMPLATE = "Sprint #%d";
    public static final String PAIRING_TEMPLATE = "Pairing %d";

    public static final String BTN_DATE_FORMAT_PATTERN = "dd'\n'MMM'\n'yyyy";
    public static final String DATE_FORMAT_PATTERN = "dd MMM yyyy";
    public static final String DATE_TITLE_TEMPLATE = "Start %s - End %s";

    public static final String CALENDAR_TITLE_TEMPLATE = "Turno Mx %s + %s. Week %d";
    private static final int CURRENT_SHIFT = 1;

    public static long MILLIS_IN_HOUR =  60*60*1000;

    private Context context;
    private List<Shift> shiftList;
    private final List<Shift> allShifts;

    public ShiftAdapter(Context context, List<Shift> shiftList) {
        this.context = context;
        this.shiftList = shiftList;
        this.allShifts = new ArrayList<>(shiftList);
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }

    @Override
    public void onBindViewHolder(final ShiftViewHolder shiftViewHolder, int position) {
        final Shift shift = shiftList.get(position);
        int viewType = getItemViewType(position);
        shiftViewHolder.vSprint.setText(String.format(SPRINT_TEMPLATE, shift.getSprint()));
        shiftViewHolder.vWeek.setText(String.format(WEEK_TEMPLATE, shift.getYear(), shift.getWeek()));
        shiftViewHolder.vPairing.setText(String.format(PAIRING_TEMPLATE, shift.getPairing()));
        shiftViewHolder.vImasdMx1.setText(shift.getImasdMx1());
        shiftViewHolder.vImasdMx2.setText(shift.getImasdMx2());
        shiftViewHolder.vImasdEu.setText(shift.getImasdEu());
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            shiftViewHolder.vStartDate.setClipToOutline(true);
        }
        if (viewType==CURRENT_SHIFT) {
            shiftViewHolder.vStartDate.setText(getDateTitle(shift.getStartDate()));
            Ion.with(getContext())
                    .load(String.format(ContactInfoHelper.HTTP_IMAGE_URI_TEMPLATE, shift.getImasdEu()))
                    .setLogging("onBindViewHolder", Log.INFO)
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (null==e) {
                                shiftViewHolder.vImageImasdEu.setImageBitmap(result);
                            }
                        }
                    });
            Ion.with(getContext())
                    .load(String.format(ContactInfoHelper.HTTP_IMAGE_URI_TEMPLATE, shift.getImasdMx1()))
                    .setLogging("onBindViewHolder", Log.INFO)
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (null==e) {
                                shiftViewHolder.vImageImasdMx1.setImageBitmap(result);
                            }
                        }
                    });
            Ion.with(getContext())
                    .load(String.format(ContactInfoHelper.HTTP_IMAGE_URI_TEMPLATE, shift.getImasdMx2()))
                    .setLogging("onBindViewHolder", Log.INFO)
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (null==e) {
                                shiftViewHolder.vImageImasdMx2.setImageBitmap(result);
                            }
                        }
                    });
        } else {
            View view = (null!=shiftViewHolder.vFabAddCal) ? shiftViewHolder.vFabAddCal : shiftViewHolder.vStartDate;
            shiftViewHolder.vStartDate.setText(DateFormat.format(BTN_DATE_FORMAT_PATTERN, shift.getStartDate()));
            if (ShiftInfoHelper.isPastShift(shift)) {
                view.setClickable(false);
            } else {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType(CALENDAR_EVENT_CONTENT_TYPE);
                        String title = String.format(CALENDAR_TITLE_TEMPLATE, shift.getImasdMx1(), shift.getImasdMx2(), shift.getWeek());
                        intent.putExtra(CalendarContract.Events.TITLE,
                                String.format(CALENDAR_TITLE_TEMPLATE, shift.getImasdMx1(), shift.getImasdMx2(), shift.getWeek()));
                        String description = String.format(SPRINT_TEMPLATE, shift.getSprint());
                        intent.putExtra(CalendarContract.Events.DESCRIPTION,
                                new StringBuilder().append(title).append("\n").append(description).toString());
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Edicom");
                        intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                        long dtStart = shift.getStartDate().getTime();
                        long duration = 9 * MILLIS_IN_HOUR;
                        long dtNotify = dtStart - 20 * MILLIS_IN_HOUR;
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, shift.getStartDate().getTime());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dtStart + duration);
                        intent.putExtra(CalendarContract.CalendarAlerts.ALARM_TIME, dtNotify);
                        ShiftAdapter.this.getContext().startActivity(intent);
                    }
                });
            }
        }
    }

    private String getDateTitle (Date date) {
        CharSequence startDate = DateFormat.format(DATE_FORMAT_PATTERN, date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 5);
        CharSequence endDate = DateFormat.format(DATE_FORMAT_PATTERN, calendar.getTime());
        return String.format(DATE_TITLE_TEMPLATE, startDate, endDate);
    }

    @Override
    public int getItemViewType(int position) {
        Shift shift = shiftList.get(position);
        if (ShiftInfoHelper.isCurrentShift(shift)) {
            return CURRENT_SHIFT;
        }
        return 0;
    }

    @Override
    public ShiftViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView;
        if (viewType == CURRENT_SHIFT) {
            itemView = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.card_shift_current, viewGroup, false);
        } else {
            itemView = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.card_shift, viewGroup, false);
        }
        return new ShiftViewHolder(itemView);
    }

    public Context getContext() {
        return context;
    }

    public void flushFilter(){
        this.shiftList=new ArrayList<>();
        this.shiftList.addAll(this.allShifts);
        notifyDataSetChanged();
    }

    public void setFilter(String queryText) {

        this.shiftList = new ArrayList<>();
        for (Shift shift: this.allShifts) {
            if (shiftMatches(shift, queryText)) {
                this.shiftList.add(shift);
            }
        }
        notifyDataSetChanged();
    }

    private boolean shiftMatches (Shift shift, String queryText) {
        String lowerQueryText = queryText.toLowerCase();
        String dateText = DateFormat.format(DATE_FORMAT_PATTERN, shift.getStartDate()).toString();
        return shift.getImasdMx1().toLowerCase().contains(lowerQueryText) ||
               shift.getImasdMx2().toLowerCase().contains(lowerQueryText) ||
               shift.getImasdEu().toLowerCase().contains(lowerQueryText) ||
               dateText.toLowerCase().contains(lowerQueryText);
    }


    /**
     * Holder for the Recycler view.
     */
    public static class ShiftViewHolder extends RecyclerView.ViewHolder {

        protected Button vStartDate;
        protected FloatingActionButton vFabAddCal;
        protected TextView vSprint;
        protected TextView vWeek;
        protected TextView vImasdMx1;
        protected TextView vImasdMx2;
        protected TextView vImasdEu;
        protected TextView vPairing;

        // for current shift
        protected ImageView vImageImasdEu;
        protected ImageView vImageImasdMx1;
        protected ImageView vImageImasdMx2;

        public ShiftViewHolder(View v) {
            super(v);
            if (null!=v.findViewById(R.id.buttonCalendar)) {
                vFabAddCal = (FloatingActionButton) v.findViewById(R.id.buttonCalendar);
            }
            if (null!=v.findViewById(R.id.textStartDate)) {
                vStartDate = (Button) v.findViewById(R.id.textStartDate);
            }
            vSprint = (TextView) v.findViewById(R.id.textSprint);
            vWeek = (TextView) v.findViewById(R.id.textWeek);
            vImasdMx1 = (TextView) v.findViewById(R.id.textImasdMx1);
            vImasdMx2 = (TextView) v.findViewById(R.id.textImasdMx2);
            vImasdEu = (TextView) v.findViewById(R.id.textImasdEu);
            vPairing = (TextView) v.findViewById(R.id.textPairing);
            if (null!=v.findViewById(R.id.imImasdEu)) {
                vImageImasdEu = (ImageView) v.findViewById(R.id.imImasdEu);
            }
            if (null!=v.findViewById(R.id.imImasdMx1)) {
                vImageImasdMx1 = (ImageView) v.findViewById(R.id.imImasdMx1);
            }
            if (null!=v.findViewById(R.id.imImasdMx2)) {
                vImageImasdMx2 = (ImageView) v.findViewById(R.id.imImasdMx2);
            }

        }
    }

}
