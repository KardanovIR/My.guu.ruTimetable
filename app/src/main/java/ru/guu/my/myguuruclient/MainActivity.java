package ru.guu.my.myguuruclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.guu.my.myguuruclient.utilities.ObscuredSharedPreferences;


public class MainActivity extends ActionBarActivity {
    static boolean active = true;

    public static void logout(Activity activity) {
        final ObscuredSharedPreferences obsSharedPrefs = new ObscuredSharedPreferences(activity,
                activity.getSharedPreferences(AuthFragment.PREFS_FILE_NAME, Context.MODE_PRIVATE));
        obsSharedPrefs.edit().putString(AuthFragment.TOKEN_STORAGE_KEY, "").commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AuthFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
