package net.krisg.riseabove.jeepneys;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

import net.krisg.riseabove.jeepneys.data.JeepneysDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    private static final String LOG_TAG = ApplicationTest.class.getSimpleName();

    public void testCreateDb() throws Throwable
    {
        mContext.deleteDatabase(JeepneysDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new JeepneysDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }
    // Make sure that everything in our content matches our insert
    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor)
    {
        Set<Map.Entry<String,Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String, Object> entry : valueSet)
        {
            String columnName = entry.getKey();
            int idx  = valueCursor.getColumnIndex(columnName);
            assertFalse( -1 == idx );
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }

    }
}