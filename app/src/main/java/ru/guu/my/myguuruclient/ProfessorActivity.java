package ru.guu.my.myguuruclient;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.guu.my.myguuruclient.data.TimetableContract;
import ru.guu.my.myguuruclient.utilities.DownloadImageTask;


public class ProfessorActivity extends ActionBarActivity{
    private static final String[] __COLUMNS = {
            TimetableContract.ProfessorEntry.COLUMN_FIRST_NAME,
            TimetableContract.ProfessorEntry.COLUMN_LAST_NAME,
            TimetableContract.ProfessorEntry.COLUMN_MIDDLE_NAME,
            TimetableContract.ProfessorEntry.COLUMN_AVATAR,
            TimetableContract.ProfessorEntry.COLUMN_ROLE,
            TimetableContract.ProfessorEntry.COLUMN_ORGANIZATIONAL_UNIT,
            TimetableContract.ProfessorEntry.COLUMN_AD_LOGIN,
            TimetableContract.ProfessorEntry.COLUMN_FB_URL,
            TimetableContract.ProfessorEntry.COLUMN_OFFICE_TEL,
            TimetableContract.ProfessorEntry.COLUMN_ROOM,
            TimetableContract.ProfessorEntry.COLUMN_PHONE_NUMBER,
            TimetableContract.ProfessorEntry.TABLE_NAME + "." + TimetableContract.ClassEntry._ID
    };


    private static final int COL_FIRST_NAME = 0;
    private static final int COL_LAST_NAME = 1;
    private static final int COL_MIDDLE_NAME = 2;
    private static final int COL_AVATAR = 3;
    private static final int COL_ROLE = 4;
    private static final int COL_ORGANIZATIONAL_UNIT = 5;
    private static final int COL_AD_LOGIN = 6;
    private static final int COL_FB_URL = 7;
    private static final int COL_OFFICE_TEL = 8;
    private static final int COL_ROOM = 9;
    private static final int COL_PHONE_NUMBER = 10;
    private static final int COL_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(ProfessorFragment.PROFESSOR_URI, getIntent().getData());
            ProfessorFragment fragment = new ProfessorFragment();
            fragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.professor_container, fragment)
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_professor, menu);
        return true;
    }

    public static class ProfessorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {



        private TextView mFullNameTView;
        private TextView mEmailTView;
        private TextView mOUTView;
        private TextView mRoleTView;
        private TextView mFacebookTView;
        private TextView mOfficeTel;
        private TextView mRoom;
        private TextView mPhoneNumber;
        private ImageView mAvatarView;

        /*Optional data*/
        private LinearLayout mFacebookTViewLabel;
        private LinearLayout mOfficeTelLabel;
        private LinearLayout mRoomLabel;
        private LinearLayout mPhoneNumberLabel;

        /* Email field click */
        private LinearLayout mEmailLabel;


        public static final String PROFESSOR_URI = "PROFESSOR_URI";
        static final String NULL_STRING = "null";
        private Uri mUri;
        private int PROFESSOR_LOADER_ID = 6956;
        public ProfessorFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle arguments = getArguments();

            if (arguments != null) {
                mUri = arguments.getParcelable(ProfessorFragment.PROFESSOR_URI);
            }

            View rootView = inflater.inflate(R.layout.fragment_professor, container, false);


            mFullNameTView = (TextView) rootView.findViewById(R.id.professor_name_value);
            mEmailTView = (TextView) rootView.findViewById(R.id.professor_email_value);
            mOUTView = (TextView) rootView.findViewById(R.id.professor_organizational_unit_value);
            mRoleTView = (TextView) rootView.findViewById(R.id.professor_role_value);

            mFacebookTView = (TextView) rootView.findViewById(R.id.professor_facebook_value);
            mOfficeTel = (TextView) rootView.findViewById(R.id.professor_offile_tel_value);
            mRoom = (TextView) rootView.findViewById(R.id.professor_room_value);
            mPhoneNumber = (TextView) rootView.findViewById(R.id.professor_phone_number_value);
            mAvatarView = (ImageView) rootView.findViewById(R.id.professor_avatar);

            mFacebookTViewLabel = (LinearLayout) rootView.findViewById(R.id.professor_facebook_label);
            mOfficeTelLabel = (LinearLayout) rootView.findViewById(R.id.professor_offile_tel_label);
            mRoomLabel = (LinearLayout) rootView.findViewById(R.id.professor_room_label);
            mPhoneNumberLabel = (LinearLayout) rootView.findViewById(R.id.professor_phone_number_label);

            mEmailLabel = (LinearLayout) rootView.findViewById(R.id.professor_email_layout_label);

            mEmailLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ (String) mEmailTView.getText()});
                    startActivity(Intent.createChooser(intent, "Send email with"));
                }
            });

            mPhoneNumberLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mPhoneNumber.getText()));
                    startActivity(intent);
                }
            });

            mOfficeTelLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mOfficeTel.getText()));
                    startActivity(intent);
                }
            });

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            getLoaderManager().initLoader(PROFESSOR_LOADER_ID, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) {
                return;
            }

            String fullName = data.getString(COL_LAST_NAME) + " " + data.getString(COL_FIRST_NAME)  + " " + data.getString(COL_MIDDLE_NAME) ;
            String email = data.getString(COL_AD_LOGIN);
            String organizationalUnit = data.getString(COL_ORGANIZATIONAL_UNIT);
            String role = data.getString(COL_ROLE);
            String professorAvatar = data.getString(COL_AVATAR);

            String facebook = data.getString(COL_FB_URL);
            String officeTel = data.getString(COL_OFFICE_TEL);
            String room = data.getString(COL_ROOM);
            String phoneNumber = data.getString(COL_PHONE_NUMBER);

            mFullNameTView.setText(fullName);
            mEmailTView.setText(email);
            mOUTView.setText(organizationalUnit);
            mRoleTView.setText(role);

            if (isEmptyValueOrNull(facebook)){
                mFacebookTViewLabel.setVisibility(View.GONE);
            }else{
                mFacebookTView.setText(facebook);
            }
            if (isEmptyValueOrNull(officeTel)){
                mOfficeTelLabel.setVisibility(View.GONE);
            }else{
                mOfficeTel.setText(officeTel);
            }
            if (isEmptyValueOrNull(room)){
                mRoomLabel.setVisibility(View.GONE);
            }else{
                mRoom.setText(room);
            }
            if (isEmptyValueOrNull(phoneNumber)){
                mPhoneNumberLabel.setVisibility(View.GONE);
            }else{
                mPhoneNumber.setText(phoneNumber);
            }

            String filename = professorAvatar.substring(professorAvatar.lastIndexOf('/') + 1);

            if (filename.equals(TimetableContract.API_DEFAULT_AVATAR_URL)) {
                mAvatarView.setImageResource(R.drawable.ic_action_person);
            } else {
                new DownloadImageTask(mAvatarView)
                        .execute(TimetableContract.REMOTE_BASE_URL + professorAvatar);
            }
        }

        private static boolean isEmptyValueOrNull(String value){
            return value == null || value.isEmpty() || value.equals(NULL_STRING);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mUri != null) {
                return new CursorLoader(getActivity(), mUri, __COLUMNS, null, null, null);
            }
            return null;
        }
    }
}
