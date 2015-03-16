package ru.guu.my.myguuruclient.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Инал on 16.03.2015.
 */
public class TimetableAuthenticatorService extends Service {
    private TimetableAuthenticator mAuthenticator;

    public TimetableAuthenticatorService() {
        mAuthenticator = new TimetableAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
