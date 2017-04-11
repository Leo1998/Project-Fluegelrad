package de.doaktiv.android.fragments;


import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.doaktiv.R;
import de.doaktiv.android.DoaktivActivity;
import de.doaktiv.database.DatabaseManager;
import de.doaktiv.database.Event;
import de.doaktiv.android.fragments.day.CalendarDayFragment;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private DoaktivActivity activity;

    private boolean homeView;

    public RecyclerViewAdapter(boolean homeView) {
        this.homeView = homeView;
    }

    public void setActivity(DoaktivActivity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_item, parent, false);

        ViewHolder item = new ViewHolder(v);
        return item;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Event event;

        if (homeView) {
            event = DatabaseManager.INSTANCE.getHomeEventList().get(position);
        } else {
            event = DatabaseManager.INSTANCE.getRecentEventList().get(position);
        }

        Calendar i = event.getDateStart();

        holder.getCategoryTextView().setText(event.getName());

        if (event.getImages().size() > 0) {
            holder.getImageView().setImageAsync(event.getImages().get(0));
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("E \ndd.MM.yyyy \n HH:mm");
        holder.getDateTextView().setText(simpleDateFormat.format(event.getDateStart().getTime()));

        holder.getLocationTextView().setText(event.getLocation().getAddress());
        holder.getHostTextView().setText("N/A");

        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("eventId", event.getId());

                CalendarDayFragment calendarDayFragment = new CalendarDayFragment();
                calendarDayFragment.setArguments(bundle);

                if (activity != null) {
                    activity.getController().openScreen(calendarDayFragment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (homeView) {
            return DatabaseManager.INSTANCE.getHomeEventList().size();
        } else {
            return DatabaseManager.INSTANCE.getRecentEventList().size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dateTextView;
        private TextView categoryTextView;
        private TextView locationTextView;
        private TextView hostTextView;
        private AsyncImageView imageView;

        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            dateTextView = (TextView) itemView.findViewById(R.id.date);
            categoryTextView = (TextView) itemView.findViewById(R.id.category);
            locationTextView = (TextView) itemView.findViewById(R.id.location);
            hostTextView = (TextView) itemView.findViewById(R.id.host);
            imageView = (AsyncImageView) itemView.findViewById(R.id.imageView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }

        public TextView getDateTextView() {
            return dateTextView;
        }

        public TextView getCategoryTextView() {
            return categoryTextView;
        }

        public TextView getLocationTextView() {
            return locationTextView;
        }

        public TextView getHostTextView() {
            return hostTextView;
        }

        public AsyncImageView getImageView() {
            return imageView;
        }

        public CardView getCardView() {
            return cardView;
        }
    }
}
