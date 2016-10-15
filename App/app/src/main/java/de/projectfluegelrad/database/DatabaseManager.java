package de.projectfluegelrad.database;

import android.view.View;

public class DatabaseManager {

    private View attachedView;

    private ConnectorTask connector;

    public DatabaseManager(View attachedView) {
        this.attachedView = attachedView;

        this.connector = new ConnectorTask(attachedView);

        connect();
    }

    private void connect() {
        connector.execute(new DatabaseAddress("pipigift.ddns.net", 3306, "fluegelrad"));
    }

}
