package net.krisg.riseabove.jeepneys;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.util.Log;

import net.krisg.riseabove.jeepneys.data.JeepneysContract;
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
    ContentValues getPointContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(JeepneysContract.PointEntry.COLUMN_LATITUDE, 7.7);
        values.put(JeepneysContract.PointEntry.COLUMN_LONGITUDE, 125.33);
        return values;
    }

    public void testInsertReadDb()
    {
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = getPointContentValues();

        long pointRowId = -1;
        try
        {
            pointRowId = db.insertOrThrow(JeepneysContract.PointEntry.TABLE_NAME, null, values);
        }catch(SQLException e) // THIS IS android.database.SQLException, NOT the java.sql...
        {
            Log.d(LOG_TAG, e.getMessage());
        }
        assertTrue(pointRowId != -1);
        Log.d(LOG_TAG, "New row id: " + pointRowId);
    }
}