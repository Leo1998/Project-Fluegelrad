package de.doaktiv.android.fragments.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.doaktiv.R;
import de.doaktiv.database.Database;
import de.doaktiv.database.Event;

public class CalendarMonthView extends LinearLayout {

    public interface DaySelectListener {
        public void onDaySelect(Calendar day);
    }

    private static final int DAYS_COUNT = 42;

    private final String DATE_FORMAT = "MMMM yyyy";
    private Calendar currentDate = Calendar.getInstance();

    private LinearLayout header;
    private GridView grid;

    private Database database;

    private CalendarAdapter calendarAdapter;
    private List<Calendar> daysShown = new ArrayList<>();
    private String currentDateTitle;
    private DaySelectListener daySelectListener;

    public CalendarMonthView(Context context) {
        super(context);
        init(context);
    }

    public CalendarMonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calendar_month, this);

        assignUiElements();
        loadDateFormat();

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calendar day = daysShown.get(position);

                if (daySelectListener != null) {
                    daySelectListener.onDaySelect(day);
                }
            }
        });

        updateCalendar();
    }

    public String getCurrentDateTitle() {
        return currentDateTitle;
    }

    public void setDatabase(Database database) {
        this.database = database;
        updateCalendar();
    }

    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
        updateCalendar();
    }

    public DaySelectListener getDaySelectListener() {
        return daySelectListener;
    }

    public void setDaySelectListener(DaySelectListener daySelectListener) {
        this.daySelectListener = daySelectListener;
    }

    private void loadDateFormat() {
        header.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.day_week));
        LinearLayout.LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        Calendar calendarTemp = (Calendar) currentDate.clone();
        for (int i = 0; i < 7; i++) {
            TextView day = new TextView(getContext());
            calendarTemp.set(2016, 7, i + 1);
            day.setText(new SimpleDateFormat("EEE").format(calendarTemp.getTime()));
            day.setLayoutParams(params);
            day.setGravity(Gravity.CENTER_HORIZONTAL);

            header.addView(day);
        }
    }

    private void assignUiElements() {
        this.header = (LinearLayout) findViewById(R.id.calendar_header);
        this.grid = (GridView) findViewById(R.id.calendar_grid);
    }

    private void updateCalendar() {
        this.daysShown.clear();
        Calendar calendar = (Calendar) currentDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 6 : calendar.get(Calendar.DAY_OF_WEEK) - 1 - 1;

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        while (daysShown.size() < DAYS_COUNT) {
            daysShown.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        this.calendarAdapter = new CalendarAdapter(getContext(), daysShown);
        this.grid.setAdapter(this.calendarAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        currentDateTitle = sdf.format(currentDate.getTime());
    }

    private class CalendarAdapter extends ArrayAdapter<Calendar> {

        private LayoutInflater inflater;

        public CalendarAdapter(Context context, List<Calendar> days) {
            super(context, R.layout.calendar_item, days);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Calendar date = getItem(position);
            int day = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH);
            int year = date.get(Calendar.YEAR);

            Calendar today = Calendar.getInstance();

            if (view == null) {
                view = inflater.inflate(R.layout.calendar_item, parent, false);
            }

            if (database != null) {
                for (Event eventDate : database.getEventList()) {
                    Calendar cal = eventDate.getDateStart();

                    if (cal.get(Calendar.DAY_OF_MONTH) == day && cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.event));

                        break;
                    }
                }
            }

            ((CircleText) view).setTypeface(null, Typeface.NORMAL);
            ((CircleText) view).setTextColor(Color.BLACK);

            if (month != currentDate.get(Calendar.MONTH) || year != currentDate.get(Calendar.YEAR)) {
                ((CircleText) view).setTextColor(ContextCompat.getColor(getContext(), R.color.greyed_out));
            } else if (month == today.get(Calendar.MONTH) && year == today.get(Calendar.YEAR) && day == today.get(Calendar.DAY_OF_MONTH)) {
                ((CircleText) view).setTypeface(null, Typeface.BOLD);
                ((CircleText) view).setTextColor(ContextCompat.getColor(getContext(), R.color.today));
            }

            ((CircleText) view).setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));

            GridView.LayoutParams params = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, grid.getColumnWidth());
            view.setLayoutParams(params);

            return view;
        }
    }
}