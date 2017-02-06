package de.doaktiv.fragments.calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.doaktiv.R;
import de.doaktiv.database.DatabaseManager;
import de.doaktiv.database.Image;
import de.doaktiv.database.Sponsor;
import de.doaktiv.fragments.AsyncImageView;

public class SponsorFragment extends Fragment {

    private Sponsor sponsor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.sponsor = DatabaseManager.INSTANCE.getSponsor(getArguments().getInt("sponsorId"));

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.sponsor_fragment, container, false);

        if (sponsor != null) {
            AsyncImageView sponsorImage = (AsyncImageView) layout.findViewById(R.id.sponsor_image);
            sponsorImage.setImageAsync(new Image(sponsor.getImagePath()));

            TextView sponsorName = (TextView) layout.findViewById(R.id.sponsor_name);
            sponsorName.setText(sponsor.getName());

            TextView sponsorWeb = (TextView) layout.findViewById(R.id.sponsor_web);
            sponsorWeb.setText(this.getResources().getString(R.string.website) + ": " + sponsor.getWeb());

            TextView sponsorMail = (TextView) layout.findViewById(R.id.sponsor_mail);
            sponsorMail.setText(this.getResources().getString(R.string.mail) + ": " + sponsor.getMail());

            TextView sponsorPhone = (TextView) layout.findViewById(R.id.sponsor_phone);
            sponsorPhone.setText(this.getResources().getString(R.string.phone) + ": " + sponsor.getPhone());

            TextView sponsorDescription = (TextView) layout.findViewById(R.id.sponsor_description);
            sponsorDescription.setText(sponsor.getDescription());
        }

        return layout;
    }

}
