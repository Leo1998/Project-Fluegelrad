package de.doaktiv.android.fragments.calendar;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.doaktiv.R;
import de.doaktiv.android.RootController;
import de.doaktiv.android.fragments.RecyclerViewAdapter;
import de.doaktiv.database.Event;

public class EventSelectView extends LinearLayout {

    public EventSelectView(Context context, List<Event> eventList, RootController rootController) {
        super(context);

        this.setOrientation(VERTICAL);
        this.setBackgroundColor(0xFFFFFFFF);

        FrameLayout header = new FrameLayout(context);
        header.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(header, params);

        TextView titleText = new TextView(context);
        titleText.setTextAppearance(context, R.style.TitleTextLight);
        titleText.setText(R.string.choose_event);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        header.addView(titleText, params);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        adapter.setController(rootController);
        adapter.setEventList(eventList);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(recyclerView, params);
    }
}
