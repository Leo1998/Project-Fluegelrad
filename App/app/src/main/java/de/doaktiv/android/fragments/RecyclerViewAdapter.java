package de.doaktiv.android.fragments;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import de.doaktiv.R;
import de.doaktiv.android.DoaktivActivity;
import de.doaktiv.database.Event;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private DoaktivActivity activity;
    private List<Event> eventList;

    public RecyclerViewAdapter() {
    }

    public void setActivity(DoaktivActivity activity) {
        this.activity = activity;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_item, parent, false);

        ViewHolder item = new ViewHolder(v);
        return item;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Event event = eventList.get(position);

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
                if (activity != null) {
                    activity.getController().openEventView(event.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
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
