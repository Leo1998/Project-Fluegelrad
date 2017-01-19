package de.projectfluegelrad.fragments.calendar.gridview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.fragments.day.CalendarDayFragment;

public class CalendarDayDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setTitle("Wähle ein Event");//TODO

        int[] eventIds = getArguments().getIntArray("eventIds");
        List<Event> allEvents = DatabaseManager.INSTANCE.getEventList();
        final List<Event> events = new ArrayList<>();
        for (Event e : allEvents) {
            for (int i = 0; i < eventIds.length; i++) {
                int id = eventIds[i];

                if (e.getId() == id) {
                    events.add(e);
                }
            }
        }
        String[] shortDescriptions = new String[events.size()];
        for (int i = 0; i < events.size(); i++){
            shortDescriptions[i] = events.get(i).getName();
        }

        builder.setItems(shortDescriptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Bundle bundle = new Bundle();
                bundle.putInt("eventId", events.get(i).getId());

                CalendarDayFragment calendarDayFragment = new CalendarDayFragment();
                calendarDayFragment.setArguments(bundle);

                CalendarDayDialog.this.getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fragment_container, calendarDayFragment).addToBackStack("calendarDayFragment").commit();
            }
        });

        return builder.create();
    }
}