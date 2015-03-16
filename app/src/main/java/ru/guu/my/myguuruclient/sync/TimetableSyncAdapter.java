package ru.guu.my.myguuruclient.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import ru.guu.my.myguuruclient.AuthFragment;
import ru.guu.my.myguuruclient.R;
import ru.guu.my.myguuruclient.TimetableActivity;
import ru.guu.my.myguuruclient.data.TimetableContract;

public class TimetableSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int TIMETABLE_NOTIFICATION_ID = 60666;


    public static final String CLASS_QUERY_EXTRA = "cqe";
    private final String LOG_TAG = TimetableSyncAdapter.class.getSimpleName();

    public TimetableSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        TimetableSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {// Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private void notifyTimetableUpdated() {
        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        String prefShowNotificationsKey = context.getString(R.string.pref_show_notifications_key);
        boolean displayNotifications = prefs.getBoolean(prefShowNotificationsKey, Boolean.parseBoolean(context.getString(R.string.pref_show_notifications_default)));
        long lastSync = prefs.getLong(lastNotificationKey, 0);

        if (displayNotifications) {
            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {

                int iconId = R.mipmap.ic_launcher;
                String title = context.getString(R.string.app_name);
                String contentText = context.getString(R.string.format_notification);
                NotificationCompat.Builder notification =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(iconId)
                                .setContentText(title)
                                .setContentTitle(contentText);

                Intent resultIntent = new Intent(context, TimetableActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                notification.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(TIMETABLE_NOTIFICATION_ID, notification.getNotification());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(lastNotificationKey, System.currentTimeMillis());
                editor.commit();
            }
        }

}

    private long addProfessor(String adLogin, JSONObject professor) {
        long professorId;

        final String JSON_USER_ID = "user_id";
        final String JSON_AD_LOGIN = "ad_login";
        final String JSON_LAST_NAME = "last_name";
        final String JSON_FIRST_NAME = "first_name";
        final String JSON_MIDDLE_NAME = "middle_name";
        final String JSON_PHONE_NUMBER = "phone_number";
        final String JSON_OFFICE_TEL = "office_tel";
        final String JSON_ROLE = "role";
        final String JSON_AVATAR = "avatar";
        final String JSON_FB_URL = "fb_url";
        final String JSON_ORGANIZATIONAL_UNIT = "OU";
        final String JSON_ROOM = "room";

        Cursor profCursor = getContext().getContentResolver().query(
                TimetableContract.ProfessorEntry.CONTENT_URI,
                new String[]{TimetableContract.ProfessorEntry._ID},
                TimetableContract.ProfessorEntry.COLUMN_AD_LOGIN + " = ?",
                new String[]{adLogin},
                null
        );
        if (profCursor.moveToNext()) {
            int IdIndex = profCursor.getColumnIndex(TimetableContract.ProfessorEntry._ID);
            professorId = profCursor.getLong(IdIndex);
        } else {
            ContentValues values = new ContentValues();
            try {
                JSONArray organizationalUnits = professor.getJSONArray(JSON_ORGANIZATIONAL_UNIT);
                int OUCount = organizationalUnits.length() - 1;
                String organizationUnitName = organizationalUnits.getJSONObject(OUCount).getString("name");
                values.put(TimetableContract.ProfessorEntry.COLUMN_USER_ID, professor.getInt(JSON_USER_ID));
                values.put(TimetableContract.ProfessorEntry.COLUMN_AD_LOGIN, professor.getString(JSON_AD_LOGIN));
                values.put(TimetableContract.ProfessorEntry.COLUMN_ROLE, professor.getString(JSON_ROLE));
                values.put(TimetableContract.ProfessorEntry.COLUMN_AVATAR, professor.getString(JSON_AVATAR));
                values.put(TimetableContract.ProfessorEntry.COLUMN_LAST_NAME, professor.getString(JSON_LAST_NAME));
                values.put(TimetableContract.ProfessorEntry.COLUMN_FIRST_NAME, professor.getString(JSON_FIRST_NAME));
                values.put(TimetableContract.ProfessorEntry.COLUMN_MIDDLE_NAME, professor.getString(JSON_MIDDLE_NAME));
                values.put(TimetableContract.ProfessorEntry.COLUMN_ROOM, professor.getString(JSON_ROOM));
                values.put(TimetableContract.ProfessorEntry.COLUMN_PHONE_NUMBER, professor.getString(JSON_PHONE_NUMBER));
                values.put(TimetableContract.ProfessorEntry.COLUMN_ORGANIZATIONAL_UNIT, organizationUnitName);
                values.put(TimetableContract.ProfessorEntry.COLUMN_FB_URL, professor.getString(JSON_FB_URL));
                values.put(TimetableContract.ProfessorEntry.COLUMN_OFFICE_TEL, professor.getString(JSON_OFFICE_TEL));

                Uri insertedUri = getContext().getContentResolver().insert(
                        TimetableContract.ProfessorEntry.CONTENT_URI,
                        values
                );
                professorId = ContentUris.parseId(insertedUri);
            } catch (JSONException e1) {
                Log.d(LOG_TAG, "JSON PARSE ERROR" + e1);
                professorId = -1;
            }
        }
        return professorId;
    }

    private String[] formatResponseJSON(String JSONStr)
            throws JSONException {

        final String OBJ_ROOT_DATA = "data";
        final String ARR_TIMETABLE = "timetable";
        final String OBJ_PROFESSOR = "professor";
        final String STR_AD_LOGIN = "ad_login";


        final String STR_START_TIME = "start_time";
        final String STR_FINISH_TIME = "finish_time";
        final String STR_DAY_ABBR = "day_abbr";
        final String STR_DAY_NAME = "day_name";
        final String STR_DAY_NUMBER = "day_number";
        final String STR_SUBJECT_REAL_NAME = "subject_real_name";
        final String STR_ROOM = "room";
        final String STR_BUILDING_ID = "building_id";
        final String STR_CLASS_NUMBER = "class_number";
        final String STR_FORMAT_NAME = "format_name";


        try {
            JSONObject timetableJSON = new JSONObject(JSONStr);
            JSONObject data = timetableJSON.getJSONObject(OBJ_ROOT_DATA);
            JSONArray timetableArr = data.getJSONArray(ARR_TIMETABLE);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(timetableArr.length());

            for (int i = 0; i < timetableArr.length(); i++) {

                JSONObject item = timetableArr.getJSONObject(i);
                JSONObject professor = item.getJSONObject(OBJ_PROFESSOR);
                long professorId = addProfessor(professor.getString(STR_AD_LOGIN), professor);

                ContentValues values = new ContentValues();

                values.put(TimetableContract.ClassEntry.COLUMN_PROFESSOR_ID, professorId);
                values.put(TimetableContract.ClassEntry.COLUMN_ROOM, item.getString(STR_ROOM));
                values.put(TimetableContract.ClassEntry.COLUMN_SUBJECT_REAL_NAME, item.getString(STR_SUBJECT_REAL_NAME));
                values.put(TimetableContract.ClassEntry.COLUMN_FINISH_TIME, item.getString(STR_FINISH_TIME));
                values.put(TimetableContract.ClassEntry.COLUMN_START_TIME, item.getString(STR_START_TIME));
                values.put(TimetableContract.ClassEntry.COLUMN_DAY_NUMBER, item.getString(STR_DAY_NUMBER));
                values.put(TimetableContract.ClassEntry.COLUMN_BUILDING_NUMBER, item.getString(STR_BUILDING_ID));
                values.put(TimetableContract.ClassEntry.COLUMN_DAY_ABBR, item.getString(STR_DAY_ABBR));
                values.put(TimetableContract.ClassEntry.COLUMN_DAY_NAME, item.getString(STR_DAY_NAME));
                values.put(TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER, item.getString(STR_CLASS_NUMBER));
                values.put(TimetableContract.ClassEntry.COLUMN_FORMAT_NAME, item.getString(STR_FORMAT_NAME));
                cVVector.add(values);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(TimetableContract.ClassEntry.CONTENT_URI, cvArray);
                notifyTimetableUpdated();
            }

            String sortOrder = TimetableContract.ClassEntry.COLUMN_DAY_NUMBER + ", " + TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER + " ASC";
            Uri timetableUri = TimetableContract.ClassEntry.buildClassesUri();

            Cursor cur = getContext().getContentResolver().query(timetableUri,
                    null, null, null, sortOrder);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if (cur.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            Log.d(LOG_TAG, "FetchTask Complete. " + cVVector.size() + " Inserted");

            String[] resultStrs = new String[cVVector.size()];
            for (int i = 0; i < cVVector.size(); i++) {
                ContentValues values = cVVector.elementAt(i);
                resultStrs[i] = values.getAsString(TimetableContract.ClassEntry.COLUMN_SUBJECT_REAL_NAME) +
                        " - " +
                        values.getAsString(TimetableContract.ClassEntry.COLUMN_START_TIME) + "-" +
                        values.getAsString(TimetableContract.ClassEntry.COLUMN_FINISH_TIME) + " in " +
                        values.getAsString(TimetableContract.ClassEntry.COLUMN_BUILDING_NUMBER) + "-" +
                        values.getAsString(TimetableContract.ClassEntry.COLUMN_ROOM);
            }
            return resultStrs;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String tokenQuery = AuthFragment.getToken();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JSONStr;

        try {
            final String PATH_ME = "me";
            final String PATH_TIMETABLE = "timetable";

            Uri builtUri = Uri.parse(TimetableContract.API_BASE_URL).buildUpon()
                    .appendPath(TimetableContract.API_PATH_USER)
                    .appendPath(PATH_ME)
                    .appendPath(PATH_TIMETABLE)
                    .appendQueryParameter(TimetableContract.TOKEN_PARAM_NAME, tokenQuery)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            JSONStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            formatResponseJSON(JSONStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
