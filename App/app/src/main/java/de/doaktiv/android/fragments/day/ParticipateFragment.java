package de.doaktiv.android.fragments.day;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import de.doaktiv.R;
import de.doaktiv.android.DoaktivActivity;
import de.doaktiv.android.DoaktivFragment;
import de.doaktiv.database.DatabaseManager;
import de.doaktiv.database.DatabaseParticipateTask;
import de.doaktiv.database.DatabaseRateTask;
import de.doaktiv.database.DatabaseTaskWatcher;
import de.doaktiv.database.Event;

import static de.doaktiv.database.DatabaseManager.INSTANCE;

public class ParticipateFragment extends DoaktivFragment {

    private Event event;

    private boolean participateButtonClickable = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.event = INSTANCE.getEvent(getArguments().getInt("eventId"));

        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.participate_fragment, container, false);

        if (event != null) {
            assignData(layout);

            final CardView ratingCard = (CardView) layout.findViewById(R.id.ratingCard);
            ratingCard.setVisibility(event.isParticipating() ? View.VISIBLE : View.INVISIBLE);

            final Button participateButton = (Button) layout.findViewById(R.id.participateButton);
            participateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!event.isParticipating() && participateButtonClickable) {
                        participateButtonClickable = false;

                        final DatabaseManager databaseManager = ((DoaktivActivity) ParticipateFragment.this.getActivity()).getDatabaseManager();
                        databaseManager.executeTask(new DatabaseParticipateTask(), new Event[]{event}, new DatabaseTaskWatcher() {
                            @Override
                            public void onFinish(Object result) {
                                boolean success = (Boolean) result;

                                if (success) {
                                    assignData(layout);

                                    ratingCard.setVisibility(View.VISIBLE);
                                } else {
                                    makeSnackbar(layout, getResources().getString(R.string.participate_error));

                                    participateButtonClickable = true;
                                }
                            }
                        });
                    }
                }
            });

            final RatingBar ratingBar = (RatingBar) layout.findViewById(R.id.ratingBar);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(final RatingBar ratingBar, float rating, boolean fromUser) {
                    if (fromUser) {
                        int r = Math.max(0, Math.min(5, (int) rating));

                        final DatabaseManager databaseManager = ((DoaktivActivity) ParticipateFragment.this.getActivity()).getDatabaseManager();
                        databaseManager.executeTask(new DatabaseRateTask(), new DatabaseRateTask.RateParamsWrapper[]{new DatabaseRateTask.RateParamsWrapper(event, r)}, new DatabaseTaskWatcher() {
                            @Override
                            public void onFinish(Object result) {
                                boolean success = (Boolean) result;

                                if (!success) {
                                    makeSnackbar(layout, getResources().getString(R.string.rating_error));
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
