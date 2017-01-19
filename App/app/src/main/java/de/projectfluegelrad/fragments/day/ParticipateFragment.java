package de.projectfluegelrad.fragments.day;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.Event;

import static de.projectfluegelrad.database.DatabaseManager.INSTANCE;

public class ParticipateFragment extends Fragment {

    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.event = INSTANCE.getEvent(getArguments().getInt("eventId"));

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.participate_fragment, container, false);

        if (event != null) {
            final TextView participantsTextView = (TextView) layout.findViewById(R.id.participantsTextView);
            participantsTextView.setText(getResources().getString(R.string.participants) + ": " + event.getParticipants() + " / " + event.getMaxParticipants());

            final Button participateButton = (Button) layout.findViewById(R.id.participateButton);
            if (event.isParticipating()) {
                participateButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                participateButton.setClickable(false);
            }
            participateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (!event.isParticipating()) {
                        DatabaseManager.INSTANCE.request(DatabaseRequest.Participate, new Event[]{event}, false, new DatabaseRequestListener() {
                            @Override
                            public void onFinish() {

                            }
                        });

                        event.participate();

                        participateButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        participateButton.setClickable(false);
                    }*/
                }
            });
        }

        return layout;
    }

}
