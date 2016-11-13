package de.projectfluegelrad.calendar.gridview;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.calendar.CalendarDayFragment;
import de.projectfluegelrad.database.Event;

public class CalendarDayDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        builder.setTitle("Wähle ein Event");

        List<Event> events = getArguments().getParcelableArrayList("eventList");
        String[] shortDescriptions = new String[events.size()];
        for (int i = 0; i < events.size(); i++){
            shortDescriptions[i] = events.get(i).getCategory() + " von " + events.get(i).getHost();
        }

        builder.setItems(shortDescriptions, (dialogInterface, i) -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", events.get(i));

            CalendarDayFragment calendarDayFragment = new CalendarDayFragment();
            calendarDayFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarDayFragment).addToBackStack("calendarDayFragment").commit();
        });

        return builder.create();
    }
}