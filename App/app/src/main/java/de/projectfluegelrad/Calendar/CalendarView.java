package de.projectfluegelrad.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarView extends LinearLayout {
    private Calendar calendar;

    public CalendarView(Context context) {
        super(context);

        calendar = Calendar.getInstance();

        setBackgroundColor(Color.YELLOW);

        setOrientation(LinearLayout.VERTICAL);

        TextView month = new TextView(context);
        month.setText(new SimpleDateFormat("MMMM").format(calendar.getTime()));
        addView(month);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        month.setLayoutParams(params);

        LinearLayout days = new LinearLayout(context);
        days.setOrientation(HORIZONTAL);
        Calendar calendarTemp = (Calendar) calendar.clone();
        for (int i = 0; i < 7; i++){
            TextView day = new TextView(context);
            calendarTemp.set(2016, 7, i + 1);
            day.setText(new SimpleDateFormat("EEE").format(calendarTemp.getTime()));

            days.addView(day);
        }
        addView(days);
    }
}
