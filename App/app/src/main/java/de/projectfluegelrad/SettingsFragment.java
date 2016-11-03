package de.projectfluegelrad;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.GnuGeneralPublicLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.settings_fragment, container, false);

        ((Button) layout.findViewById(R.id.open_licenses_button)).setOnClickListener(v -> {
            final Notices notices = new Notices();
            notices.addNotice(new Notice("MySQL Connector/J 5.1.40", "", "Copyright (C) 1989, 1991 Free Software Foundation, Inc.", new GnuGeneralPublicLicense20()));
            notices.addNotice(new Notice("JSON-java", "http://www.JSON.org/", "Copyright (c) 2002 JSON.org", new MITLicense()));

            new LicensesDialog.Builder(getContext())
                    .setNotices(notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show();
        });

        return layout;
    }

}
