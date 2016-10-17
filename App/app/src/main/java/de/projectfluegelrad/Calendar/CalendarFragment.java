package de.projectfluegelrad.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import de.projectfluegelrad.R;

public class CalendarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.calendar, container, false);

        CalendarView calendarView = (CalendarView) linearLayout.findViewById(R.id.calendar_view);

        Random r = new Random();
        for (int i = 0; i < 50; i++){
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, r.nextInt(100) - 50);
            CalendarView.getEvents().add((Calendar) c.clone());
        }

        return linearLayout;
    }
}
