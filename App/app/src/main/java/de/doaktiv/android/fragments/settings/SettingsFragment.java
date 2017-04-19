package de.doaktiv.android.fragments.settings;


import de.doaktiv.android.base.DoaktivFragment;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingsFragment extends DoaktivFragment {

    /*@Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preferences);

        Preference openLicensesPreference = findPreference("open_licenses");
        openLicensesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                launchLicensesDialog();

                return true;
            }
        });

        Preference appVersionPreference = findPreference("app_version");
        try {
            PackageInfo info = this.getActivity().getPackageManager().getPackageInfo(this.getActivity().getPackageName(), 0);

            appVersionPreference.setSummary(info.versionName + " (versionCode: " + info.versionCode + ")");
        } catch (PackageManager.NameNotFoundException e) {
        }
    }*/

    public void launchLicensesDialog() {
        final Notices notices = new Notices();
        notices.addNotice(new Notice("JSON-java", "http://www.JSON.org/", "Copyright (c) 2002 JSON.org", new MITLicense()));

        notices.addNotice(new Notice("support-appcompat-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support-v4", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support-design", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support-cardview-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support-recyclerview-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("support:preference-v7", null, "Copyright (c) 2005 Android Open Source Project", new ApacheSoftwareLicense20()));

        notices.addNotice(new Notice("osmdroid", "https://github.com/osmdroid/osmdroid", "", new ApacheSoftwareLicense20())); // wer ist der owner???

        new LicensesDialog.Builder(SettingsFragment.this.getApplication())
                .setNotices(notices)
                .setIncludeOwnLicense(true)
                .build()
                .show();
    }

    @Override
    protected void onRefreshLayout() {

    }
}
