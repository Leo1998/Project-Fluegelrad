package de.projectfluegelrad.Calendar;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalenderRecyclerViewAdapter extends RecyclerView.Adapter<CalenderRecyclerViewAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView id;
        private CardView cv;

        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            date = (TextView)itemView.findViewById(R.id.date);
            id = (TextView)itemView.findViewById(R.id.id);
        }

        public TextView getDate() {
            return date;
        }

        public TextView getId() {
            return id;
        }
    }

    List<Event> eventList;

    CalenderRecyclerViewAdapter(List<Event> eventList){
        this.eventList = eventList;
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

        holder.getDate().setText(String.valueOf(i.get(Calendar.DAY_OF_MONTH)+1));
        holder.getId().setText(String.valueOf(i.get(Calendar.MONTH)));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
