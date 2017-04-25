package de.doaktiv.android;

public interface RootController {

    DoaktivActivity getActivity();

    void openHome();

    void openCalendar();

    void openEventList();

    void openSettings();

    void openEventView(int eventId);

    void openParticipateView(int eventId);

    void openSponsorView(int sponsorId);

    boolean doSystemBack();

}
