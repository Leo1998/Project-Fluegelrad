package de.doaktiv.android.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.doaktiv.R;
import de.doaktiv.android.RootController;
import de.doaktiv.database.Image;
import de.doaktiv.database.Sponsor;

public class SponsorView extends RelativeLayout {

    private AsyncImageView sponsorImage;
    private TextView sponsorName;

    private Sponsor sponsor;

    public SponsorView(Context context, RootController rootController) {
        super(context);

        init(context, rootController);
    }

    public SponsorView(Context context, AttributeSet attrs, RootController rootController) {
        super(context, attrs);

        init(context, rootController);
    }

    private void init(Context context, final RootController rootController) {
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sponsor != null) {
                    rootController.openSponsorView(sponsor.getId());
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
