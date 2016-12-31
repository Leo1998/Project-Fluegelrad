package de.projectfluegelrad.fragments.calendar.listview;

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
import de.projectfluegelrad.fragments.calendar.CalendarDayFragment;
import de.projectfluegelrad.database.Event;

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<CalendarRecyclerViewAdapter.ViewHolder>{

    private List<Event> eventList;

    private FragmentActivity activity;

    CalendarRecyclerViewAdapter(List<Event> events){
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Calendar i = eventList.get(position).getDateStart();

        holder.getNameTextView().setText(eventList.get(position).getName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("E  dd.MM.yyyy HH:mm");
        holder.getDateTextView().setText(simpleDateFormat.format(eventList.get(position).getDateStart().getTime()));

        //TODO
        holder.getLocationTextView().setText(eventList.get(position).getLocation().getAddress());
        holder.getHostTextView().setText("N/A");

        holder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("eventId", eventList.get(position).getId());

                CalendarDayFragment calendarDayFragment = new CalendarDayFragment();
                calendarDayFragment.setArguments(bundle);

                if (activity != null) {
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarDayFragment).addToBackStack("calendarDayFragment").commit();
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
        private TextView nameTextView;
        private TextView locationTextView;
        private TextView hostTextView;

        private CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView)itemView.findViewById(R.id.date);
            nameTextView = (TextView)itemView.findViewById(R.id.name);
            locationTextView = (TextView)itemView.findViewById(R.id.location);
            hostTextView = (TextView)itemView.findViewById(R.id.host);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }

        public TextView getDateTextView() {
            return dateTextView;
        }

        public TextView getNameTextView() {
            return nameTextView;
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
