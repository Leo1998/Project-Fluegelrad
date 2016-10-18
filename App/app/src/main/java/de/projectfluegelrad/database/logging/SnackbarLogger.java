package de.projectfluegelrad.database.logging;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarLogger implements Logger {

    private View attachedView;

    public SnackbarLogger(View attachedView) {
        this.attachedView = attachedView;
    }

    @Override
    public void log(String msg) {
        Snackbar snackbar = Snackbar.make(attachedView, msg, 3000);
        snackbar.show();
    }
}
