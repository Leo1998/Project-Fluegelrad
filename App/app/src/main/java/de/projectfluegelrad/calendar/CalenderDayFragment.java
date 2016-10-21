package de.projectfluegelrad.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.projectfluegelrad.database.Event;

public class CalenderDayFragment extends Fragment{

    private Event event;

    public CalenderDayFragment() {
        this.event = getArguments().getParcelable("event");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
