package de.projectfluegelrad.calendar.listview;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.calendar.CalenderDayFragment;
import de.projectfluegelrad.database.Event;

public class CalenderRecyclerViewAdapter extends RecyclerView.Adapter<CalenderRecyclerViewAdapter.ViewHolder>{

    private List<Event> eventList;
    private FragmentActivity activity;

    CalenderRecyclerViewAdapter(List<Event> events){
        this.eventList = events;
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calender_list_item, parent, false);

        ViewHolder item = new ViewHolder(v);
        return item;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Calendar i = Calendar.getInstance();
        i.setTime(eventList.get(position).getDate());

        holder.getCategoryTextView().setText(eventList.get(position).getCategory());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("E  dd.MM.yyyy HH:mm");
        holder.getDateTextView().setText(simpleDateFormat.format(eventList.get(position).getDate()));

        //TODO
        holder.getLocationTextView().setText(eventList.get(position).getLocation());
        holder.getHostTextView().setText(eventList.get(position).getHost());

        holder.getCardView().setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", eventList.get(position));

            CalenderDayFragment calenderDayFragment = new CalenderDayFragment();
            calenderDayFragment.setArguments(bundle);

            if (activity != null) {
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calenderDayFragment).addToBackStack("calenderDayFragment").commit();
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

        private CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView)itemView.findViewById(R.id.date);
            categoryTextView = (TextView)itemView.findViewById(R.id.category);
            locationTextView = (TextView)itemView.findViewById(R.id.location);
            hostTextView = (TextView)itemView.findViewById(R.id.host);

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

        public CardView getCardView() {
            return cardView;
        }
    }
}
