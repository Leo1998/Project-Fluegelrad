package de.projectfluegelrad;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.settings_fragment, container, false);

        layout.findViewById(R.id.open_licenses_button).setOnClickListener(v -> {
            final Notices notices = new Notices();
            notices.addNotice(new Notice("JSON-java", "http://www.JSON.org/", "Copyright (c) 2002 JSON.org", new MITLicense()));

            new LicensesDialog.Builder(getContext())
                    .setNotices(notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show();
        });

        layout.findViewById(R.id.clear_cache_button).setOnClickListener(v -> {
            File dir = new File(this.getContext().getFilesDir(), "database");

            if (dir.exists()) {
                dir.delete();
                dir.mkdirs();
            }
        });

        return layout;
    }

}
