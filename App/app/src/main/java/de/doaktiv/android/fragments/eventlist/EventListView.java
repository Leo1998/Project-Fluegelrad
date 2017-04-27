package de.doaktiv.android.fragments.eventlist;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.doaktiv.R;
import de.doaktiv.android.RootController;
import de.doaktiv.android.fragments.AsyncImageView;
import de.doaktiv.database.Event;

public class EventListView extends FrameLayout {

    public interface OnEventSelectedListener {

        void onEventSelect(Event event);

    }

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private RootController rootController;

    private OnEventSelectedListener onEventSelectedListener = new OnEventSelectedListener() {
        @Override
        public void onEventSelect(Event event) {
            rootController.openEventView(event.getId());
        }
    };

    public EventListView(Context context) {
        super(context);
        init(context);
    }

    public EventListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EventListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.setBackgroundColor(0xFFFFFFFF);

        this.recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addView(recyclerView, params);
    }

    public void setEventList(List<Event> eventList, RootController rootController) {
        this.rootController = rootController;

        if (this.adapter == null) {
            this.adapter = new RecyclerViewAdapter();
            recyclerView.setAdapter(adapter);
        }

        adapter.setEventList(eventList);
    }

    public OnEventSelectedListener getOnEventSelectedListener() {
        return onEventSelectedListener;
    }

    public void setOnEventSelectedListener(OnEventSelectedListener onEventSelectedListener) {
        this.onEventSelectedListener = onEventSelectedListener;
    }

    /**
     * RecyclerViewAdapter
     */
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<Event> eventList = new ArrayList<>();

        public RecyclerViewAdapter() {
        }

        public void setEventList(List<Event> eventList) {
            this.eventList = eventList;
            this.notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);

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
                    EventListView.this.onEventSelectedListener.onEventSelect(event);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

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
}
