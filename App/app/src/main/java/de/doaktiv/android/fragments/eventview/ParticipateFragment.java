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
import de.doaktiv.android.base.Toolbar;
import de.doaktiv.database.Database;
import de.doaktiv.database.Event;

public class ParticipateFragment extends DoaktivFragment {

    private LinearLayout layout;
    private CardView statusCard;
    private CardView participateCard;
    private CardView ratingCard;
    private Button participateButton;
    private Button ratingButton;
    private RatingBar ratingBar;

    private boolean participateButtonClickable = true;

    @Override
    public View createView(Context context) {
        this.layout = (LinearLayout) inflater().inflate(R.layout.participate_fragment, null, false);
        this.statusCard = (CardView) layout.findViewById(R.id.statusCard);
        this.participateCard = (CardView) layout.findViewById(R.id.participateCard);
        this.ratingCard = (CardView) layout.findViewById(R.id.ratingCard);
        this.participateButton = (Button) layout.findViewById(R.id.participateButton);
        this.ratingButton = (Button) layout.findViewById(R.id.ratingButton);
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
        Toolbar toolbar = this.getToolbar();
        if (toolbar != null) {
            toolbar.setTitleText(event.getName());
        }

        if (event.getMaxParticipants() == -1) {
            statusCard.setVisibility(View.VISIBLE);
            participateCard.setVisibility(View.GONE);
            ratingCard.setVisibility(View.VISIBLE);
        } else {
            statusCard.setVisibility(View.GONE);
            participateCard.setVisibility(View.VISIBLE);
            ratingCard.setVisibility(event.isParticipating() ? View.VISIBLE : View.INVISIBLE);

            final TextView participantsTextView = (TextView) layout.findViewById(R.id.participantsTextView);
            participantsTextView.setText(getResources().getString(R.string.participants) + ": " + event.getParticipants() + " / " + event.getMaxParticipants());

            final TextView messageTextView = (TextView) layout.findViewById(R.id.messageTextView);
            messageTextView.setText(event.isParticipating() ? getResources().getString(R.string.participating) : "");
        }

        participateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!event.isParticipating() && participateButtonClickable) {
                    participateButtonClickable = false;

                    event.participate(true);

                    final DatabaseService databaseService = getRootController().getDatabaseService();
                    databaseService.participateEvent(event, new Runnable() {
                        @Override
                        public void run() {
                            assignData(event);
                        }
                    });
                }
            }
        });

        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = Math.max(0, Math.min(5, (int) ratingBar.getRating()));

                final DatabaseService databaseService = getRootController().getDatabaseService();
                databaseService.rateEvent(event, r, null);
            }
        });
    }
}
