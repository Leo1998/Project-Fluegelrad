package de.projectfluegelrad.calendar.gridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalenderGridView extends LinearLayout {

    private static final int DAYS_COUNT = 42;

    private final String DATE_FORMAT = "MMM yyyy";

    private String dateFormat;
    private Calendar currentDate = Calendar.getInstance();

    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    private List<Event> events;

    private ArrayList<Calendar> daysShown;

    public CalenderGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalenderGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }


    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_grid_controls, this);

        assignUiElements();
        assignClickHandlers();
        loadDateFormat(attrs);

        updateCalendar();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalenderGridView);

        try {
            dateFormat = ta.getString(R.styleable.CalenderGridView_dateFormat);
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
    }

    public void updateCalendar() {
        daysShown = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        while (daysShown.size() < DAYS_COUNT) {
            daysShown.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        grid.setAdapter(new CalendarAdapter(getContext(), daysShown));

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(currentDate.getTime()));
    }

    public ArrayList<Calendar> getDaysShown() {
        return daysShown;
    }

    private class CalendarAdapter extends ArrayAdapter<Calendar> {
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Calendar> days) {
            super(context, R.layout.calendar_grid_item, days);
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
                view = inflater.inflate(R.layout.calendar_grid_item, parent, false);
            }

            if (events != null) {
                for (Event eventDate : events) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(eventDate.getDate());

                    if (cal.get(Calendar.DAY_OF_MONTH) == day && cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
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
}