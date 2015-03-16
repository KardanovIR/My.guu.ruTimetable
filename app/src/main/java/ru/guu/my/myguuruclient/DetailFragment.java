package ru.guu.my.myguuruclient;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import ru.guu.my.myguuruclient.data.TimetableContract;

/**
 * Created by Инал on 15.03.2015.
 */


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    static final String DETAIL_URI = "URI";
    static final int COL_ID = 0;
    static final int COL_BUILDING_NUMBER = 1;
    static final int COL_DAY_NAME = 2;
    static final int COL_ROOM = 3;
    static final int COL_FINISH_TIME = 4;
    static final int COL_START_TIME = 5;
    static final int COL_SUBJECT_REAL_NAME = 6;
    static final int COL_DAY_ABBR = 7;
    static final int COL_DAY_NUMBER = 8;
    static final int COL_PROFESSOR_ID = 9;
    static final int COL_CLASS_NUMBER = 10;
    static final int COL_FORMAT_NAME = 11;
    static final int COL_FIRST_NAME = 12;
    static final int COL_LAST_NAME = 13;
    static final int COL_MIDDLE_NAME = 14;
    static final int COL_AVATAR = 15;
    static final int COL_ROLE = 16;
    static final int COL_ORGANIZATIONAL_UNIT = 17;
    private static final String SHARE_TAG = " #my.guu.ru";
    private static final int DETAIL_LOADER = 0;
    private static final String[] __COLUMNS = {
            TimetableContract.ClassEntry.TABLE_NAME + "." + TimetableContract.ClassEntry._ID,
            TimetableContract.ClassEntry.COLUMN_BUILDING_NUMBER,
            TimetableContract.ClassEntry.COLUMN_DAY_NAME,
            TimetableContract.ClassEntry.COLUMN_ROOM,
            TimetableContract.ClassEntry.COLUMN_FINISH_TIME,
            TimetableContract.ClassEntry.COLUMN_START_TIME,
            TimetableContract.ClassEntry.COLUMN_SUBJECT_REAL_NAME,
            TimetableContract.ClassEntry.COLUMN_DAY_ABBR,
            TimetableContract.ClassEntry.COLUMN_DAY_NUMBER,
            TimetableContract.ClassEntry.COLUMN_PROFESSOR_ID,
            TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER,
            TimetableContract.ClassEntry.COLUMN_FORMAT_NAME,
            TimetableContract.ProfessorEntry.COLUMN_FIRST_NAME,
            TimetableContract.ProfessorEntry.COLUMN_LAST_NAME,
            TimetableContract.ProfessorEntry.COLUMN_MIDDLE_NAME,
            TimetableContract.ProfessorEntry.COLUMN_AVATAR,
            TimetableContract.ProfessorEntry.COLUMN_ROLE,
            TimetableContract.ProfessorEntry.COLUMN_ORGANIZATIONAL_UNIT,
    };
    private static String mClassStr;
    ShareActionProvider mShareActionProvider;
    private Uri mUri;
    private ImageView mAvatarView;
    private TextView mSubjectNameTView;
    private TextView mDayNameTView;
    private TextView mTimeTView;
    private TextView mFormatNameTView;
    private TextView mClassroomTView;
    private LinearLayout mProfessorLayout;
    private TextView mProfessorName;
    private TextView mProfessorOUName;
    private TextView mProfessorRole;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mAvatarView = (ImageView) rootView.findViewById(R.id.detail_avatar);
        mSubjectNameTView = (TextView) rootView.findViewById(R.id.detail_subject_name_textview);
        mDayNameTView = (TextView) rootView.findViewById(R.id.detail_day_name_textview);
        mTimeTView = (TextView) rootView.findViewById(R.id.detail_time_textview);
        mFormatNameTView = (TextView) rootView.findViewById(R.id.detail_format_name_textview);
        mClassroomTView = (TextView) rootView.findViewById(R.id.detail_classroom_textview);
        mProfessorName = (TextView) rootView.findViewById(R.id.detail_professors_name);
        mProfessorOUName = (TextView) rootView.findViewById(R.id.detail_organizational_unit);
        mProfessorRole = (TextView) rootView.findViewById(R.id.detail_role);
        mProfessorLayout = (LinearLayout) rootView.findViewById(R.id.professor_layout);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    private Intent createShareActionIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mClassStr + SHARE_TAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mClassStr != null) {
            mShareActionProvider.setShareIntent(createShareActionIntent());
        } else {
            Log.d("", "Share provider is null");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String dayName = data.getString(COL_DAY_NAME);
        String subjectName = data.getString(COL_SUBJECT_REAL_NAME);
        String startTime = data.getString(COL_START_TIME);
        String finishTime = data.getString(COL_FINISH_TIME);
        String formatName = data.getString(COL_FORMAT_NAME);
        String classroom = data.getString(COL_ROOM);

        String professorName = data.getString(COL_LAST_NAME) + " " + data.getString(COL_FIRST_NAME) + " " + data.getString(COL_MIDDLE_NAME);
        String professorRole = data.getString(COL_ROLE);
        String professorOU = data.getString(COL_ORGANIZATIONAL_UNIT);
        String professorAvatar = data.getString(COL_AVATAR);

        mDayNameTView.setText(dayName);
        mSubjectNameTView.setText(subjectName);
        mTimeTView.setText(Utils.getTimeRange(startTime, finishTime));
        mFormatNameTView.setText(formatName);
        mClassroomTView.setText(classroom);
        mProfessorName.setText(professorName);
        mProfessorRole.setText(professorRole);
        mProfessorOUName.setText(professorOU);

        mProfessorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "hello", Toast.LENGTH_LONG).show();
            }
        });


        String filename = professorAvatar.substring(professorAvatar.lastIndexOf('/') + 1);

        if (filename.equals(TimetableContract.API_DEFAULT_AVATAR_URL)) {
            mAvatarView.setImageResource(R.drawable.ic_action_person);
        } else {
            new DownloadImageTask(mAvatarView)
                    .execute(TimetableContract.REMOTE_BASE_URL + professorAvatar);
        }

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareActionIntent());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, __COLUMNS, null, null, null);
        }
        return null;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = mAvatarView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Bitmap output = Bitmap.createBitmap(result.getWidth(),
                    result.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, result.getWidth(),
                    result.getHeight());
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(result.getWidth() / 2,
                    result.getHeight() / 2, result.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(result, rect, rect, paint);

            bmImage.setImageBitmap(output);
        }
    }
}