package ru.guu.my.myguuruclient;

/**
 * Created by Инал on 14.03.2015.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import ru.guu.my.myguuruclient.data.TimetableContract;
import ru.guu.my.myguuruclient.sync.TimetableSyncAdapter;

public class TimetableFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface Callback{
        public void onItemSelected(Uri classUri);
    }

    private TimetableAdapter mTTAdapter;
    private static final int TIMETABLE_LOADER_ID = 0;
    private int mPosition;
    private String mToken;
    private final String SELECTED_KEY = "LIST_VIEW_POSITION";
    private ListView mListView;

    private static final String[] __COLUMNS = {
            TimetableContract.ClassEntry.TABLE_NAME + "." + TimetableContract.ClassEntry._ID,
            TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER,
            TimetableContract.ClassEntry.COLUMN_BUILDING_NUMBER,
            TimetableContract.ClassEntry.COLUMN_FINISH_TIME,
            TimetableContract.ClassEntry.COLUMN_DAY_ABBR,
            TimetableContract.ClassEntry.COLUMN_DAY_NUMBER,
            TimetableContract.ClassEntry.COLUMN_PROFESSOR_ID,
            TimetableContract.ClassEntry.COLUMN_START_TIME,
            TimetableContract.ClassEntry.COLUMN_SUBJECT_REAL_NAME,
            TimetableContract.ClassEntry.COLUMN_ROOM,
            TimetableContract.ClassEntry.COLUMN_DAY_NAME,
            TimetableContract.ClassEntry.COLUMN_FORMAT_NAME,
    };

    static final int COL_CLASS_ID = 0;
    static final int COL_CLASS_NUMBER = 1;
    static final int COL_BUILDING_NUMBER = 2;
    static final int COL_FINISH_TIME = 3;
    static final int COL_DAY_ABBR = 4;
    static final int COL_DAY_NUMBER = 5;
    static final int COL_PROFESSOR_ID = 6;
    static final int COL_START_TIME = 7;
    static final int COL_SUBJECT_REAL_NAME = 8;
    static final int COL_ROOM = 9;
    static final int COL_DAY_NAME = 10;


    public TimetableFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTimetable();
    }

    private void updateTimetable() {
        TimetableSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String sortOrder = TimetableContract.ClassEntry.COLUMN_DAY_NUMBER + ", " + TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER + " ASC";

        Uri classesUri = TimetableContract.ClassEntry.buildClassesUri();
        Cursor cur = getActivity().getContentResolver().query(classesUri,
                null, null, null, sortOrder);
        mTTAdapter = new TimetableAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_timetable);
        mListView.setAdapter(mTTAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(TimetableContract.ClassEntry.buildClassUri(cursor.getLong(COL_CLASS_ID)));
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mToken = AuthFragment.getToken();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TIMETABLE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = TimetableContract.ClassEntry.COLUMN_DAY_NUMBER + ", " + TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER + " ASC";
        Uri classesUri = TimetableContract.ClassEntry.buildClassesUri();

        return new CursorLoader(getActivity(),
                classesUri,
                __COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTTAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION){
            mListView.setSelection(mPosition);
        }else{
            if (data.getCount() != ListView.INVALID_POSITION){
                mListView.setSelection(data.getCount());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTTAdapter.swapCursor(null);
    }
}