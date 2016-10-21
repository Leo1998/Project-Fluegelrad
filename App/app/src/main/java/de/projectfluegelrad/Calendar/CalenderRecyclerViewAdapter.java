package de.projectfluegelrad.calendar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalenderRecyclerViewAdapter extends RecyclerView.Adapter<CalenderRecyclerViewAdapter.ViewHolder>{

    private List<Event> eventList;

    CalenderRecyclerViewAdapter(List<Event> events){
        this.eventList = new ArrayList<>();

        for (int i = events.size()-1; i >= 0; i--){
            if (events.get(i).getDate().compareTo(new Date(System.currentTimeMillis())) > 0){
                this.eventList.add(events.get(i));
            }else {
                break;
            }
        }

        Collections.reverse(eventList);

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

        ViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView)itemView.findViewById(R.id.date);
            categoryTextView = (TextView)itemView.findViewById(R.id.category);
            locationTextView = (TextView)itemView.findViewById(R.id.location);
            hostTextView = (TextView)itemView.findViewById(R.id.host);
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
    }
}
