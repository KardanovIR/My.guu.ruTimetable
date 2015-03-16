package ru.guu.my.myguuruclient.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Инал on 14.03.2015.
 */
public class TimetableProvider extends ContentProvider{


    static final int CLASSES = 100;
    static final int CLASSES_WITH_DAY_NUMBER = 101;
    static final int CLASSES_WITH_DAY_NUMBER_AND_TIME = 102;
    static final int CLASS_WITH_ID = 103;

    static final int PROFESSORS = 300;
    static final int PROFESSOR = 301;


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TimetableDBHelper mOpenHelper;


    private static final SQLiteQueryBuilder sQueryBuilder = new SQLiteQueryBuilder();

    static{
        sQueryBuilder.setTables(
                TimetableContract.ClassEntry.TABLE_NAME + " INNER JOIN " +
                        TimetableContract.ProfessorEntry.TABLE_NAME + " ON " +
                        TimetableContract.ClassEntry.TABLE_NAME + '.' + TimetableContract.ClassEntry.COLUMN_PROFESSOR_ID +
                        " = " + TimetableContract.ProfessorEntry.TABLE_NAME + "." + TimetableContract.ProfessorEntry._ID);
    }

    private static final String sProfessorSelection = TimetableContract.ProfessorEntry.TABLE_NAME
            + "." + TimetableContract.ProfessorEntry._ID + " = ?";


    private static final String sClassIdSelection = TimetableContract.ClassEntry.TABLE_NAME
            + "." + TimetableContract.ClassEntry._ID + " = ?";

    private static final String sClassesDayNumberSelection = TimetableContract.ClassEntry.TABLE_NAME
            + "." + TimetableContract.ClassEntry.COLUMN_DAY_NUMBER + " = ?";


    private static final String sClassesDayNumberAndTimeSelection = TimetableContract.ClassEntry.TABLE_NAME
            + "." + TimetableContract.ClassEntry.COLUMN_DAY_NUMBER + " = ? AND time(?) BETWEEN time(" +
            TimetableContract.ClassEntry.TABLE_NAME + "." + TimetableContract.ClassEntry.COLUMN_START_TIME + ") AND ("+
            TimetableContract.ClassEntry.TABLE_NAME + "." + TimetableContract.ClassEntry.COLUMN_FINISH_TIME + ") ";


    public Cursor getClassesByDayNumber(Uri uri, String[] projection, String sortOrder){
        String dayNumber = TimetableContract.ClassEntry.getDayNumberFromUri(uri);
        String[] selectionArgs;

        long time = TimetableContract.ClassEntry.getTimeFromUri(uri);
        if (time == 0){
            selectionArgs = new String[] {dayNumber};
        }else{
            selectionArgs = new String[] {dayNumber, Long.toString(time)};
        }
        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, sClassesDayNumberSelection, selectionArgs, null, null, sortOrder);
    }


    public Cursor getProfessor(Uri uri, String[] projection, String sortOrder){
        long professorId = TimetableContract.ProfessorEntry.getProfessorIdFromUri(uri);
        String[] selectionArgs = {Long.toString(professorId)};
        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, sProfessorSelection, selectionArgs, null, null, sortOrder);
    }

    public Cursor getClassWithId(Uri uri, String[] projection, String sortOrder){
        long classId = TimetableContract.ClassEntry.getIdFromUri(uri);
        String[] selectionArgs = {Long.toString(classId)};
        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, sClassIdSelection, selectionArgs, null, null, sortOrder);
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TimetableContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, TimetableContract.PATH_CLASSES, CLASSES);
        matcher.addURI(authority, TimetableContract.PATH_CLASSES + "/#", CLASS_WITH_ID);
        matcher.addURI(authority, TimetableContract.PATH_CLASSES + "/*/#", CLASSES_WITH_DAY_NUMBER);

        matcher.addURI(authority, TimetableContract.PATH_PROFESSORS, PROFESSORS);
        matcher.addURI(authority, TimetableContract.PATH_PROFESSORS + "/#", PROFESSOR);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new TimetableDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor resCursor;
        switch (sUriMatcher.match(uri)){
            case CLASS_WITH_ID:{
                resCursor = getClassWithId(uri, projection, sortOrder);
                break;
            }
            case CLASSES_WITH_DAY_NUMBER:{
                resCursor = getClassesByDayNumber(uri, projection, sortOrder);
                break;
            }
            case PROFESSOR:{
                resCursor = getProfessor(uri, projection, sortOrder);
                break;
            }
            case PROFESSORS:{
                resCursor = mOpenHelper.getReadableDatabase().query(
                        TimetableContract.ProfessorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            case CLASSES_WITH_DAY_NUMBER_AND_TIME:{
                resCursor = getClassesByDayNumber(uri, projection, sortOrder);
                break;
            }
            case CLASSES: {
                resCursor = mOpenHelper.getReadableDatabase().query(
                        TimetableContract.ClassEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("BAD URI. " + uri);
        }
        resCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match){
            case CLASS_WITH_ID:
                return TimetableContract.ClassEntry.CONTENT_ITEM_TYPE;
            case CLASSES_WITH_DAY_NUMBER:
                return TimetableContract.ClassEntry.CONTENT_TYPE;
            case CLASSES_WITH_DAY_NUMBER_AND_TIME:
                return TimetableContract.ClassEntry.CONTENT_ITEM_TYPE;
            case PROFESSOR:
                return TimetableContract.ProfessorEntry.CONTENT_TYPE;
            case PROFESSORS:
                return TimetableContract.ProfessorEntry.CONTENT_ITEM_TYPE;
            case CLASSES:
                return TimetableContract.ProfessorEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("BAD URI. " + uri);
        }
    }

    //TODO: Change to insert professors for each class
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri resUri;
        switch(match){
            case PROFESSORS:{
                long _id = db.insert(TimetableContract.ProfessorEntry.TABLE_NAME, null, values);
                if (_id != -1){
                    resUri = TimetableContract.ProfessorEntry.buildProfessorUri(_id);

                }else{
                    throw new SQLException("FAIL.CANT INSERT PROFESSOR");
                }
                break;
            }
            case CLASSES:{
                long _id = db.insert(TimetableContract.ClassEntry.TABLE_NAME, null, values);
                if (_id != -1){
                    resUri = TimetableContract.ClassEntry.buildClassUri(_id);

                }else{
                    throw new SQLException("FAIL.CANT INSERT PROFESSOR");
                }
                break;
            }
            default: throw new UnsupportedOperationException("");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return resUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        final int deletedRows;
        if (selection == null) selection = "1";
        switch (match){
            case CLASSES:{
                deletedRows = db.delete(TimetableContract.ClassEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case PROFESSORS:{
                deletedRows = db.delete(TimetableContract.ProfessorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:{
                throw new UnsupportedOperationException("BAD URI" + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updatedRows;

        switch (match) {
            case PROFESSORS:
                updatedRows = db.update(TimetableContract.ProfessorEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CLASSES:
                updatedRows = db.update(TimetableContract.ClassEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLASSES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TimetableContract.ClassEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
