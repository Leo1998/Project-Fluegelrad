package de.doaktiv.android.fragments.calendar;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.android.fragments.eventlist.EventListView;
import de.doaktiv.database.Database;
import de.doaktiv.database.Event;

public class CalendarFragment extends DoaktivFragment {

    private class PagerAdapter extends android.support.v4.view.PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            CalendarMonthView view = null;
            if (position == 0) {
                view = lastMonth;
            } else if (position == 1) {
                view = currentMonth;
            } else if (position == 2) {
                view = nextMonth;
            }

            if (view != null)
                container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private Calendar currentDate = Calendar.getInstance();
    private CalendarMonthView lastMonth;
    private CalendarMonthView currentMonth;
    private CalendarMonthView nextMonth;

    private BottomSheetLayout bottomSheetLayout;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private ViewPager pager;

    private Database database;

    @Override
    public View createView(Context context) {
        this.bottomSheetLayout = (BottomSheetLayout) inflater().inflate(R.layout.calender_fragment, null, false);

        init();

        return bottomSheetLayout;
    }

    private void init() {
        this.btnPrev = (ImageView) bottomSheetLayout.findViewById(R.id.calendar_prev_button);
        this.btnNext = (ImageView) bottomSheetLayout.findViewById(R.id.calendar_next_button);
        this.txtDate = (TextView) bottomSheetLayout.findViewById(R.id.calendar_date_display);
        this.pager = (ViewPager) bottomSheetLayout.findViewById(R.id.calendar_pager);

        this.lastMonth = new CalendarMonthView(getApplication());
        this.currentMonth = new CalendarMonthView(getApplication());
        this.nextMonth = new CalendarMonthView(getApplication());
        updateCurrentDate(0);//sets dates for the three views

        CalendarMonthView.DaySelectListener daySelectListener = new CalendarMonthView.DaySelectListener() {
            @Override
            public void onDaySelect(Calendar day) {
                presentDay(day);
            }
        };
        this.currentMonth.setDaySelectListener(daySelectListener);

        pager.setAdapter(new PagerAdapter());
        pager.setCurrentItem(1);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int position = pager.getCurrentItem();

                    int monthChange = position - 1;
                    if (monthChange != 0) {
                        updateCurrentDate(monthChange);

                        pager.setCurrentItem(1, false);
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.arrowScroll(View.FOCUS_RIGHT);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.arrowScroll(View.FOCUS_LEFT);
            }
        });
    }

    private void updateCurrentDate(int monthChange) {
        this.currentDate.add(Calendar.MONTH, monthChange);

        Calendar lastMonthDate = ((Calendar) currentDate.clone());
        lastMonthDate.add(Calendar.MONTH, -1);
        this.lastMonth.setCurrentDate(lastMonthDate);

        this.currentMonth.setCurrentDate(this.currentDate);

        Calendar nextMonthDate = ((Calendar) currentDate.clone());
        nextMonthDate.add(Calendar.MONTH, +1);
        this.nextMonth.setCurrentDate(nextMonthDate);

        this.txtDate.setText(this.currentMonth.getCurrentDateTitle());
    }

    private void presentDay(Calendar day) {
        List<Event> eventsOnDate = new ArrayList<>();
        if (database != null) {
            for (Event e : database.getEventList()) {
                Calendar calendar = e.getDateStart();
                if (calendar.get(Calendar.YEAR) == day.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == day.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH)) {
                    eventsOnDate.add(e);
                }
            }
        }
        if (eventsOnDate.size() == 1) {
            getRootController().openEventView(eventsOnDate.get(0).getId());
        }
        if (eventsOnDate.size() > 1) {
            LinearLayout container = new LinearLayout(getApplication());
            container.setOrientation(LinearLayout.VERTICAL);
            container.setBackgroundColor(0xFFFFFFFF);

            FrameLayout header = new FrameLayout(getApplication());
            header.setBackgroundColor(ContextCompat.getColor(getApplication(), R.color.colorPrimary));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            container.addView(header, params);

            TextView titleText = new TextView(getApplication());
            titleText.setTextAppearance(getApplication(), R.style.TitleTextLight);
            titleText.setText(R.string.choose_event);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            header.addView(titleText, params);

            EventListView listView = new EventListView(getApplication());
            listView.setEventList(eventsOnDate, getRootController());
            container.addView(listView);

            listView.setOnEventSelectedListener(new EventListView.OnEventSelectedListener() {
                @Override
                public void onEventSelect(final Event event) {
                    bottomSheetLayout.dismissSheet();
                    getRootController().openEventView(event.getId());
                }
            });

            bottomSheetLayout.showWithSheetView(container);
        }
    }

    @Override
    public void onDatabaseReceived(final Database database) {
        if (this.getFragmentView() != null) {
            this.database = database;

            this.lastMonth.setDatabase(database);
            this.currentMonth.setDatabase(database);
            this.nextMonth.setDatabase(database);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (this.bottomSheetLayout != null) {
            this.bottomSheetLayout.clearAnimation();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (bottomSheetLayout.isSheetShowing()) {
            bottomSheetLayout.dismissSheet();
            return true;
        }

        return super.onBackPressed();
    }

}
