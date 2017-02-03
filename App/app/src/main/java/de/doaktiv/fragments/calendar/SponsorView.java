package de.doaktiv.fragments.calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.doaktiv.R;
import de.doaktiv.database.Image;
import de.doaktiv.database.Sponsor;
import de.doaktiv.fragments.AsyncImageView;

public class SponsorView extends RelativeLayout {

    private AsyncImageView sponsorImage;
    private TextView sponsorName;

    private Sponsor sponsor;

    public SponsorView(Context context, FragmentActivity activity) {
        super(context);

        init(context, activity);
    }

    public SponsorView(Context context, AttributeSet attrs, FragmentActivity activity) {
        super(context, attrs);

        init(context, activity);
    }

    private void init(Context context, final FragmentActivity activity) {
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sponsor != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("sponsorId", sponsor.getId());

                    SponsorFragment sponsorFragment = new SponsorFragment();
                    sponsorFragment.setArguments(bundle);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sponsorFragment).addToBackStack("sponsorFragment").commit();
                }
            }
        });

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sponsor_small, this);

        this.sponsorImage = (AsyncImageView) findViewById(R.id.sponsor_image);
        this.sponsorName = (TextView) findViewById(R.id.sponsor_name);
    }

    public void setSponsor(Sponsor sponsor) {
        if (sponsor != null) {
            this.sponsor = sponsor;

            sponsorImage.setImageAsync(new Image(sponsor.getImagePath()));
            sponsorName.setText(sponsor.getName());
        }
    }
}
