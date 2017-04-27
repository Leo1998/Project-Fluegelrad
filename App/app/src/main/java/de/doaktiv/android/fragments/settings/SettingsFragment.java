package de.doaktiv.android.fragments.settings;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.BSD3ClauseLicense;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingsFragment extends DoaktivFragment {

    private LinearLayout layout;

    @Override
    public View createView(Context context) {
        this.layout = (LinearLayout) inflater().inflate(R.layout.settings_fragment, null, false);

        ((Button) layout.findViewById(R.id.show_licenses_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLicensesDialog();
            }
        });

        return layout;
    }

    private void launchLicensesDialog() {
        final Notices notices = new Notices();
        notices.addNotice(new Notice("JSON-java", "http://www.JSON.org/", "Copyright (c) 2002 JSON.org", new MITLicense()));

        notices.addNotice(new Notice("support-appcompat-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support-v4", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support-design", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support-cardview-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support-recyclerview-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support:preference-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));

        notices.addNotice(new Notice("osmdroid", "https://github.com/osmdroid/osmdroid", "", new ApacheSoftwareLicense20())); // wer ist der owner???
        notices.addNotice(new Notice("FloatingActionButton", "https://github.com/Clans/FloatingActionButton", "Copyright 2015 Dmytro Tarianyk", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("bottomsheet", "https://github.com/Flipboard/bottomsheet", "Copyright 2015 Flipboard", new BSD3ClauseLicense()));

        new LicensesDialog.Builder(layout.getContext())
                .setNotices(notices)
                .setIncludeOwnLicense(true)
                .setCloseText(R.string.notices_close)
                .build()
                .show();
    }
}
