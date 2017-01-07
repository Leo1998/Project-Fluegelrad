package de.projectfluegelrad.fragments.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import de.projectfluegelrad.R;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.settings_fragment, container, false);

        layout.findViewById(R.id.open_licenses_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Notices notices = new Notices();
                notices.addNotice(new Notice("JSON-java", "http://www.JSON.org/", "Copyright (c) 2002 JSON.org", new MITLicense()));

                notices.addNotice(new Notice("support-appcompat-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("support-v4", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("support-design", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("support-cardview-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("support-recyclerview-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));

                notices.addNotice(new Notice("osmdroid", "https://github.com/osmdroid/osmdroid", "", new ApacheSoftwareLicense20())); // wer ist der owner???

                new LicensesDialog.Builder(SettingsFragment.this.getContext())
                        .setNotices(notices)
                        .setIncludeOwnLicense(true)
                        .build()
                        .show();
            }
        });

        return layout;
    }

}
