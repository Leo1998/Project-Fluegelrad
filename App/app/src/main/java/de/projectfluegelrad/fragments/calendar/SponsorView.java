package de.projectfluegelrad.fragments.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Image;
import de.projectfluegelrad.database.Sponsor;

public class SponsorView extends RelativeLayout {

    private ImageHolder sponsorImage;
    private TextView sponsorName;

    public SponsorView(Context context) {
        super(context);

        init(context);
    }

    public SponsorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public SponsorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sponsor_small, this);

        this.sponsorImage = (ImageHolder) findViewById(R.id.sponsor_image);
        this.sponsorName = (TextView) findViewById(R.id.sponsor_name);
    }

    public void setSponsor(Sponsor sponsor) {
        if (sponsor != null) {
            sponsorImage.setImage(new Image(sponsor.getImage()));
            sponsorName.setText(sponsor.getName());
        }
    }
}
