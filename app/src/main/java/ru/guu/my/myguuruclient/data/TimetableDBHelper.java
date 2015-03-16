package ru.guu.my.myguuruclient.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Инал on 14.03.2015.
 */
public class TimetableDBHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "timetable.db";
    private static final int DB_VERSION = 5;

    public TimetableDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PROFESSORS_TABLE = "CREATE TABLE " + TimetableContract.ProfessorEntry.TABLE_NAME + " (" +
                TimetableContract.ProfessorEntry._ID + " INTEGER PRIMARY KEY," +
                TimetableContract.ProfessorEntry.COLUMN_USER_ID + " INTEGER," +
                TimetableContract.ProfessorEntry.COLUMN_AD_LOGIN + " TEXT UNIQUE NOT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_LAST_NAME + " TEXT NOT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_MIDDLE_NAME + " TEXT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_ROLE + " TEXT NOT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_AVATAR + " TEXT NOT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_FB_URL + " TEXT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_OFFICE_TEL + " TEXT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_ORGANIZATIONAL_UNIT + " TEXT NOT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_PHONE_NUMBER + " TEXT NULL," +
                TimetableContract.ProfessorEntry.COLUMN_ROOM + " TEXT NULL" +
                "  );";

        final String SQL_CREATE_CLASSES_TABLE = "CREATE TABLE " + TimetableContract.ClassEntry.TABLE_NAME + " (" +
                TimetableContract.ClassEntry._ID + " INTEGER PRIMARY KEY," +
                TimetableContract.ClassEntry.COLUMN_SUBJECT_REAL_NAME + " TEXT NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_PROFESSOR_ID + " INTEGER NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_BUILDING_NUMBER + " INTEGER NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_DAY_ABBR + " TEXT NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_DAY_NAME + " TEXT NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_FINISH_TIME + " TEXT NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_START_TIME + " TEXT NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_FORMAT_NAME + " TEXT NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_DAY_NUMBER + " INTEGER NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER + " INTEGER NOT NULL," +
                TimetableContract.ClassEntry.COLUMN_ROOM + " TEXT NOT NULL," +
                " FOREIGN KEY (" + TimetableContract.ClassEntry.COLUMN_PROFESSOR_ID + ") REFERENCES " +
                TimetableContract.ProfessorEntry.TABLE_NAME + " (" + TimetableContract.ProfessorEntry._ID + "), " +
                " UNIQUE (" + TimetableContract.ClassEntry.COLUMN_DAY_NUMBER + ", " +
                TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_PROFESSORS_TABLE);
        db.execSQL(SQL_CREATE_CLASSES_TABLE);
    }


    /*
    * Update DB version
    * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TimetableContract.ClassEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TimetableContract.ProfessorEntry.TABLE_NAME);
        onCreate(db);
    }
}
