package de.doaktiv.android.fragments.calendar;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.android.base.Toolbar;
import de.doaktiv.android.fragments.AsyncImageView;
import de.doaktiv.database.Database;
import de.doaktiv.database.Image;
import de.doaktiv.database.Sponsor;

public class SponsorFragment extends DoaktivFragment {

    private RelativeLayout layout;

    @Override
    public View createView(Context context) {
        this.layout = (RelativeLayout) inflater().inflate(R.layout.sponsor_fragment, null, false);

        return layout;
    }

    @Override
    public void onDatabaseReceived(final Database database) {
        if (this.getFragmentView() != null) {
            this.getFragmentView().post(new Runnable() {
                @Override
                public void run() {
                    Sponsor sponsor = database.getSponsor(getArguments().getInt("sponsorId"));

                    if (sponsor != null) {
                        AsyncImageView sponsorImage = (AsyncImageView) layout.findViewById(R.id.sponsor_image);
                        sponsorImage.setImageAsync(new Image(sponsor.getImagePath()));

                        Toolbar toolbar = SponsorFragment.this.getToolbar();
                        if (toolbar != null) {
                            toolbar.setTitleText(sponsor.getName());
                        }

                        TextView sponsorWeb = (TextView) layout.findViewById(R.id.sponsor_web);
                        sponsorWeb.setText(getResources().getString(R.string.website) + ": " + sponsor.getWeb());

                        TextView sponsorMail = (TextView) layout.findViewById(R.id.sponsor_mail);
                        sponsorMail.setText(getResources().getString(R.string.mail) + ": " + sponsor.getMail());

                        TextView sponsorPhone = (TextView) layout.findViewById(R.id.sponsor_phone);
                        sponsorPhone.setText(getResources().getString(R.string.phone) + ": " + sponsor.getPhone());

                        TextView sponsorDescription = (TextView) layout.findViewById(R.id.sponsor_description);
                        sponsorDescription.setText(sponsor.getDescription());
                    }
                }
            });
        }
    }
}
