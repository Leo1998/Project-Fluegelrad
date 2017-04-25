package de.doaktiv.android.fragments.calendar.gridview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

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
        /**final List<Event> events = DatabaseManager.INSTANCE.getDatabase().getEvents(eventIds);

         String[] shortDescriptions = new String[events.size()];
         for (int i = 0; i < events.size(); i++) {
         shortDescriptions[i] = events.get(i).getName();
         }

         builder.setItems(shortDescriptions, new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", events.get(i).getId());

        EventViewFragment eventViewFragment = new EventViewFragment();
        eventViewFragment.setArguments(bundle);

        //CalendarDayDialog.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, eventViewFragment).addToBackStack("eventViewFragment").commit();
        }
        });*///TODO

        return builder.create();
    }
}