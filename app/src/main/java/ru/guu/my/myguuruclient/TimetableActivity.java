package ru.guu.my.myguuruclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.guu.my.myguuruclient.sync.TimetableSyncAdapter;


public class TimetableActivity extends ActionBarActivity implements TimetableFragment.Callback, DetailFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        mTwoPane = findViewById(R.id.detail_container) != null;
        if (mTwoPane) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            getSupportActionBar().setElevation(0f);
        }

        TimetableSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            MainActivity.logout(this);
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri classUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, classUri);

            DetailFragment frag = new DetailFragment();
            frag.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, frag, DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(classUri);
            startActivity(intent);
        }
    }


    @Override
    public void onProfessorClick(Uri professorUri) {
        Uri uri = professorUri;
        Intent intent = new Intent(this, ProfessorActivity.class)
                .setData(professorUri);
        startActivity(intent);
    }
}
