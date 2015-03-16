package ru.guu.my.myguuruclient.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Инал on 16.03.2015.
 */
public class TimetableSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static TimetableSyncAdapter sTimetableSyncAdapter = null;


    @Override
    public void onCreate() {
        Log.d("Timetable", "onCreate - TimetbleSyncService");
        synchronized (sSyncAdapterLock) {
            if (sTimetableSyncAdapter == null) {
                sTimetableSyncAdapter = new TimetableSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sTimetableSyncAdapter.getSyncAdapterBinder();
    }
}
