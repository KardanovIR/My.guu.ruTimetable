package ru.guu.my.myguuruclient.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by Инал on 14.03.2015.
 */
public class TimetableContract {
    public static final String REMOTE_BASE_URL = "http://my.guu.ru/";
    public static final String API_BASE_URL = REMOTE_BASE_URL + "/api/";
    public static final String API_PATH_USER = "users";
    public static final String API_DEFAULT_AVATAR_URL = "avatar.png";

    public static final String TOKEN_PARAM_NAME = "token";


    public static final String CONTENT_AUTHORITY = "ru.guu.my.myguuru";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CLASSES = "classes";
    public static final String PATH_PROFESSORS = "professors";


    public static final class ClassEntry implements BaseColumns {

        public static final String TABLE_NAME = "classes";


        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_FINISH_TIME = "finish_time";
        public static final String COLUMN_DAY_ABBR = "day_abbr";
        public static final String COLUMN_DAY_NAME = "day_name";
        public static final String COLUMN_DAY_NUMBER = "day_number";
        public static final String COLUMN_SUBJECT_REAL_NAME = "subject_real_name";
        public static final String COLUMN_PROFESSOR_ID = "professor_id";
        public static final String COLUMN_ROOM = "classroom";
        public static final String COLUMN_CLASS_NUMBER = "class_number";
        public static final String COLUMN_BUILDING_NUMBER = "building_id";
        public static final String COLUMN_FORMAT_NAME = "format_name";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASSES).build();

        public static Uri buildClassesUri() {
            return CONTENT_URI;
        }        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASSES;

        public static Uri buildClassesWithDate(String dayNumber, long date) {
            return CONTENT_URI.buildUpon().appendPath(dayNumber)
                    .appendPath(Long.toString(date)).build();
        }        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASSES;

        public static long getIdFromUri(Uri uri) {
            List<String> segs = uri.getPathSegments();
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getDayNumberFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static long getTimeFromUri(Uri uri) {
            long res = Long.parseLong(uri.getPathSegments().get(2));
            return res;
        }

        public static Uri buildClassUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }




    }

    public static final class ProfessorEntry implements BaseColumns {
        public static final String TABLE_NAME = "professors";


        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_MIDDLE_NAME = "middle_name";
        public static final String COLUMN_AD_LOGIN = "ad_login";
        public static final String COLUMN_ROLE = "role";
        public static final String COLUMN_AVATAR = "avatar";
        public static final String COLUMN_ORGANIZATIONAL_UNIT = "OU";
        public static final String COLUMN_FB_URL = "fb_url";
        public static final String COLUMN_OFFICE_TEL = "office_tel";
        public static final String COLUMN_ROOM = "room";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_USER_ID = "user_id";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROFESSORS).build();

        public static Uri buildProfessorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROFESSORS;

        public static long getProfessorIdFromUri(Uri uri) {
            List<String> segs = uri.getPathSegments();
            return Long.parseLong(uri.getPathSegments().get(1));
        }        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROFESSORS;






    }


}
