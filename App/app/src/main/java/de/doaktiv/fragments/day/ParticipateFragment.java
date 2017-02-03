package de.doaktiv.fragments.day;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.doaktiv.MainActivity;
import de.doaktiv.R;
import de.doaktiv.database.DatabaseManager;
import de.doaktiv.database.DatabaseParticipateTask;
import de.doaktiv.database.DatabaseTaskWatcher;
import de.doaktiv.database.Event;

import static de.doaktiv.database.DatabaseManager.INSTANCE;

public class ParticipateFragment extends Fragment {

    private Event event;

    private boolean clickable = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.event = INSTANCE.getEvent(getArguments().getInt("eventId"));

        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.participate_fragment, container, false);

        if (event != null) {
            assignData(layout);

            final Button participateButton = (Button) layout.findViewById(R.id.participateButton);
            participateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!event.isParticipating() && clickable) {
                        clickable = false;

                        final DatabaseManager databaseManager = ((MainActivity) ParticipateFragment.this.getActivity()).getDatabaseManager();

                        databaseManager.executeTask(new DatabaseParticipateTask(), new Event[] {event}, new DatabaseTaskWatcher() {
                            @Override
                            public void onFinish(Object result) {
                                boolean success = (Boolean) result;

                                if (success) {
                                    assignData(layout);
                                } else {
                                    makeSnackbar(layout, getResources().getString(R.string.participate_error));

                                    clickable = true;
                                }
                            }
                        });
                    }
                }
            });
        }

        return layout;
    }

    private void assignData(LinearLayout layout) {
        final TextView participantsTextView = (TextView) layout.findViewById(R.id.participantsTextView);
        participantsTextView.setText(getResources().getString(R.string.participants) + ": " + event.getParticipants() + " / " + event.getMaxParticipants());

        final TextView messageTextView = (TextView) layout.findViewById(R.id.messageTextView);
        messageTextView.setText(event.isParticipating() ? getResources().getString(R.string.participating) : "");
    }

    private void makeSnackbar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
