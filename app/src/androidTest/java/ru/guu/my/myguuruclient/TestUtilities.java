package ru.guu.my.myguuruclient;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import ru.guu.my.myguuruclient.data.TimetableContract;

/**
 * Created by Инал on 14.03.2015.
 */
public class TestUtilities extends AndroidTestCase {





    static ContentValues createDemoClass(long professorRowId) {
        ContentValues testValues = new ContentValues();
        testValues.put(TimetableContract.ClassEntry.COLUMN_SUBJECT_REAL_NAME, "IT management");
        testValues.put(TimetableContract.ClassEntry.COLUMN_BUILDING_NUMBER, 1);
        testValues.put(TimetableContract.ClassEntry.COLUMN_CLASS_NUMBER, 2);
        testValues.put(TimetableContract.ClassEntry.COLUMN_DAY_ABBR, "ПН");
        testValues.put(TimetableContract.ClassEntry.COLUMN_DAY_NAME, "Понедельник");
        testValues.put(TimetableContract.ClassEntry.COLUMN_DAY_NUMBER, 2);
        testValues.put(TimetableContract.ClassEntry.COLUMN_START_TIME, "08:15:00");
        testValues.put(TimetableContract.ClassEntry.COLUMN_FINISH_TIME, "08:15:00");
        testValues.put(TimetableContract.ClassEntry.COLUMN_PROFESSOR_ID, professorRowId);
        testValues.put(TimetableContract.ClassEntry.COLUMN_SUBJECT_REAL_NAME, "IT management");
        testValues.put(TimetableContract.ClassEntry.COLUMN_ROOM, "IT management");

        return testValues;
    }


    static ContentValues createDemoProfessor(){
        ContentValues testValues = new ContentValues();
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_AD_LOGIN, "test@guu.ru");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_AVATAR, "");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_FB_URL, "");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_FIRST_NAME, "Ivan");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_LAST_NAME, "Inalov");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_MIDDLE_NAME, "Ivanovich");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_OFFICE_TEL, "1023");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_ORGANIZATIONAL_UNIT, "ЦНИТ");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_PHONE_NUMBER, "+79687184939");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_ROLE, "Programmer");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_ROOM, "У505");
        testValues.put(TimetableContract.ProfessorEntry.COLUMN_USER_ID, 2040);
        return testValues;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
