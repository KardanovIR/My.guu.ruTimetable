package ru.guu.my.myguuruclient;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import ru.guu.my.myguuruclient.data.TimetableContract;
import ru.guu.my.myguuruclient.data.TimetableDBHelper;

/**
 * Created by Инал on 14.03.2015.
 */
public class TestDB extends AndroidTestCase {

    public static final String LOG_TAG = TestDB.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(TimetableDBHelper.DB_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }


    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(TimetableContract.ClassEntry.TABLE_NAME);
        tableNameHashSet.add(TimetableContract.ProfessorEntry.TABLE_NAME);

        mContext.deleteDatabase(TimetableDBHelper.DB_NAME);
        SQLiteDatabase db = new TimetableDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: database was created without both tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + TimetableContract.ProfessorEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> professorColumnHashSet = new HashSet<String>();
        professorColumnHashSet.add(TimetableContract.ProfessorEntry._ID);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_AD_LOGIN);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_AVATAR);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_FB_URL);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_FIRST_NAME);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_LAST_NAME);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_MIDDLE_NAME);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_OFFICE_TEL);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_ORGANIZATIONAL_UNIT);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_PHONE_NUMBER);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_ROLE);
        professorColumnHashSet.add(TimetableContract.ProfessorEntry.COLUMN_ROOM);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            professorColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required columns",
                professorColumnHashSet.isEmpty());
        db.close();
    }

    public long testProfessorsTable(){
        TimetableDBHelper dbHelper = new TimetableDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createDemoProfessor();

        long professorRowId;
        professorRowId = db.insert(TimetableContract.ProfessorEntry.TABLE_NAME, null, testValues);

        assertTrue(professorRowId != -1);

        Cursor cursor = db.query(
                TimetableContract.ProfessorEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );


        assertTrue( "Error: No Records returned", cursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: Query Validation Failed",
                cursor, testValues);

        assertFalse( "Error: More than one record returned from query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
        return professorRowId;
    }

    public void testClassesTable(){

        TimetableDBHelper dbHelper = new TimetableDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long professorId = testProfessorsTable();
        ContentValues classValues = TestUtilities.createDemoClass(professorId);

        long classRowId = db.insert(TimetableContract.ClassEntry.TABLE_NAME, null, classValues);

        assertTrue(classRowId != -1);

        Cursor cursor = db.query(
                TimetableContract.ClassEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );


        assertTrue( "Error: No Records returned", cursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: Query Validation Failed",
                cursor, classValues);

        assertFalse( "Error: More than one record returned from query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
    }

}
