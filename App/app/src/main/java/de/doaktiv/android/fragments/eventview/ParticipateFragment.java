package de.doaktiv.android.fragments.eventview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import de.doaktiv.R;
import de.doaktiv.android.DatabaseService;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.database.Database;
import de.doaktiv.database.Event;

public class ParticipateFragment extends DoaktivFragment {

    private LinearLayout layout;
    private CardView ratingCard;
    private Button participateButton;
    private RatingBar ratingBar;

    private boolean participateButtonClickable = true;

    @Override
    public View createView(Context context) {
        this.layout = (LinearLayout) inflater().inflate(R.layout.participate_fragment, null, false);
        this.ratingCard = (CardView) layout.findViewById(R.id.ratingCard);
        this.participateButton = (Button) layout.findViewById(R.id.participateButton);
        this.ratingBar = (RatingBar) layout.findViewById(R.id.ratingBar);

        return layout;
    }

    @Override
    public void onDatabaseReceived(final Database database) {
        if (this.getFragmentView() != null) {
            this.getFragmentView().post(new Runnable() {
                @Override
                public void run() {
                    Event event = database.getEvent(getArguments().getInt("eventId"));

                    assignData(event);
                }
            });
        }
    }

    private void assignData(final Event event) {
        final TextView participantsTextView = (TextView) layout.findViewById(R.id.participantsTextView);
        participantsTextView.setText(getResources().getString(R.string.participants) + ": " + event.getParticipants() + " / " + event.getMaxParticipants());

        final TextView messageTextView = (TextView) layout.findViewById(R.id.messageTextView);
        messageTextView.setText(event.isParticipating() ? getResources().getString(R.string.participating) : "");

        ratingCard.setVisibility(event.isParticipating() ? View.VISIBLE : View.INVISIBLE);

        participateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!event.isParticipating() && participateButtonClickable) {
                    participateButtonClickable = false;

                    final DatabaseService databaseService = getRootController().getDatabaseService();
                        /*databaseManager.executeTask(new DatabaseParticipateTask(), new Event[]{event}, new DatabaseTaskObserver() {
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
                        });*///TODO
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    int r = Math.max(0, Math.min(5, (int) rating));

                    /**final DatabaseManager databaseManager = ParticipateFragment.this.getApplication().getDatabaseManager();
                     databaseManager.executeTask(new DatabaseRateTask(), new DatabaseRateTask.RateParamsWrapper[]{new DatabaseRateTask.RateParamsWrapper(event, r)}, new DatabaseTaskObserver() {
                    @Override public void onFinish(Object result) {
                    boolean success = (Boolean) result;

                    if (!success) {
                    makeSnackbar(layout, getResources().getString(R.string.rating_error));
                    }
                    }
                    });*///TODO
                }
            }
        });
    }
}
