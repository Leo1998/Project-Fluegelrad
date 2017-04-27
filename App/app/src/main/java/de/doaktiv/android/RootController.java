package de.doaktiv.android;

public interface RootController {

    DatabaseService getDatabaseService();

    boolean doSystemBack();

    void openHome();

    void openCalendar();

    void openEventList();

    void openSettings();

    void openEventView(int eventId);

    void openParticipateView(int eventId);

    void openSponsorView(int sponsorId);

}
