package de.projectfluegelrad.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import de.projectfluegelrad.R;

public class CalendarView extends LinearLayout {

    private static final int DAYS_COUNT = 42;

    private static final String DATE_FORMAT = "MMM yyyy";

    private String dateFormat;
    private Calendar currentDate = Calendar.getInstance();

    private static List<Calendar> events;

    private EventHandler eventHandler = null;

    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }


    private void initControl(Context context, AttributeSet attrs) {
        events = new ArrayList<>();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_controls, this);

        assignUiElements();
        assignClickHandlers();
        loadDateFormat(attrs);

        updateCalendar();
    }

    public static List<Calendar> getEvents() {
        return events;
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null) {
                dateFormat = DATE_FORMAT;
            }
        } finally {
            ta.recycle();
        }

        header.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.day_week));
        LinearLayout.LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        Calendar calendarTemp = (Calendar) currentDate.clone();
        for (int i = 0; i < 7; i++){
            TextView day = new TextView(getContext());
            calendarTemp.set(2016, 7, i + 1);
            day.setText(new SimpleDateFormat("EEE").format(calendarTemp.getTime()));
            day.setLayoutParams(params);
            day.setGravity(Gravity.CENTER_HORIZONTAL);

            header.addView(day);
        }
    }
    private void assignUiElements() {
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers() {
        btnNext.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        btnPrev.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        grid.setOnItemLongClickListener((view, cell, position, id) -> {
            if (eventHandler == null) {
                return false;
            }

            eventHandler.onDayLongPress((Calendar) view.getItemAtPosition(position));
            return true;
        });
    }

    public void updateCalendar() {
        ArrayList<Calendar> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        while (cells.size() < DAYS_COUNT) {
            cells.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(currentDate.getTime()));
    }

    private class CalendarAdapter extends ArrayAdapter<Calendar> {
        private List<Calendar> eventDays;

        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Calendar> days, List<Calendar> eventDays) {
            super(context, R.layout.control_calendar_day, days);
            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            Calendar date = getItem(position);
            int day = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH);
            int year = date.get(Calendar.YEAR);

            Calendar today = Calendar.getInstance();


            if (view == null) {
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);
            }

            if (eventDays != null) {
                for (Calendar eventDate : eventDays) {
                    if (eventDate.get(Calendar.DAY_OF_MONTH) == day && eventDate.get(Calendar.MONTH) == month && eventDate.get(Calendar.YEAR) == year) {
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.event));

                        break;
                    }
                }
            }

            ((CircleText)view).setTypeface(null, Typeface.NORMAL);
            ((CircleText)view).setTextColor(Color.BLACK);

            if (month != currentDate.get(Calendar.MONTH) || year != currentDate.get(Calendar.YEAR)) {
                ((CircleText)view).setTextColor(ContextCompat.getColor(getContext(), R.color.greyed_out));
            } else if (month == today.get(Calendar.MONTH) && year == today.get(Calendar.YEAR) && day == today.get(Calendar.DAY_OF_MONTH)) {
                ((CircleText)view).setTypeface(null, Typeface.BOLD);
                ((CircleText)view).setTextColor(ContextCompat.getColor(getContext(), R.color.today));
            }

            ((CircleText)view).setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));

            GridView.LayoutParams params = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, grid.getColumnWidth());
            view.setLayoutParams(params);


            return view;
        }
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public interface EventHandler {
        void onDayLongPress(Calendar date);
    }
}