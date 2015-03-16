package ru.guu.my.myguuruclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.guu.my.myguuruclient.data.TimetableContract;
import ru.guu.my.myguuruclient.utilities.ObscuredSharedPreferences;

/**
 * Created by Инал on 14.03.2015.
 */
public class AuthFragment extends Fragment {


    public static String PREFS_FILE_NAME = "prefs";
    public static final String TOKEN_STORAGE_KEY = "token";
    public static Button mScanQRButton = null;
    public static ProgressBar mAsyncSpinner = null;
    private static Activity mActiviy;

    public AuthFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mScanQRButton = (Button) rootView.findViewById(R.id.read_qr_button);
        mAsyncSpinner = (ProgressBar) rootView.findViewById(R.id.auth_check_loading);
        mScanQRButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), SimpleScannerActivity.class), 1);
            }
        });
        mActiviy = getActivity();
        checkTokenAsync();
        return rootView;
    }


    public static String getToken() {
        final ObscuredSharedPreferences obsSharedPrefs = new ObscuredSharedPreferences(mActiviy,
                mActiviy.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE));
        String token = obsSharedPrefs.getString(TOKEN_STORAGE_KEY, null);
        return token;
    }

    private void checkTokenAsync(){
        CheckToken checker = new CheckToken();
        checker.execute();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == getActivity().RESULT_OK){
                String result = data.getStringExtra("RESULT");
                final ObscuredSharedPreferences obsSharedPrefs = new ObscuredSharedPreferences(this.getActivity(),
                        this.getActivity().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE));
                obsSharedPrefs.edit().putString(TOKEN_STORAGE_KEY, result).commit();
                this.checkTokenAsync();
            }
            if (resultCode == this.getActivity().RESULT_CANCELED) {
                this.showErrorToast("QR scanning canceled");
            }
        }
    }

    private void activateQRButton() {
        mScanQRButton.setText("Scan QR to log in...");
        mScanQRButton.setEnabled(true);
        mScanQRButton.setClickable(true);
        mAsyncSpinner.setVisibility(View.INVISIBLE);
    }


    public void showErrorToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }


    public class CheckToken extends AsyncTask<Void, Void, Boolean> {

        private final String LOG_TAG = CheckToken.class.getSimpleName();

        protected Boolean getTokenStatusFromJSON(String JSONString) throws JSONException {
            final String STATUS_KEY = "status";
            try {
                JSONObject forecastJson = new JSONObject(JSONString);
                return forecastJson.getBoolean(STATUS_KEY);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String userInfoJSONStr;
            final String PATH_TO_ME = "me";

            try {
                Uri builtUri = Uri.parse(TimetableContract.API_BASE_URL)
                        .buildUpon()
                        .appendPath(TimetableContract.API_PATH_USER)
                        .appendPath(PATH_TO_ME)
                        .appendQueryParameter(TimetableContract.TOKEN_PARAM_NAME, getToken())
                        .build();

                Log.v(LOG_TAG, builtUri.toString());
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                userInfoJSONStr = buffer.toString();
                Log.v(LOG_TAG, userInfoJSONStr);
                try{
                    return getTokenStatusFromJSON(userInfoJSONStr);
                }catch(JSONException e){
                    Log.e(LOG_TAG, userInfoJSONStr);
                    return null;
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
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
        }

        protected void onPostExecute(Boolean result) {
            if (result != null) {
                if (result == true){
                    startActivity(new Intent(getActivity(), TimetableActivity.class));
                    getActivity().finish();
                }else{
                    if (!MainActivity.active){
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                    activateQRButton();
                }
            }else{
                showErrorToast("Connection error.");
                activateQRButton();
            }
        }
    }
}
